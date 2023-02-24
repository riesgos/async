import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, interval } from 'rxjs';
import { AppStateService } from 'src/app/services/appstate/appstate.service';
import { LogsService } from 'src/app/services/logs/logs.service';

@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.css']
})
export class LogsComponent implements OnInit {

  // public logs$ = new BehaviorSubject<{[key: string]: string[]}>({});
  public logs: {[key: string]: string[]} = {};


  constructor(private stateSvc: AppStateService, private logsSvc: LogsService) { }

  ngOnInit(): void {
    interval(5000).subscribe(_ => {
      if (this.logsSvc.isConnected()) {
        this.refreshLogData();
      }
    })
  }

  public refreshLogData() {
    this.logsSvc.readLatest().subscribe(data => {
      
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

      this.logs = parsed;
    });
  }
}
