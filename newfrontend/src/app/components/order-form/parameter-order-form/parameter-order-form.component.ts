import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ParameterModel, ServiceDataModel } from '../../order-form/order-form.component';

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
  public subFormGroup = new FormGroup({});

  constructor() {}

  ngOnInit(): void {
    if (this.isString(this.data)) {
      this.formControl.setValue(this.data);
      this.formGroup.addControl(this.title, this.formControl);
    }
    
    else if (this.isArray(this.data)) {
      if (this.data.length > 0) {
        this.formControl.setValue((this.data as Array<any>)[0]);
      }
      this.formGroup.addControl(this.title, this.formControl);
    }

    else if (this.isComplex(this.data)) {
      console.log('making complex control: ', this.title, this.data)
      this.formGroup.addControl(this.title, this.subFormGroup);
    }
  }

  public isString(data: any): data is string {
    return (typeof data === 'string');
  }

  public isArray(data: any): data is any[] {
    return Array.isArray(data);
  }

  public isComplex(data: any): data is ServiceDataModel {
    return !this.isString(data) && !this.isArray(data);
  }
}
