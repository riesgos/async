import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { BehaviorSubject, map } from 'rxjs';
import { AppStateFormDatum, AppStateService } from 'src/app/services/appstate/appstate.service';
import { PrecalcDataService } from 'src/app/services/precalcData/precalc-data.service';
import { downloadJson } from 'src/app/utils/utils';

@Component({
  selector: 'app-precalc-form',
  templateUrl: './precalc-form.component.html',
  styleUrls: ['./precalc-form.component.css']
})
export class PrecalcFormComponent implements OnInit {

  public formEntries$ = new BehaviorSubject<AppStateFormDatum[]>([]);
  public combosAvailable$ = this.state.state.pipe(
    map(s => s.combinationsAvailable)
  );

  constructor(
    private state: AppStateService, 
    private dataSvc: PrecalcDataService
  ) {}

  ngOnInit(): void {
    this.state.state.pipe(
      map(s => s.formData)
    ).subscribe(this.formEntries$);
  }

  public submit() {
    this.state.action({
      type: 'formSubmit',
      payload: {} // could create payload here ... but it's not used in reducer, anyway.
    })
  }

  public download() {
    // const currentEntries = this.formEntries$.value;
    const orders = this.dataSvc.toOrders();
    downloadJson('orders', orders);
  }

}

