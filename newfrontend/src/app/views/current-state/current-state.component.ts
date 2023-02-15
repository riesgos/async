import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, delay, forkJoin, map, Observable, of, interval } from 'rxjs';
import { Job, Order, Process, Product, ProductType } from 'src/app/backend_api/models';
import { DbService, ProductInfo } from 'src/app/services/db/db.service';

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

  constructor(private db: DbService) {}

  ngOnInit(): void {
    interval(5000).subscribe(v => {
      if (this.db.isLoggedIn()) {
        this.onRefresh();
      }
    });
  }


  public onRefresh() {
    this.db.getProductTypes().subscribe(types => {
      const sorted = types.sort((a, b) => a.id > b.id ? -1 : 1);
      this.productTypes$.next(sorted);
    });
    this.db.getProducts().subscribe(products => {
      const sorted = products.sort((a, b) => a.complexOutputId > b.complexOutputId ? -1 : 1);
      for (const entry of sorted) {
        if (entry.link) {
          entry.link = entry.link.replace(/.*\/riesgosfiles\//, '/api/v1/files/');
        }
      }
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

  public getProcessById(id: number) {
    return this.processes$.value.find(v => v.id === id);
  }

}
