import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { map } from 'rxjs';
import { AppStateService } from 'src/app/services/appstate/appstate.service';

@Component({
  selector: 'app-precalc-form',
  templateUrl: './precalc-form.component.html',
  styleUrls: ['./precalc-form.component.css']
})
export class PrecalcFormComponent implements OnInit {

  public orderForm: FormGroup = new FormGroup({});
  public formEntries = this.state.state.pipe(
    map(s => s.formData),
  );

  constructor(private state: AppStateService) {}

  ngOnInit(): void {}

  public submit() {
    this.state.action({
      type: 'formSubmit',
      payload: {} // could create payload here ... but it's not used in reducer, anyway.
    })
  }

  public download() {

  }

}
