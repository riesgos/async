import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { dataModel } from 'src/app/data/datamodel';
import { AppStateService } from 'src/app/services/appstate/appstate.service';
import { UserOrder, ParameterConstraints, LiteralParameterConstraints, BboxParameterConstraints, ComplexParameterConstraints } from 'src/app/services/backend/backend.service';
import { FormmodelService } from 'src/app/services/formmodel/formmodel.service';
import { downloadJson } from 'src/app/utils/utils';


 /**
   * Data-model. 
   *  - *NOT* an instance of Order
   *  - for each entry:
   *    - single value: default
   *    - array: options; first one = default
   *    - null: optional
   *    - object: subform
   * 
   * Used as basis for form-control and form-html.
   * Form-submission then converted to actual order.
   */
export interface DataModel {
  [serviceName: string]: ServiceDataModel
}

export interface ServiceDataModel {
  [parameterName: string]: ParameterModel
}

export type ParameterModel = string | any[] | ServiceDataModel;




@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',
  styleUrls: ['./order-form.component.css']
})
export class OrderFormComponent implements OnInit {

  public model = dataModel;
  public formGroup = new FormGroup({});

  constructor(private state: AppStateService, private formmodel: FormmodelService) { }

  ngOnInit(): void {
  }

  download() {
    const order = this.formmodel.dataModelToUserOrder(this.formGroup.value);
    downloadJson('order', order);
  }

  submit() {
    const order = this.formmodel.dataModelToUserOrder(this.formGroup.value);
    this.state.action({
      type: 'orderStart',
      payload: [order]
    });
  }


}
