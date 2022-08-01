import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Producer } from './pulsar';


@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private pulsarProducer = new Producer('ws', 'localhost', 8080, 'non-persistent', "public", 'default', 'riesgos');
  // private pulsarProducer = new Producer('ws', 'rz-vm154.gfz-potsdam.de', 8082, 'non-persistent', 'digital-earth', 'riesgos');

  constructor() { }

  public postOrder(order: Order): Observable<boolean> {
    const orderString = JSON.stringify(order);
    return this.pulsarProducer.postMessage(orderString).pipe(map(response => {
      return response.result === 'ok';
    }));
  }

  ngOnDestroy() {
    this.pulsarProducer.close();
  }
}

export interface Order {
  constraints: ServiceConstraints
}

export interface ServiceConstraints {
  [constrainedService: string]: ParameterConstraints
}

export interface ParameterConstraints {
  [parameterName: string]: any[]
}
