import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { DbService } from './db.service';
import { Producer } from './pulsar';


@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private pulsarProducer = new Producer('ws://tramiel.eoc.dlr.de:8080/ws/v2/producer/non-persistent/public/default/riesgos');

  constructor(private db: DbService) { }

  public postOrder(order: Order): Observable<boolean> {
    return this.db.postOrder(order).pipe(
      switchMap(completeOrder => {
        const orderString = JSON.stringify(completeOrder);
        return this.pulsarProducer.postMessage(orderString);
      }),
      map(response => response.result === 'ok')
    );
  }

  ngOnDestroy() {
    this.pulsarProducer.close();
  }
}

export interface Order {
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
