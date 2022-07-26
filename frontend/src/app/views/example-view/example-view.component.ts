import { Component, OnInit, HostBinding, OnDestroy } from '@angular/core';
import { PulsarService } from 'src/app/services/pulsar.service';




@Component({
  selector: 'app-example-view',
  templateUrl: './example-view.component.html',
  styleUrls: ['./example-view.component.scss']
})
export class ExampleViewComponent implements OnInit, OnDestroy {
  @HostBinding('class') class = 'content-container';

  public messages: string = '';
  public messageInput: string = 'Test Message';
  constructor(private pulsarSvc: PulsarService) { }
  ngOnInit() {

  }

  ngOnDestroy() {

  }

  connect() {
    this.pulsarSvc.connect();
    this.pulsarSvc.socket$.subscribe(data => {
      console.log(data);
      this.messages += `; ${data}`;
    });
  }

  disconnect() {
    this.pulsarSvc.close();
  }

  send() {
    this.pulsarSvc.sendMessage(this.messageInput);
  }
}
