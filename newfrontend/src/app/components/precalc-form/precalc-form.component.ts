import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
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
    map(formData => {
      const newFormEntries: { key: string, values: string[] | number[] }[] = [];
      for (const [key, values] of Object.entries(formData)) {
        newFormEntries.push({ key, values });
      }
      return newFormEntries;
    })
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
