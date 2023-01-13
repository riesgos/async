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


  private orders = new Consumer('ws://localhost:8080/ws/v2/producer/persistent/public/default/new-order');

  private logs: Consumer[] = [
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/quakeledger-success/shared'),
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/assetmaster-success/shared'),
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/modelprop-success/shared'),
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/shakyground-success/shared'),
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/shakemap-resampler-success/shared'),
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/deus-success/shared'),
  ]

  private errors: Consumer[] = [
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/quakeledger-failure/shared'),
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/assetmaster-failure/shared'),
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/modelprop-failure/shared'),
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/shakyground-failure/shared'),
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/shakemap-resampler-failure/shared'),
    // new Consumer('ws://localhost:8080/ws/v2/consumer/persistent/public/default/deus-failure/shared'),
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
