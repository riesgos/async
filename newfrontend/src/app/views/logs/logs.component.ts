import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, interval } from 'rxjs';
import { AppStateService } from 'src/app/services/appstate/appstate.service';
import { LogsService } from 'src/app/services/logs/logs.service';

interface Log {
  "assetmaster_wrapper": string[],
  "modelprop_eq_wrapper": string[],
  "shakyground_wrapper": string[],
  "shakemap_resampler_wrapper": string[],
  "deus_eq_wrapper": string[],
  "tsunami_wrapper": string[],
  "modelprop_ts_wrapper": string[],
  "deus_ts_wrapper": string[],
};

function createEmptyLog(): Log {
  const emptyLog: Log = {
    "assetmaster_wrapper": [],
    "modelprop_eq_wrapper": [],
    "shakyground_wrapper": [],
    "shakemap_resampler_wrapper": [],
    "deus_eq_wrapper": [],
    "tsunami_wrapper": [],
    "modelprop_ts_wrapper": [],
    "deus_ts_wrapper": [],
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
      this.logs = parsed;
    });
  }
}
