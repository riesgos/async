import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ServiceDataModel } from '../order-form/order-form.component';

/**
   * Data-model. 
   *  - *NOT* an instance of Order
   *  - for each entry:
   *    - single value: default
   *    - array: options; first one = default
   *    - null: optional
   *    - object: subform
   */

@Component({
  selector: 'app-service-order-form',
  templateUrl: './service-order-form.component.html',
  styleUrls: ['./service-order-form.component.css']
})
export class ServiceOrderFormComponent implements OnInit {

  @Input() title: string = "";
  @Input() data!: ServiceDataModel;
  @Input() formGroup!: FormGroup;
  public subFormGroup = new FormGroup({});

  constructor() { }

  ngOnInit(): void {
    this.formGroup.addControl(this.title, this.subFormGroup);
  }

}
