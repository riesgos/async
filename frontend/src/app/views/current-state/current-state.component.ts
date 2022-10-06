import { Component, HostBinding, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { DbService, Job, Order, Process, User } from 'src/app/services/db.service';
import { LoginService } from 'src/app/services/login.service';

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
  public processes$: Observable<Process[]> | null = null;

  constructor(
    private db: DbService,
    private login: LoginService
    ) {
  }
  
  ngOnInit(): void {
    const userId = this.login.getUserId();
    this.user$ = this.db.getUser(userId);
    this.jobs$ = this.db.getJobs();
    this.orders$ = this.db.getOrders();
    this.processes$ = this.db.getProcesses();
  }

}
