import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, merge, Observable, of, zip } from 'rxjs';
import { UserOrder } from './pulsar.service';
import { ComplexOutput, Product, ProductType } from '../../../../node-test-wss/fastAPI-Types/index';
import { map, mergeAll, switchMap, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';






@Injectable({
  providedIn: 'root'
})
export class DbService {

  private dbUrl = environment.backend + '/api/v1';
  private apiKey = '';

  constructor(private http: HttpClient) {}

  public getApiKey(): string {
    return this.apiKey;
  }
  
  public setApiKey(apiKey: string) {
    this.apiKey = apiKey;
  }

  public register(email: string, password: string) {
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

  public login(email: string, password: string) {
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

  public getUser(id: number): Observable<User> {
    return this.get<User>(`users/${id}`);
  }

  public getJobs(): Observable<Job[]> {
    return this.get<Job[]>(`jobs`);
  }

  public getOrders(): Observable<Order[]> {
    return this.get<Order[]>(`orders`);
  }

  public getProcesses(): Observable<Process[]> {
    return this.get<Process[]>(`processes`);
  }

  public postOrder(order: UserOrder): Observable<any> {
    return this.post(`orders`, order);
  }

  public resolveProduct(productId: number) {
    return this.get<ComplexOutput>(`complex-outputs/${productId}`);
  }
  
  public getProducts(): Observable<Product[]> {
    return this.get<Product[]>(`products`);
  }

  public getProduct(productId: number): Observable<Product> {
    return this.get<Product>(`products/${productId}`);
  }

  public getProductsDerivedFrom(productId: number) {
    return this.get<Product[]>(`products/${productId}/derived-products`).pipe(
      switchMap((products: Product[]) => {
        const resolved$ = products.map(p => this.resolveProduct(p.id));
        return zip(...resolved$);
      })
    );
  }

  public getBaseProducts(productId: number) {
    return this.get<Product[]>(`products/${productId}/base-products`).pipe(
      switchMap((products: Product[]) => {
        const resolved$ = products.map(p => this.resolveProduct(p.id));
        return zip(...resolved$);
      })
    );
  }
  public getProductsTypes(): Observable<ProductType[]> {
    return this.get<ProductType[]>(`product-types`);
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
