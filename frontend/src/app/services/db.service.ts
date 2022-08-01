import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';


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
