import { Injectable } from "@angular/core";
import { BehaviorSubject, forkJoin } from "rxjs";
import { CredentialsError, isAuthenticationError, isSuccessfulAuthentication } from "../db/db.service";
import { BackendService, UserOrder } from "../backend/backend.service";
import { UserSelfInformation } from "src/app/backend_api/models";
import { allTrue } from "../../utils/utils";
import { LocalstoreService } from "../localstore/localstore";



export interface AppState {
    authentication: 'none' | 'ongoing' | 'authenticated' | 'error',
    authenticationData: null | UserSelfInformation | CredentialsError,
    orderState: 'none' | 'sending' | 'accepted',
    localStoreData: { [key: string]: any }
}

const initialState: AppState = {
    authentication: 'none',
    authenticationData: null,
    orderState: 'none',
    localStoreData: {}
};


export interface AppStart {
    type: 'appStart'
}

export interface LoginAction {
    type: 'loginStart',
    payload: {
        email: string,
        password: string
    }
}

export interface LoginSuccessAction {
    type: 'loginSuccess',
    payload: UserSelfInformation
}

export interface LoginFailureAction {
    type: 'loginFailure',
    payload: CredentialsError
}

export interface RegisterAction {
    type: 'registerStart',
    payload: {
        email: string,
        password: string
    }
}

export interface RegisterSuccessAction {
    type: 'registerSuccess',
    payload: UserSelfInformation
}

export interface RegisterFailureAction {
    type: 'registerFailure',
    payload: CredentialsError
}

export interface OrderAction {
    type: 'orderStart',
    payload: UserOrder[]
}

export interface OrderSuccessAction {
    type: 'orderSuccess'
}

export interface OrderFailureAction {
    type: 'orderFailure'
}

export interface SaveToLocalStoreAction {
    type: 'saveToLocalStore',
    payload: { [key: string]: string }
}

export interface GetFromLocalStoreAction {
    type: 'getFromLocalStore',
    payload: string[]
}

export interface GetFromLocalStoreResultAction {
    type: 'getFromLocalStoreResult',
    payload: { [key: string]: string | null }
}

export type Action = AppStart |
                     LoginAction | LoginSuccessAction | LoginFailureAction |
                     RegisterAction | RegisterSuccessAction | RegisterFailureAction |
                     OrderAction | OrderSuccessAction | OrderFailureAction |
                     SaveToLocalStoreAction | GetFromLocalStoreAction | GetFromLocalStoreResultAction;



@Injectable({
    providedIn: 'root'
})
export class AppStateService {

    public state = new BehaviorSubject<AppState>(initialState);

    constructor(private backend: BackendService, private local: LocalstoreService) {}

    public action(action: Action) {
        this.fireOffSideEffects(action);
        const newState = this.reduceState(action, this.state.value);
        this.state.next(newState);
        console.log(`Handled action of type ${action.type}. New state: `, newState);
    }




    private fireOffSideEffects(action: Action) {

        if (action.type === 'appStart') {
            const localData = this.local.getAll([]);
            this.action({
                type: 'getFromLocalStoreResult',
                payload: localData
            });
        }


        if (action.type === 'saveToLocalStore') {
            const data = action.payload;
            this.local.saveAll(data);
        }
        if (action.type === 'getFromLocalStore') {
            const data = action.payload;
            const results = this.local.getAll(data);
            this.action({
                type: 'getFromLocalStoreResult',
                payload: results
            });
        }


        if (action.type === 'loginStart') {
            const creds = action.payload;
            this.backend.connect(creds.email, creds.password).subscribe(results => {
                if (isSuccessfulAuthentication(results)) {
                    this.action({
                        type: 'loginSuccess',
                        payload: results
                    });
                    this.action({
                        type: 'saveToLocalStore',
                        payload: { email: creds.email, password: creds.password }
                    });
                } else if (isAuthenticationError(results)) {
                    this.action({
                        type: 'loginFailure',
                        payload: results
                    });
                }
            }, error => {
                this.action({
                    type: 'loginFailure',
                    payload: error
                })
            });
        }

        if (action.type === 'orderStart') {
            const orders = action.payload;
            const processes$ = orders.map(o => this.backend.postOrder(o));
            forkJoin(processes$).subscribe(successes => {
                if (allTrue(successes)) {
                    this.action({
                        type: 'orderSuccess'
                    });
                } else {
                    this.action({
                        type: 'orderFailure'
                    });
                }
            }); 
        }

    }




    private reduceState(action: Action, currentState: AppState): AppState {

        if (action.type === 'getFromLocalStoreResult') {
            currentState.localStoreData = action.payload;
        }

        if (action.type === 'loginStart' || action.type === 'registerStart') {
            currentState.authentication = 'ongoing';
        }
        if (action.type === 'loginSuccess' || action.type === 'registerSuccess') {
            currentState.authentication = 'authenticated';
            currentState.authenticationData = action.payload;
        }
        if (action.type === 'loginFailure' || action.type === 'registerFailure') {
            currentState.authentication = 'error';
            currentState.authenticationData = action.payload;
        }

        if (action.type === 'orderStart') {
            currentState.orderState = 'sending';
        }
        if (action.type === 'orderSuccess') {
            currentState.orderState = 'accepted';
        }
        if (action.type === 'orderFailure') {
            currentState.orderState = 'none';
        }

        return currentState;
    }
}