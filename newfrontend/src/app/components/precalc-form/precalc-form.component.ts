import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { map } from 'rxjs';
import { AppStateService } from 'src/app/services/appstate/appstate.service';
import { FormmodelService } from 'src/app/services/formmodel/formmodel.service';
import { downloadJson } from 'src/app/utils/utils';

@Component({
  selector: 'app-precalc-form',
  templateUrl: './precalc-form.component.html',
  styleUrls: ['./precalc-form.component.css']
})
export class PrecalcFormComponent implements OnInit {

  public formEntries$ = this.state.state.pipe(
    map(s => s.formData),
  );
  public combosAvailable$ = this.state.state.pipe(
    map(s => s.combinationsAvailable)
  );

  constructor(private state: AppStateService, private formmodel: FormmodelService) {}

  ngOnInit(): void {}

  public submit() {
    this.state.action({
      type: 'formSubmit',
      payload: {} // could create payload here ... but it's not used in reducer, anyway.
    })
  }

  public download() {
    // const order = entriesToOrder(this.formEntries$)
    // downloadJson('order', order);
  }

}
