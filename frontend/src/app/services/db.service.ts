import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { UserOrder } from './pulsar.service';
import { Product, ProductType } from '../../../../node-test-wss/fastAPI-Types/index';
import { map, tap } from 'rxjs/operators';






@Injectable({
  providedIn: 'root'
})
export class DbService {

  private dbUrl = 'http://tramiel.eoc.dlr.de:8000/api/v1';
  private apiKey = '';

  constructor(private http: HttpClient) {}

  getApiKey(): string {
    return this.apiKey;
  }

  
  setApiKey(apiKey: string) {
    this.apiKey = apiKey;
  }

  register(email: string, password: string) {
    return this.http.post<User>(
      `${this.dbUrl}/users/register`,
      { email, password },
      {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        }
      }
    ).pipe(tap((user: any) => {
      this.apiKey = user.apikey;
    }));
  }

  login(email: string, password: string) {
    return this.http.post<User>(
      `${this.dbUrl}/users/login`,
      { email, password },
      {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        }
      }
    ).pipe(tap((user: any) => {
      this.apiKey = user.apikey;
    }));
  }

  getUser(id: number): Observable<User> {
    return this.get<User>(`users/${id}`);
  }

  getJobs(): Observable<Job[]> {
    return this.get<Job[]>(`jobs`);
  }

  getOrders(): Observable<Order[]> {
    return this.get<Order[]>(`orders`);
  }

  getProcesses(): Observable<Process[]> {
    return this.get<Process[]>(`processes`);
  }

  postOrder(order: UserOrder): Observable<any> {
    return this.post(`orders`, order);
  }
  
  public getProducts(): Observable<Product[]> {
    return this.get<Product[]>(`products`);
  }

  public getProductsTypes(): Observable<ProductType[]> {
    return this.get<ProductType[]>(`product-types`);
  }

  public getProductsDerivedFrom(product: Product): Observable<Product[]> {
    return of([]);
  }

  private get<T>(path: string): Observable<T> {
    if (!this.apiKey) throw new Error(`Need to sign in first`);
    const headers: HttpHeaders = new HttpHeaders({
      'accept': 'application/json',
      'Content-Type': 'application/json',
      'X-APIKEY': this.apiKey
    });
    return this.http.get<T>(`${this.dbUrl}/${path}`, { headers });
  }

  private post<T>(path: string, body: any): Observable<T> {
    if (!this.apiKey) throw new Error(`Need to sign in first`);
    const headers: HttpHeaders = new HttpHeaders({
      'accept': 'application/json',
      'Content-Type': 'application/json',
      'X-APIKEY': this.apiKey
    });
    return this.http.post<T>(`${this.dbUrl}/${path}`, body, { headers });
  }
}


export interface User {
  id: number,
  email: string,
  apikey: string
};


export interface Job {
  id: number,
  process_id: string,
  status: string
};


export interface Order {
  id: number,
  user_id: number,
  order_constraints: any
};

export interface Process {
  id: number;
  wps_url: string;
  wps_identifier: string;
};