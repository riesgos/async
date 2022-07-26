import { Component, OnDestroy, OnInit } from '@angular/core';

import './components/icons/ukis';

import { AlertService, IAlert } from './components/global-alert/alert.service';
import { ProgressService, IProgress } from './components/global-progress/progress.service';
import { Subscription } from 'rxjs';
import { PulsarService } from './services/pulsar.service';
import { DbService } from './services/db.service';

interface IUi {
  floating: boolean;
  flipped: boolean;
  alert: null | IAlert;
  progress: null | IProgress;
  subs: Subscription[];
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  title = '';
  shortTitle = '';

  ui: IUi = {
    floating: false,
    flipped: false,
    alert: null,
    progress: null,
    subs: []
  };

  constructor(
    private alertService: AlertService,
    private progressService: ProgressService
  ) {

    const meta = this.getHtmlMeta(['title', 'version', 'description', 'short-title']);
    if (meta['title']) {
      this.title = meta['title'];
    }
    if (meta['short-title']) {
      this.shortTitle = meta['short-title'];
    }
    this.ui.subs = this.sub2AlertAndProgress();
  }

  ngOnInit(): void {}

  /**
   *  returns an object with the keys from the input array
   */
  getHtmlMeta(names: string[]) {
    const ref = document.getElementsByTagName('meta');
    const obj: { [name: string]: string } = {};
    for (let i = 0, len = ref.length; i < len; i++) {
      const meta = ref[i];
      const name = meta.getAttribute('name');
      if (name && names.includes(name)) {
        const cv = meta.getAttribute('content') || meta.getAttribute('value');
        if (cv) {
          obj[name] = cv;
        }
      }
    }
    return obj;
  }

  sub2AlertAndProgress() {
    const subs: Subscription[] = [
      this.alertService.alert$.subscribe((alert) => {
        this.ui.alert = alert;
      }),
      this.progressService.progress$.subscribe((progress) => {
        this.ui.progress = progress;
      })
    ];
    return subs;
  }

  ngOnDestroy() {
    this.ui.subs.map(s => s.unsubscribe());
  }
}
