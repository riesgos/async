import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, interval } from 'rxjs';
import { AppState, AppStateService } from 'src/app/services/appstate/appstate.service';
import { LogsService } from 'src/app/services/backend/logs/logs.service';

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
  return {
    "assetmaster_wrapper": [],
    "modelprop_eq_wrapper": [],
    "shakyground_wrapper": [],
    "shakemap_resampler_wrapper": [],
    "deus_eq_wrapper": [],
    "tsunami_wrapper": [],
    "modelprop_ts_wrapper": [],
    "deus_ts_wrapper": [],
  };
}


@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.css']
})
export class LogsComponent implements OnInit {



  public logs = createEmptyLog();
  public focussedLog: AppState["focussedLog"] | undefined = undefined;

  constructor(private stateSvc: AppStateService, private logsSvc: LogsService) {
    console.log("Log component constructed")
  }

  ngOnInit(): void {
    this.stateSvc.state.subscribe(s => {
      this.focussedLog = s.focussedLog;
    });

    interval(5000).subscribe(_ => {
      if (this.logsSvc.isConnected()) {
        this.refreshLogData();
      }
    })
  }

  focusOnLog(log: AppState["focussedLog"]) {
    this.stateSvc.action({type: "focusOnLog", payload: log });
  }

  getFocussedLogs(): any {
    if (this.focussedLog === undefined) return [];
    return this.logs[this.focussedLog];
  }

  refreshLogData() {
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
