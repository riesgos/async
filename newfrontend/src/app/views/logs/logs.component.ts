import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, interval } from 'rxjs';
import { AppStateService } from 'src/app/services/appstate/appstate.service';
import { LogsService } from 'src/app/services/logs/logs.service';

interface Log {
    "db": string[],
    "backend": string[],
    "modelprop_wrapper": string[],
    "assetmaster_wrapper": string[],
    "shakyground_wrapper": string[],
    "shakemap_resampler_wrapper": string[],
    "deus_wrapper": string[],
};

function createEmptyLog(): Log {
  const emptyLog: Log = {
    "db": [],
    "backend": [],
    "modelprop_wrapper": [],
    "assetmaster_wrapper": [],
    "shakyground_wrapper": [],
    "shakemap_resampler_wrapper": [],
    "deus_wrapper": [],
  };
  return emptyLog;
}


@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.css']
})
export class LogsComponent implements OnInit {

  public logs: Log = createEmptyLog();


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
      
      const parsed: Log = createEmptyLog();

      for (const entry of data) {
        for (const key in parsed) {
          if (entry.startsWith(`async-${key}`)) {
            // @ts-ignore
            parsed[key].push(entry);
            continue;
          }
        }
      }

      for (const container in parsed) {
                    // @ts-ignore
        parsed[container] = parsed[container].reverse().slice(0, 50);
      }
console.log(parsed);
      this.logs = parsed;
    });
  }
}
