import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, interval } from 'rxjs';
import { Job, Order, Process, ProductType } from 'src/app/backend_api/models';
import { DbService, ProductInfo } from 'src/app/services/db/db.service';
import { LogsService } from 'src/app/services/logs/logs.service';

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

  constructor(private db: DbService, private logs: LogsService) {}

  ngOnInit(): void {
    // interval(5000).subscribe(v => {
    //   if (this.db.isLoggedIn()) {
    //     this.refreshDbData();
    //   }
    //   if (this.logs.isConnected()) {
    //     this.refreshLogData();
    //   }
    // });
  }


  public refreshDbData() {
    this.db.getProductTypes().subscribe(types => {
      const sorted = types.sort((a, b) => a.id > b.id ? -1 : 1);
      this.productTypes$.next(sorted);
    });
    this.db.getProducts().subscribe(products => {
      const sorted = products.sort((a, b) => a.complexOutputId > b.complexOutputId ? -1 : 1);
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
  public refreshLogData() {
    this.logs.readLatest().subscribe(data => {
      
      const parsed: {[key: string]: string[]} = {};
      const regex = /async-(.*)-1 *\| (.*)/g;

      for (const entry of data) {

        const matches = [... entry.matchAll(regex)];
        if (matches && matches[0]) {
          const container = matches[0][1];
          const message = matches[0][2];
  
          if (container && !(container in parsed)) {
            parsed[container] = [];
          }
          parsed[container].push(message);
        }
        
      }

      for (const container in parsed) {
        parsed[container] = parsed[container].reverse().slice(0, 50);
      }

      this.logs$.next(parsed);
    });
  }

  public getProcessById(id: number) {
    return this.processes$.value.find(v => v.id === id);
  }

}
