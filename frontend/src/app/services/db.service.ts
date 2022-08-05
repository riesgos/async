import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
// import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { Product, ProductType } from '../../../../node-test-wss/fastAPI-Types/index';






@Injectable({
  providedIn: 'root'
})
export class DbService {
  port = 8080;
  base = `http://localhost:${this.port}`;
  constructor(private http: HttpClient) { }

  public getProducts(serviceId: string): Observable<Product[]> {
    let url = `${this.base}${serviceId}`
    if (serviceId.includes('http')) {
      url = serviceId;
    }
    return this.http.get<Product[]>(url);
  }

  public getProductsTypes(serviceId: string): Observable<ProductType[]> {
    // https://medium.com/dailyjs/how-to-remove-array-duplicates-in-es6-5daa8789641c
    // return this.http.get<IProduct[]>(url).pipe(map(a => Array.from(new Set(a.map(p => p.wps_identifier)))));

    let url = `${this.base}${serviceId}`
    if (serviceId.includes('http')) {
      url = serviceId;
    }
    return this.http.get<ProductType[]>(url);
  }

  public getProductsDerivedFrom(product: Product): Observable<Product[]> {
    return of([]);
  }
}
