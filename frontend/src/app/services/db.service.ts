import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Order } from './order.service';



/**
 * Interface to the database
 */


@Injectable({
  providedIn: 'root'
})
export class DbService {

  private dbUrl = 'http://localhost:8000/api/v1';

  constructor(private http: HttpClient) { }

  postOrder(order: Order): Observable<any> {
    return this.http.post(`${this.dbUrl}/order`, order);
  }

  public getProducts(serviceId: string): Observable<Product[]> {
    return of([]);
  }

  public getProductsDerivedFrom(product: Product): Observable<Product[]> {
    return of([]);
  }
}

export interface Product {

}
