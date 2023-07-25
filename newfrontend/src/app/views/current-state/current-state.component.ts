import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, filter, interval, map, share, switchMap } from 'rxjs';
import { Job, Order, Process, ProductType } from 'src/app/services/backend/backend_api/models';
import { AppStateService } from 'src/app/services/appstate/appstate.service';
import { DbService, ProductInfo } from 'src/app/services/backend/db/db.service';

/**
 * @TODO: 
 * Hasn't been refactored yet.
 * should those requests go accross StateService instead?
 */


@Component({
  selector: 'app-current-state',
  templateUrl: './current-state.component.html',
  styleUrls: ['./current-state.component.css']
})
export class CurrentStateComponent implements OnInit {


  public productTypes$   = new BehaviorSubject<ProductType[]>([]);
  public products$       = new BehaviorSubject<ProductInfo[]>([]);
  public processes$      = new BehaviorSubject<Process[]>([]);
  public jobs$           = new BehaviorSubject<Job[]>([]);
  public orders$         = new BehaviorSubject<Order[]>([]);
  public logs$           = new BehaviorSubject<{[key: string]: string[]}>({});

  constructor(private state: AppStateService, private db: DbService) {}

  ngOnInit(): void {

    const timer$ = interval(5000).pipe(filter(_ => this.db.isLoggedIn()), share());

    timer$.pipe(
      switchMap(_ => this.db.getProductTypes()),
      map(types =>  types.sort((a, b) => a.id > b.id ? -1 : 1))
    ).subscribe(this.productTypes$);

    timer$.pipe(
        switchMap(_ => this.db.getProductTypes()),
        map(types => types.sort((a, b) => a.id > b.id ? -1 : 1))
    ).subscribe(this.productTypes$);

    timer$.pipe(
        switchMap(_ => this.db.getProducts()),
        map(products => products.sort((a, b) => a.complexOutputId > b.complexOutputId ? -1 : 1)),
        map(p => {
          p[0].baseProducts[0]
          return p;
        })
    ).subscribe(this.products$);

    timer$.pipe(
        switchMap(_ => this.db.getProcesses()),
        map(processes => processes.sort((a, b) => a.id > b.id ? -1 : 1))
    ).subscribe(this.processes$);

    timer$.pipe(
        switchMap(_ => this.db.getJobs()),
        map(jobs => jobs.sort((a, b) => a.id > b.id ? -1 : 1))
    ).subscribe(this.jobs$);

    timer$.pipe(
        switchMap(_ => this.db.getOrders()),
        map(orders => orders.sort((a, b) => a.id > b.id ? -1 : 1))
    ).subscribe(this.orders$);


  }

  public getLinkForProductId(id: number) {
    const product = this.products$.value.find(v => v.complexOutputId === id);
    return product?.link;
  }

  public getProcessById(id: number) {
    return this.processes$.value.find(v => v.id === id);
  }

}
