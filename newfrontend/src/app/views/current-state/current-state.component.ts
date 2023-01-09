import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, forkJoin, map, Observable, of } from 'rxjs';
import { Job, Order, Process, Product, ProductType } from 'src/app/backend_api/models';
import { ApiService } from 'src/app/backend_api/services';

@Component({
  selector: 'app-current-state',
  templateUrl: './current-state.component.html',
  styleUrls: ['./current-state.component.css']
})
export class CurrentStateComponent implements OnInit {

  public productTypes$   = new BehaviorSubject<ProductType[]>([]);
  public products$       = new BehaviorSubject<{product: Product, baseProducts: Product[], derivedProducts: Product[]}[]>([]);
  public processes$      = new BehaviorSubject<Process[]>([]);
  public jobs$           = new BehaviorSubject<Job[]>([]);
  public orders$         = new BehaviorSubject<Order[]>([]);

  constructor(private apiSvc: ApiService) {}

  ngOnInit(): void {
    this.ensureLoggedIn().subscribe(success => {
      this.onRefresh();
    });
  }

  private ensureLoggedIn() {
    return of(true);
  }

  public onRefresh() {

    // Product-types
    this.apiSvc.readListApiV1ProductTypesGet().subscribe((productTypes: ProductType[]) => {
      const tasks$: Observable<ProductType>[] = [];
      for (const productType of productTypes) {
        const details$ = this.apiSvc.readDetailApiV1ProductTypesProductTypeIdGet({ product_type_id: productType.id });
        tasks$.push(details$);
      }
      forkJoin(tasks$).subscribe((results: ProductType[]) => {
        this.productTypes$.next(results);
      });
    });

    // Products
    this.apiSvc.readListApiV1ProductsGet().subscribe((products: Product[]) => {
      const tasks$: Observable<{product: Product, baseProducts: Product[], derivedProducts: Product[]}>[] = [];
      for (const product of products) {
        const details$ = this.apiSvc.readDetailApiV1ProductsProductIdGet({ product_id: product.id });
        const baseProds$ = this.apiSvc.readBaseProductsApiV1ProductsProductIdBaseProductsGet({ product_id: product.id });
        const derivedProds$ = this.apiSvc.readDervicedProductsApiV1ProductsProductIdDerivedProductsGet({ product_id: product.id });
        const task$: Observable<{product: Product, baseProducts: Product[], derivedProducts: Product[]}> = forkJoin([details$, baseProds$, derivedProds$]).pipe(map(([details, base, derived]) => {
          return {product: details, baseProducts: base, derivedProducts: derived};
        }));
        tasks$.push(task$);
      }
      forkJoin(tasks$).subscribe(results => {
        this.products$.next(results);
      });
    });

    // Processes
    this.apiSvc.readListApiV1ProcessesGet().subscribe((processes: Process[]) => {
      const tasks$: Observable<Process>[] = [];
      for (const process of processes) {
        const details$ = this.apiSvc.readDetailApiV1ProcessesProcessIdGet({ process_id: process.id });
        tasks$.push(details$);
      }
      forkJoin(tasks$).subscribe((results: Process[]) => {
        this.processes$.next(results);
      });
    });

    // Jobs: small parts of orders. Every job is associated with one process, one set of inputs and one set of outputs
    this.apiSvc.readListApiV1JobsGet().subscribe((jobs: Job[]) => {
      const tasks$: Observable<Job>[] = [];
      for (const job of jobs) {
        const details$ = this.apiSvc.readDetailApiV1JobsJobIdGet({ job_id: job.id });
        tasks$.push(details$);
      }
      forkJoin(tasks$).subscribe((results: Job[]) => {
        this.jobs$.next(results);
      });
    });

    // Orders: 
    this.apiSvc.readListApiV1OrdersGet().subscribe((orders: Order[]) => {
      const tasks$: Observable<Order>[] = [];
      for (const order of orders) {
        const details$ = this.apiSvc.readDetailApiV1OrdersOrderIdGet({ order_id: order.id });
        tasks$.push(details$);
      }
      forkJoin(tasks$).subscribe((results: Order[]) => {
        this.orders$.next(results);
      });
    });
  }

  
  private login() {}
  
  private register() {}
}
