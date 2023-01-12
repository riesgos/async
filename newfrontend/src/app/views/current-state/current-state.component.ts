import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, delay, forkJoin, map, Observable, of, interval } from 'rxjs';
import { Job, Order, Process, Product, ProductType } from 'src/app/backend_api/models';
import { DbService } from 'src/app/services/db/db.service';

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

  constructor(private db: DbService) {}

  ngOnInit(): void {
    interval(1000).subscribe(v => this.onRefresh());
  }


  public onRefresh() {
    this.db.getProductTypes().subscribe(types => {
      const sorted = types.sort((a, b) => a.id > b.id ? -1 : 1);
      this.productTypes$.next(sorted);
    });
    this.db.getProducts().subscribe(products => {
      const sorted = products.sort((a, b) => a.product.id > b.product.id ? -1 : 1);
      this.products$.next(sorted);
    });
    this.db.getProcesses().subscribe(processes => {
      const sorted = processes.sort((a, b) => a.id > b.id ? -1 : 1);
      this.processes$.next(sorted);
    });
    this.db.getJobs().subscribe(jobs => {
      const sorted = jobs.sort((a, b) => a.id > b.id ? -1 : 1);
      this.jobs$.next(sorted);
    });
    this.db.getOrders().subscribe(orders => {
      const sorted = orders.sort((a, b) => a.id > b.id ? -1 : 1);
      this.orders$.next(sorted);
    });
  }


}
