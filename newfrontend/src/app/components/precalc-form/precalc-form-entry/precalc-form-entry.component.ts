import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AppStateService } from 'src/app/services/appstate/appstate.service';

@Component({
  selector: 'app-precalc-form-entry',
  templateUrl: './precalc-form-entry.component.html',
  styleUrls: ['./precalc-form-entry.component.css']
})
export class PrecalcFormEntryComponent implements OnInit {

  @Input() formGroup!: FormGroup;
  @Input() entry!: {key: string, values: (string | number)[] }
  public form!: FormControl;

  constructor(private state: AppStateService) { }

  ngOnInit(): void {
    this.form = new FormControl();
    this.formGroup.addControl(this.entry.key, this.form);
    this.form.valueChanges.subscribe(v => this.state.action({
        type: 'formSelect',
        payload: {
          key: this.entry.key, value: v
        }
    }));
  }

}
