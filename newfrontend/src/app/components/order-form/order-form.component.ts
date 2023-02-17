import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { dataModel } from 'src/app/data/datamodel';
import { AppStateService } from 'src/app/services/appstate/appstate.service';
import { UserOrder, ParameterConstraints, LiteralParameterConstraints, BboxParameterConstraints, ComplexParameterConstraints } from 'src/app/services/backend/backend.service';


 /**
   * Data-model. 
   *  - *NOT* an instance of Order
   *  - for each entry:
   *    - single value: default
   *    - array: options; first one = default
   *    - null: optional
   *    - object: subform
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

  constructor(private state: AppStateService) { }

  ngOnInit(): void {
    // this.formGroup.valueChanges.subscribe(v => console.log('new value', v));
  }

  download() {
    const order = this.dataModelToUserOrder(this.formGroup.value);
    this.downloadJson('order', order);
  }

  submit() {
    const order = this.dataModelToUserOrder(this.formGroup.value);
    this.state.action({
      type: 'orderStart',
      payload: [order]
    });
  }

  private dataModelToUserOrder(model: DataModel): UserOrder {
    const order: UserOrder = {
      order_constraints: {}
    };
    for (const service in model) {
      const serviceData = model[service];
      const parameterConstraints = this.serviceDataModelToParameterConstraints(serviceData);
      order.order_constraints[service] = parameterConstraints;
    }
    return order;
  }

  private serviceDataModelToParameterConstraints(serviceData: ServiceDataModel): ParameterConstraints {
    const literalInputs: LiteralParameterConstraints = {};
    const bboxInputs: BboxParameterConstraints = {};
    const complexInputs: ComplexParameterConstraints = {};

    for (const parameterName in serviceData) {
      const parameterData = serviceData[parameterName];

      // case 1: literal data
      if (typeof parameterData === 'string') {
        literalInputs[parameterName] = [parameterData];
      } 
      
      // case 2: array-data
      else if (Array.isArray(parameterData)) {
        console.error(`Got back an array from form. We expect a string or an object.`, parameterData);
      } 
      
      // case 3: complex data
      else {
        const datum = parameterData[parameterName];
        console.log(`converting complex form-data into user-constraint: `, datum);

        // case 3.1: bbox
        if (typeof datum === 'object' && 'crs' in datum && 'lower_corner_x' in datum) {
            //@ts-ignore
          bboxInputs[parameterName] = [{
            ...datum
          }];
        }

        // case 3.2: eq-event
        if (typeof datum === 'object' && 'type' in datum && 'geometry' in datum && 'properties' in datum) {
          // @ts-ignore
          datum.geometry = datum.geometry.geometry;
          // @ts-ignore
          datum.properties = datum.properties.properties;
          // @ts-ignore
          datum.geometry.coordinates = JSON.parse(datum.geometry.coordinates);
          complexInputs[parameterName] = [{
            encoding: 'UTF-8',
            input_value: JSON.stringify(datum),
            mime_type: 'application/vnd.geo+json',
            xmlschema: ''
          }];
        }
        
        
        else {
          console.error(`Don't know how to parse this into a ParameterConstraint:`, datum);
        } 


      }
    }

    return {
      literal_inputs: literalInputs,
      bbox_inputs: bboxInputs,
      complex_inputs: complexInputs
    };
  }

  private downloadJson(name: string, data: any) {
    const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(data));
    var downloadAnchorNode = document.createElement('a');
    downloadAnchorNode.setAttribute("href",     dataStr);
    downloadAnchorNode.setAttribute("download", name + ".json");
    document.body.appendChild(downloadAnchorNode); // required for firefox
    downloadAnchorNode.click();
    downloadAnchorNode.remove();
  }
}
