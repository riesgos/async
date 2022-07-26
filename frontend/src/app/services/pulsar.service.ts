import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';

import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PulsarService {

  /**
   * Test Test WebSocket Servers
   * https://www.piesocket.com/websocket-tester
   *
   * https://javascript-conference.com/blog/real-time-in-angular-a-journey-into-websocket-and-rxjs/
   */
  public socket$!: WebSocketSubject<any>;

  constructor() {

  }


  public connect(): void {
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = webSocket({
        url: environment.pulsarWS,
        // https://stackoverflow.com/questions/58926375/cant-read-websocket-on-angular
        deserializer: e => e.data
      });

      console.info('Connecting to', environment.pulsarWS, this.socket$);
    }
  }


  sendMessage(msg: any) {
    this.socket$.next(msg);
  }

  close() {
    this.socket$.complete();
  }

}
