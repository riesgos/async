import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { DbService } from './db.service';
import { Consumer, Producer } from './pulsar';


@Injectable({
  providedIn: 'root'
})
export class PulsarService {

  // https://pulsar.apache.org/docs/next/client-libraries-websocket/
  // ws://broker-service-url:8080/ws/v2/producer/persistent/:tenant/:namespace/:topic
  // ws://broker-service-url:8080/ws/v2/consumer/persistent/:tenant/:namespace/:topic/:subscription
  private orders = new Producer('ws://tramiel.eoc.dlr.de:8080/ws/v2/producer/persistent/public/default/new-order');
  private shakygroundErrors = new Consumer('ws://tramiel.eoc.dlr.de:8080/ws/v2/consumer/persistent/public/default/shakyground-failure/shared');
  private deusErrors = new Consumer('ws://tramiel.eoc.dlr.de:8080/ws/v2/consumer/persistent/public/default/deus-failure/shared');

  constructor(private db: DbService) {
    this.shakygroundErrors.readMessages().subscribe(data => console.log(`Shakyground-Errors: `, data));
    this.deusErrors.readMessages().subscribe(data => console.log(`Deus-Errors: `, data));
  }

  public postOrder(order: UserOrder): Observable<boolean> {
    return this.db.postOrder(order).pipe(
      switchMap(completeOrder => {
        const orderString = JSON.stringify({
          orderId: completeOrder.id
        });
        return this.orders.postMessage(orderString);
      }),
      map(response => response.result === 'ok')
    );
  }

  ngOnDestroy() {
    this.orders.close();
  }
}

export interface UserOrder {
  order_constraints: ServiceConstraints
}

export interface ServiceConstraints {
  [constrainedService: string]: ParameterConstraints
}

export interface ParameterConstraints {
  literal_inputs?: {
    [parameterName: string]: string
  },
  bbox_inputs?: {
    [parameterName: string]: number[]
  },
  complex_inputs?: {
    [parameterName: string]: any
  }
}