import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { debounce, debounceTime, map } from 'rxjs';
import { AppStateFormDatum, AppStateService } from 'src/app/services/appstate/appstate.service';

@Component({
  selector: 'app-precalc-form-range',
  templateUrl: './precalc-form-range.component.html',
  styleUrls: ['./precalc-form-range.component.css']
})
export class PrecalcFormRangeComponent implements OnInit {

  @Input() formGroup!: FormGroup;
  @Input() entry!: AppStateFormDatum;
  public lowerForm!: FormControl;
  public upperForm!: FormControl;

  constructor(private state: AppStateService) { }

  ngOnInit(): void {
    const options = this.entry.options.filter(o => o !== undefined).map(o => +o!).sort((a, b) => a < b ? -1 : 1);
    this.lowerForm = new FormControl(options[0]);
    this.upperForm = new FormControl(options[options.length - 1]);
    this.formGroup.addControl(this.entry.key + '_lower', this.lowerForm);
    this.formGroup.addControl(this.entry.key + '_upper', this.upperForm);

    this.lowerForm.valueChanges.pipe(
      debounceTime(1000)
    ).subscribe(v => this.state.action({
      type: 'formSelect',
      payload: {
        key: this.entry.key + '_lower', value: v
      }
    }));
    this.upperForm.valueChanges.pipe(
      debounceTime(1000)
    ).subscribe(v => this.state.action({
      type: 'formSelect',
      payload: {
        key: this.entry.key + '_upper', value: v
      }
    }));

  }

}
