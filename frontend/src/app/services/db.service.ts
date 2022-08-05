import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Order } from './order.service';



/**
 * Interface to the database
 */

/**
 * .../api/complex-outputs
 */
export interface IProduct {
  id: number;
  job_id: number;
  job: string;
  wps_identifier: string;
  link: string;
  mime_type: string;
  xmlschema: string;
  encoding: string;
  inputs: string;

  name: string;
}


@Injectable({
  providedIn: 'root'
})
export class DbService {
  port = 8080;
  base = `http://localhost:${this.port}`;
  constructor(private http: HttpClient) { }

  private dbUrl = 'http://tramiel.eoc.dlr.de:8000/api/v1';
  private apiKey = '';

  constructor(private http: HttpClient) { }

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
  
  public getProducts(serviceId: string): Observable<IProduct[]> {
    return this.http.get<IProduct[]>(`${this.base}${serviceId}`);
  }

  public getProductsTypes(serviceId: string): Observable<IProduct['wps_identifier'][]> {
    // https://medium.com/dailyjs/how-to-remove-array-duplicates-in-es6-5daa8789641c
    return this.http.get<IProduct[]>(`${this.base}${serviceId}`).pipe(map(a => Array.from(new Set(a.map(p => p.wps_identifier)))));
  }

  public getProductsDerivedFrom(product: IProduct): Observable<IProduct[]> {
    return of([]);
  }
}
