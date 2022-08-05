import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';


/**
 * Interface to the database
 */

/**
 * https://github1s.com/riesgos/async/blob/23354fb83b4342613e8b62a6d63117c99c30bad4/backend/catalog_app/schemas.py#L161-L162
 */
export interface IProduct {
  /** job.id */
  id: number;
  /** name and job.id */
  name: string;
  /** process.id */
  product_type_id: number;
}

/**
 * https://github1s.com/riesgos/async/blob/23354fb83b4342613e8b62a6d63117c99c30bad4/backend/catalog_app/schemas.py#L156-L157
 * https://github1s.com/riesgos/async/blob/23354fb83b4342613e8b62a6d63117c99c30bad4/backend/tests/test_routes/test_product_types.py#L25-L26
 */
export interface IProductType {
  /** process.id */
  id: number;
  /** wps name and output */
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
    let url = `${this.base}${serviceId}`
    if (serviceId.includes('http')) {
      url = serviceId;
    }
    return this.http.get<IProduct[]>(url);
  }

  public getProductsTypes(serviceId: string): Observable<IProductType[]> {
    // https://medium.com/dailyjs/how-to-remove-array-duplicates-in-es6-5daa8789641c
    // return this.http.get<IProduct[]>(url).pipe(map(a => Array.from(new Set(a.map(p => p.wps_identifier)))));

    let url = `${this.base}${serviceId}`
    if (serviceId.includes('http')) {
      url = serviceId;
    }
    return this.http.get<IProductType[]>(url);
  }

  public getProductsDerivedFrom(product: IProduct): Observable<IProduct[]> {
    return of([]);
  }
}
