import { Component, HostBinding, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { DbService, Job, Order, User } from 'src/app/services/db.service';

@Component({
  selector: 'app-current-state',
  templateUrl: './current-state.component.html',
  styleUrls: ['./current-state.component.scss']
})
export class CurrentStateComponent implements OnInit {
  @HostBinding('class') class = 'content-container';
  public user$: Observable<User> | null = null;
  public jobs$: Observable<Job[]> | null = null;
  public orders$: Observable<Order[]> | null = null;

  constructor(private db: DbService) {
  }
  
  ngOnInit(): void {
    this.user$ = this.db.getUser();
    this.jobs$ = this.db.getJobs();
    this.orders$ = this.db.getOrders();
  }

}
