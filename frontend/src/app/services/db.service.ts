import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Order } from './order.service';
import { Product, ProductType } from '../../../../node-test-wss/fastAPI-Types/index';






@Injectable({
  providedIn: 'root'
})
export class DbService {

  private dbUrl = 'http://tramiel.eoc.dlr.de:8000/api/v1';
  private apiKey = '';

  constructor(private http: HttpClient) {}

  setApiKey(key: string) {
    this.apiKey = key;
  }

  getApiKey(): string {
    return this.apiKey;
  }

  register(email: string, password: string) {
    return this.http.post(
      `${this.dbUrl}/users/register`,
      { email, password },
      {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        }
      }
    );
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
    if (this.apiKey === '') {
      throw new Error(`You need to login first`);
    }
    const headers: HttpHeaders = new HttpHeaders({
      'accept': 'application/json',
      'Content-Type': 'application/json',
      'X-APIKEY': this.apiKey
    });

    return this.http.post(`${this.dbUrl}/orders/`, order, { headers });
  }
  
  public getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.dbUrl}/products/`);
  }

  public getProductsTypes(): Observable<ProductType[]> {
    // https://medium.com/dailyjs/how-to-remove-array-duplicates-in-es6-5daa8789641c
    // return this.http.get<IProduct[]>(url).pipe(map(a => Array.from(new Set(a.map(p => p.wps_identifier)))));
    return this.http.get<ProductType[]>(`${this.dbUrl}/product-types/`);
  }

  public getProductsDerivedFrom(product: Product): Observable<Product[]> {
    return of([]);
  }
}
