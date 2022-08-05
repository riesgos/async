import { HttpClient, HttpHeaders } from '@angular/common/http';
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
  private apiKey = '';

  constructor(private http: HttpClient) { }

  setApiKey(key: string) {
    this.apiKey = key;
  }

  login(email: string, password: string) {
    return this.http.post(
      `${this.dbUrl}/users/login`,
      { email, password },
      {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        }
      }
    );
  }

  postOrder(order: Order): Observable<any> {
    const headers: HttpHeaders = new HttpHeaders({
      'accept': 'application/json',
      'Content-Type': 'application/json'
    });
    if (this.apiKey) headers.append('X-APIKEY', this.apiKey);

    return this.http.post(`${this.dbUrl}/orders/`, order, { headers });
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
