import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';



/**
 * Interface to the database
 */


@Injectable({
  providedIn: 'root'
})
export class DbService {

  constructor() { }

  public getProducts(serviceId: string): Observable<Product[]> {
    return of([]);
  }

  public getProductsDerivedFrom(product: Product): Observable<Product[]> {
    return of([]);
  }
}

export interface Product {

}
