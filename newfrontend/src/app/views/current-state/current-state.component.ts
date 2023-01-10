import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, forkJoin, map, Observable, of } from 'rxjs';
import { Job, Order, Process, Product, ProductType } from 'src/app/backend_api/models';
import { ApiService } from 'src/app/backend_api/services';
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

  ngOnInit(): void {}


  public onRefresh() {
    this.db.getProductTypes().subscribe(types => this.productTypes$.next(types));
    this.db.getProducts().subscribe(products => this.products$.next(products));
    this.db.getProcesses().subscribe(processes => this.processes$.next(processes));
    this.db.getJobs().subscribe(jobs => this.jobs$.next(jobs));
    this.db.getOrders().subscribe(orders => this.orders$.next(orders));
  }


}
