import { Injectable } from "@angular/core";
import { catchError, defaultIfEmpty, forkJoin, map, Observable, of, switchMap, tap } from "rxjs";
import { Job, Order, Process, Product, ProductType, UserSelfInformation } from "src/app/backend_api/models";
import { ApiService } from "src/app/backend_api/services";
import { UserOrder } from "../pulsar/pulsar.service";

@Injectable({
    providedIn: 'root'
  })
  export class DbService {
    private userSelfInformation: UserSelfInformation | undefined;

    constructor(private apiSvc: ApiService) {
        this.login('a@b.com', '1234').subscribe(result => console.log(`Logged in: `, result));
    }

    private register(email: string, password: string) {
        return this.apiSvc.registerUserApiV1UsersRegisterPost({
            body: { email, password }
        }).pipe(
            tap(result => this.userSelfInformation = result)
        )
    }

    private login(email: string, password: string) {
      return this.apiSvc.loginUserApiV1UsersLoginPost({
        body: { email, password }
      }).pipe(
        switchMap(result => {
            if (result) return of(result);
            return this.register(email, password);
        }),
        catchError(error => {
            console.log(`Error logging in: `, error);
            return this.register(email, password);
        }),
        tap(result => this.userSelfInformation = result)
      )
    }

    public isLoggedIn() {
        return !!this.userSelfInformation;
    }

    placeOrder(order: UserOrder) {
        return this.apiSvc.createOrderApiV1OrdersPost({
            "x-apikey": this.userSelfInformation?.apikey,
            body: order
        })
    }

    getProductTypes() {
        return this.apiSvc.readListApiV1ProductTypesGet().pipe(
            switchMap((productTypes: ProductType[]) => {
                
                const tasks$: Observable<ProductType>[] = [];
                for (const productType of productTypes) {
                  const details$ = this.apiSvc.readDetailApiV1ProductTypesProductTypeIdGet({ product_type_id: productType.id });
                  tasks$.push(details$);
                }
                
                return forkJoin(tasks$).pipe(defaultIfEmpty([]));
            })
        );
    }

    getProducts() {
        return this.apiSvc.readListApiV1ProductsGet().pipe(
            switchMap((products: Product[]) => {

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

                return forkJoin(tasks$).pipe(defaultIfEmpty([]));
            })
        );
    }
  
    getProcesses() {
        return this.apiSvc.readListApiV1ProcessesGet().pipe(
            switchMap((processes: Process[]) => {
                const tasks$: Observable<Process>[] = [];
                for (const process of processes) {
                    const details$ = this.apiSvc.readDetailApiV1ProcessesProcessIdGet({ process_id: process.id });
                    tasks$.push(details$);
                }

                return forkJoin(tasks$).pipe(defaultIfEmpty([]));
            })
        );
    }
    
    getJobs() {
        // Jobs: small parts of orders. Every job is associated with one process, one set of inputs and one set of outputs
        return this.apiSvc.readListApiV1JobsGet().pipe(
            switchMap((jobs: Job[]) => {

                const tasks$: Observable<Job>[] = [];
                for (const job of jobs) {
                    const details$ = this.apiSvc.readDetailApiV1JobsJobIdGet({ job_id: job.id });
                    tasks$.push(details$);
                }
                
                return forkJoin(tasks$).pipe(defaultIfEmpty([]));
            })
        );
    }
  
    getOrders() {
      return this.apiSvc.readListApiV1OrdersGet().pipe(
        switchMap((orders: Order[]) => {
            const tasks$: Observable<Order>[] = [];
            for (const order of orders) {
                const details$ = this.apiSvc.readDetailApiV1OrdersOrderIdGet({ order_id: order.id });
                tasks$.push(details$);
            }

            return forkJoin(tasks$).pipe(defaultIfEmpty([]));
        })
      );
    }
}