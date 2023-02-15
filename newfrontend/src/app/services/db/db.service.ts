import { Injectable } from "@angular/core";
import { catchError, defaultIfEmpty, forkJoin, map, Observable, of, switchMap, tap } from "rxjs";
import { ComplexOutput, Job, Order, Process, Product, ProductType, UserSelfInformation } from "src/app/backend_api/models";
import { ApiService } from "src/app/backend_api/services";
import { UserOrder } from "../pulsar/pulsar.service";


export interface ProductInfo {
    orderId: number,
    processId: string,
    paraId: string,
    link: string,
    complexOutputId: number,
    baseProducts: number[],
    derivedProducts: number[]
}

export interface CredentialsError {
    url: string,
    errorMessage: string,
    errorDetails: any
}

export function isSuccessfulAuthentication(result: any): result is UserSelfInformation {
    return 'email' in result && 'id' in result;
}

export function isAuthenticationError(result: any): result is CredentialsError {
    return !isSuccessfulAuthentication(result); // 'url' in result && 'status' in result && 'statusText' in result && 'error' in result;
}

@Injectable({
    providedIn: 'root'
  })
  export class DbService {
    private userSelfInformation: UserSelfInformation | undefined;

    constructor(private apiSvc: ApiService) {
    }

    public register(email: string, password: string): Observable<UserSelfInformation | CredentialsError> {
        return this.apiSvc.registerUserApiV1UsersRegisterPost({
            body: { email, password }
        }).pipe(
            tap(result => this.userSelfInformation = result),
            map(result => {
                console.log('registered: ', result);
                return result;
            }),
            catchError(error => {
                console.log(`Error on registration: `, error);
                const errMsg = {
                    url: error.url,
                    errorMessage: error.message,
                    errorDetails: error.error
                };
                return of(errMsg);
            }),
        )
    }

    public login(email: string, password: string): Observable<UserSelfInformation | CredentialsError> {
      return this.apiSvc.loginUserApiV1UsersLoginPost({
        body: { email, password }
      }).pipe(
        tap(result => this.userSelfInformation = result),
        map(result => {
            console.log('login:', result)
            return result;
        }),
        catchError(error => {
            console.log(`Error logging in: `, error);
            const errMsg = {
                url: error.url,
                errorMessage: error.message,
                errorDetails: error.error
            };
            return of(errMsg);
        }),
      )
    }

    public isLoggedIn() {
        return !!this.userSelfInformation;
    }

    placeOrder(order: UserOrder) {
        return this.apiSvc.createOrderApiV1OrdersPost({
            "x-apikey": this.userSelfInformation?.apikey,
            body: order
        })
    }

    getProductTypes() {
        return this.apiSvc.readListApiV1ProductTypesGet().pipe(
            switchMap((productTypes: ProductType[]) => {
                
                const tasks$: Observable<ProductType>[] = [];
                for (const productType of productTypes) {
                  const details$ = this.apiSvc.readDetailApiV1ProductTypesProductTypeIdGet({ product_type_id: productType.id });
                  tasks$.push(details$);
                }
                
                return forkJoin(tasks$).pipe(defaultIfEmpty([]));
            })
        );
    }

    getProducts() {
        const jobs$ = this.apiSvc.readListApiV1JobsGet({ status: 'Succeeded' });
        const processes$ = this.getProcesses();
        const ojr$ = this.apiSvc.readListApiV1OrderJobRefsGet();
        const complexOutputs$ = this.apiSvc.readListApiV1ComplexOutputsGet();

        return forkJoin([jobs$, processes$, ojr$, complexOutputs$]).pipe(
            map(([jobs, processes, ojr, complexOutputs]) => {
                const output: ProductInfo[] = [];
                for (const job of jobs) {

                    const orderId = ojr.find(e => e.job_id === job.id)?.order_id;
                    const processId = processes.find(p => p.id === job.process_id)?.wps_identifier;
                    const complexOutput = complexOutputs.find(c => c.job_id === job.id);
                    if (!orderId || !processId || !complexOutput) continue;
                    const paraId = complexOutput.wps_identifier;
                    const link = complexOutput.link;
                    const complexOutputId = complexOutput.id;

                    const productData: ProductInfo = {
                        orderId: orderId,
                        processId: processId,
                        paraId: paraId,
                        link: link,
                        complexOutputId: complexOutputId,
                        baseProducts: [],
                        derivedProducts: []
                    };
                    output.push(productData);
                }
                return output;
            }),
            switchMap(data => {
                const tasks$: Observable<ProductInfo>[] = [];
                for (const datum of data) {
                    const baseProds$ = this.apiSvc.readBaseProductsApiV1ProductsProductIdBaseProductsGet({ product_id: datum.complexOutputId });
                    const derivedProds$ = this.apiSvc.readDervicedProductsApiV1ProductsProductIdDerivedProductsGet({ product_id: datum.complexOutputId });
                    const subTask$ = forkJoin([baseProds$, derivedProds$]).pipe(
                        map(([baseProds, derivedProds]) => {
                            datum.baseProducts = baseProds.map(p => p.id);
                            datum.derivedProducts = derivedProds.map(p => p.id);
                            return datum;
                        })
                    )
                    tasks$.push(subTask$);
                }
                return forkJoin(tasks$).pipe(defaultIfEmpty([]));
            })
        );
    }

//     getProductsOld() {
//         const products$ = this.apiSvc.readListApiV1ProductsGet();
//         const complexOutputs$ = this.apiSvc.readListApiV1ComplexOutputsGet();

//         return forkJoin([products$, complexOutputs$]).pipe(
//             switchMap(([products, complexOutputs]) => {
// console.log(complexOutputs)
//                 const tasks$: Observable<ProductInformation>[] = [];
//                 for (const product of products) {
//                     const details$ = this.apiSvc.readDetailApiV1ProductsProductIdGet({ product_id: product.id });
//                     const baseProds$ = this.apiSvc.readBaseProductsApiV1ProductsProductIdBaseProductsGet({ product_id: product.id });
//                     const derivedProds$ = this.apiSvc.readDervicedProductsApiV1ProductsProductIdDerivedProductsGet({ product_id: product.id });
//                     const task$: Observable<ProductInformation> = forkJoin([details$, baseProds$, derivedProds$, complexOutputs$]).pipe(map(([details, base, derived]) => {
//                         const productInfo: ProductInformation = {
//                             product: details,
//                             inputs: base.map(p => `${p.name} (${p.id})`),
//                             derived: derived.map(p => `${p.name} (${p.id})`),
//                         };


//                         // @TODO: pretty sure that this is not the correct kind of matching.
//                         const complexOutput = complexOutputs.find(co => co.job_id === product.id);
//                         if (complexOutput) {
//                             productInfo.link = complexOutput.link;
//                         }

//                         return productInfo;
//                     }));
//                     tasks$.push(task$);
//                 }

//                 return forkJoin(tasks$).pipe(defaultIfEmpty([]));
//             })
//         );
//     }
  
    getProcesses() {
        return this.apiSvc.readListApiV1ProcessesGet().pipe(
            switchMap((processes: Process[]) => {
                const tasks$: Observable<Process>[] = [];
                for (const process of processes) {
                    const details$ = this.apiSvc.readDetailApiV1ProcessesProcessIdGet({ process_id: process.id });
                    tasks$.push(details$);
                }

                return forkJoin(tasks$).pipe(defaultIfEmpty([]));
            })
        );
    }
    
    getJobs() {
        // Jobs: small parts of orders. Every job is associated with one process, one set of inputs and one set of outputs
        return this.apiSvc.readListApiV1JobsGet().pipe(
            switchMap((jobs: Job[]) => {

                const tasks$: Observable<Job>[] = [];
                for (const job of jobs) {
                    const details$ = this.apiSvc.readDetailApiV1JobsJobIdGet({ job_id: job.id });
                    tasks$.push(details$);
                }
                
                return forkJoin(tasks$).pipe(defaultIfEmpty([]));
            })
        );
    }
  
    getOrders() {
      return this.apiSvc.readListApiV1OrdersGet().pipe(
        switchMap((orders: Order[]) => {
            const tasks$: Observable<Order>[] = [];
            for (const order of orders) {
                const details$ = this.apiSvc.readDetailApiV1OrdersOrderIdGet({ order_id: order.id });
                tasks$.push(details$);
            }

            return forkJoin(tasks$).pipe(defaultIfEmpty([]));
        })
      );
    }
}