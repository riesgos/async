import { Injectable } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';
import { UserSelfInformation } from 'src/app/backend_api/models';
import { environment } from 'src/environments/environment';
import { CredentialsError, DbService, isAuthenticationError, isSuccessfulAuthentication } from '../db/db.service';
import { LogsService } from '../logs/logs.service';
import { Consumer, Producer } from '../pulsar/pulsar';


@Injectable({
  providedIn: 'root'
})
export class BackendService {

  private orders = new Producer();
  constructor(private db: DbService, private logs: LogsService) {}
  
  public connect(email: string, password: string): Observable<UserSelfInformation | CredentialsError> {
    return this.db.login(email, password).pipe(
      mergeMap(results => {

        let queueConnection$ = of(false);
        let logConnection$ = of(false);
        if (isSuccessfulAuthentication(results)) {
          const queueIp = environment.queueUrl.replace('http://', '').replace('https://', '').replace(/\/$/, '');
          const queueAddress = `ws://${queueIp}/ws/v2/producer/persistent/public/default/new-order`;
          console.log(`Attempting to connect to queue at ${queueAddress}`);
          queueConnection$ = this.orders.connect(queueAddress);
          logConnection$ = this.logs.connect(email, password);
        }

        return forkJoin([of(results), queueConnection$, logConnection$]);
      }),
      
      map(([dbConnection, queueConnection]) => {
        return dbConnection;
      })
    );
  }

  public postOrder(order: UserOrder): Observable<boolean> {
    if (!this.orders.isConnected()) throw Error(`Cannot post order to queue: Connection to queue has not yet been established.`);
    if (!this.db.isLoggedIn()) throw Error(`Cannot post order to db: Connection to db has not yet been established.`);

    // Step 1: send order to database
    console.log("Sending order to db...", order);
    return this.db.placeOrder(order).pipe(
      // Step 2: after confirmation from db, notify pulsar of order-id
      switchMap(completeOrder => {
        const orderString = JSON.stringify({
          orderId: completeOrder.id
        });
        console.log("... completed sending order to db. Sending order to queue ...", orderString);
        return this.orders.postMessage(orderString);
      }),

      // Step 3: after confirmation from pulsar, return true
      map(response => response.result === 'ok')
    );
  }

  ngOnDestroy() {
    this.orders?.close();
  }
}

export type UserOrder = {
  order_constraints: ServiceConstraints
}

export type ServiceConstraints = {
  [constrainedService: string]: ParameterConstraints
}

export type ParameterConstraints = {
  literal_inputs?: LiteralParameterConstraints,
  bbox_inputs?: BboxParameterConstraints,
  complex_inputs?: ComplexParameterConstraints
}

export type LiteralParameterConstraints = {
  [parameterName: string]: LiteralInput[]
}

export type BboxParameterConstraints = {
  [parameterName: string]: BboxInput[]
}

export type ComplexParameterConstraints = {
  [parameterName: string]: ComplexInput[]
}


export type LiteralInput = string;

export function isLiteralInput(obj: any): obj is LiteralInput {
  return typeof obj === 'string' || obj instanceof String;
}

export type ComplexInput = {
  input_value: string,
  mime_type: 'application/vnd.geo+json' | 'application/xml'
  encoding: 'UTF-8',
  /** may be an empty string */
  xmlschema: string,
}

export function isComplexInput(obj: any): obj is ComplexInput {
  return (
      'input_value' in obj && 
      'mime_type' in obj && 
      'encoding' in obj && 
      'xmlschema' in obj
    );
}

export type BboxInput = {
  lower_corner_x: number;
  lower_corner_y: number;
  upper_corner_x: number;
  upper_corner_y: number;
  crs: string;
}

export function isBboxInput(obj: any): obj is BboxInput {
  return (
      'lower_corner_x' in obj && 
      'lower_corner_y' in obj && 
      'upper_corner_x' in obj && 
      'upper_corner_y' in obj && 
      'crs' in obj
    );
}