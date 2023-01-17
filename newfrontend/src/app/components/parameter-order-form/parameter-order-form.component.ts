import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ParameterModel } from '../order-form/order-form.component';

@Component({
  selector: 'app-parameter-order-form',
  templateUrl: './parameter-order-form.component.html',
  styleUrls: ['./parameter-order-form.component.css']
})
export class ParameterOrderFormComponent implements OnInit {

  @Input() title = "";
  @Input() data!: ParameterModel;
  @Input() formGroup!: FormGroup;
  public formControl = new FormControl();

  constructor() { }

  ngOnInit(): void {
    if (this.isString(this.data)) {
      this.formControl.setValue(this.data);
    } else if (this.isArray(this.data)) {
      if (this.data.length > 0) {
        this.formControl.setValue((this.data as Array<string>)[0]);
      }
    } else {
      console.error(`Don't know how to display this entry: ${this.title}/${this.data}`)
    }
    this.formGroup.addControl(this.title, this.formControl);
  }

  public isString(data: any): data is string {
    return (typeof data === 'string');
  }

  public isArray(data: any): data is string[] {
    return Array.isArray(data);
  }
}
