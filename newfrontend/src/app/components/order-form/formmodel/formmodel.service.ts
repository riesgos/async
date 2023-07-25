import { Injectable } from '@angular/core';
import { DataModel, ServiceDataModel } from 'src/app/components/order-form/order-form.component';
import { UserOrder, ParameterConstraints, LiteralParameterConstraints, BboxParameterConstraints, ComplexParameterConstraints } from 'src/app/services/backend/backend.service';

@Injectable({
  providedIn: 'root'
})
export class FormmodelService {

  constructor() { }

  public dataModelToUserOrder(model: DataModel): UserOrder {
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
          const fc = {
            type: "FeatureCollection",
            features: [datum]
          }
          complexInputs[parameterName] = [{
            encoding: 'UTF-8',
            input_value: JSON.stringify(fc),
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
}
