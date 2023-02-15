import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { DbService } from '../db/db.service';
import { Consumer, Producer } from './pulsar';


@Injectable({
  providedIn: 'root'
})
export class PulsarService {

  private orders: Producer;

  constructor(
    private db: DbService,
  ) {
    this.orders = new Producer(`ws://${environment.queueUrl}/ws/v2/producer/persistent/public/default/new-order`);
  }

  public postOrder(order: UserOrder): Observable<boolean> {
    // Step 1: send order to database
    return this.db.placeOrder(order).pipe(
      // Step 2: after confirmation from db, notify pulsar of order-id
      switchMap(completeOrder => {
        const orderString = JSON.stringify({
          orderId: completeOrder.id
        });
        return this.orders.postMessage(orderString);
      }),

      // Step 3: after confirmation from pulsar, return true
      map(response => response.result === 'ok')
    );
  }

  ngOnDestroy() {
    this.orders.close();
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