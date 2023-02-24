import { Component, OnDestroy, OnInit } from '@angular/core';
import { ChildActivationStart } from '@angular/router';
import { bufferCount, map, merge, Observable, tap } from 'rxjs';
import { Consumer } from 'src/app/services/pulsar/pulsar';

@Component({
  selector: 'app-logviz',
  templateUrl: './logviz.component.html',
  styleUrls: ['./logviz.component.css']
})
export class LogvizComponent implements OnInit, OnDestroy {



  private logs: Consumer[] = [
  ]

  private errors: Consumer[] = [
  ]

  public all$!: Observable<any>;

  constructor() {}

  ngOnInit(): void {
    const bufferSize = 2;
    const startBufferEvery = 1;

    const queues = [... this.logs, ... this.errors]; //[this.orders, ... this.logs, ... this.errors];
    const queues$ = queues.map(q => q.readMessages());
    this.all$ = merge(...queues$).pipe(
      tap(v => console.log('new line arrived:', v)),
      bufferCount(bufferSize, startBufferEvery)
    );
  }

  ngOnDestroy(): void {
  }

}
