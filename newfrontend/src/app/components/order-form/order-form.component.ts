import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { BboxParameterConstraints, ComplexParameterConstraints, LiteralParameterConstraints, ParameterConstraints, PulsarService, UserOrder } from 'src/app/services/pulsar/pulsar.service';


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

export type ParameterModel = string | string[] | ServiceDataModel;

export const dataModel: DataModel = {

  quakeledger: {
    'input-boundingbox': {
      'lower_corner_x': '-71.8',
      'lower_corner_y': '-33.2',
      'upper_corner_x': '-71.4',
      'upper_corner_y': '-33.0',
      'crs': ['EPSG:4326']
    },
    mmin: '6.6',
    mmax: '8.5',
    zmin: '5',
    zmax: '140',
    p: '0.1',
    etype: ['observed', 'deaggregation', 'stochastic', 'expert'],
    tlon: '-71.5730623712764',
    tlat: '-33.1299174879672'
  },

  shakyground: {
    gmpe: ['MontalvaEtAl2016SInter', 'GhofraniAtkinson2014', 'AbrahamsonEtAl2015SInter', 'YoungsEtAl1997SInterNSHMP2008'],
    vsgrid: ['USGSSlopeBasedTopographyProxy', 'FromSeismogeotechnicsMicrozonation']
    // quakeMLFile: any
    // shakeMapFile: any
  },

  assetmaster: {
    lonmin: '-71.8',
    lonmax: '-71.4',
    latmin: '-33.2',
    latmax: '-33.0',
    schema: ['SARA_v1.0', 'Mavrouli_et_al_2014', 'Torres_Corredor_et_al_2017'],
    assettype: ['res'],
    querymode: ['intersects', 'within'],
    model: ['ValpCVTBayesian', 'ValpCommuna', 'ValpRegularOriginal', 'ValpRegularGrid', 'LimaCVT1_PD30_TI70_5000', 'LimaCVT2_PD30_TI70_10000', 'LimaCVT3_PD30_TI70_50000', 'LimaCVT4_PD40_TI60_5000', 'LimaCVT5_PD40_TI60_10000', 'LimaCVT6_PD40_TI60_50000'],
    // selectedrowsgeojson: any
  },

  'eq-modelprop': {
    schema: ['SARA_v1.0', 'HAZUS_v1.0', 'SUPPASRI2013_v2.0', 'Mavrouli_et_al_2014', 'Torres_Corredor_et_al_2017', 'Medina_2019'],
    assetcategory: ['buildings'],
    losscategory: ['structural'],
    taxonomies: ['none']
  },

  'eq-deus': {
    schema: ['SARA_v1.0', 'HAZUS_v1.0', 'SUPPASRI2013_v2.0', 'Mavrouli_et_al_2014', 'Torres_Corredor_et_al_2017', 'Medina_2019'],
    // intensity: any
    // exposure: any
    // fragility: any
  }

}



@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',
  styleUrls: ['./order-form.component.css']
})
export class OrderFormComponent implements OnInit {

  public model = dataModel;
  public formGroup = new FormGroup({});

  constructor(private pulsar: PulsarService) { }

  ngOnInit(): void {
    // this.formGroup.valueChanges.subscribe(v => console.log('new value', v));
  }

  download() {
    const order = this.dataModelToUserOrder(this.formGroup.value);
    this.downloadJson('order', order);
  }

  submit() {
    const order = this.dataModelToUserOrder(this.formGroup.value);
    console.log(`submitting order: `, order);
    this.pulsar.postOrder(order).subscribe(success => console.log(`order transmitted with ${success ? 'success' : 'failure'}`));
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

      if (typeof parameterData === 'string') {
        literalInputs[parameterName] = [parameterData];
      } else if (Array.isArray(parameterData)) {
        console.error(`Got back an array from form. We expect a string or an object.`, parameterData);
      } else {
        const datum = parameterData[parameterName];
        console.log(datum)
        if (typeof datum === 'object' && 'crs' in datum && 'lower_corner_x' in datum) {
            //@ts-ignore
          bboxInputs[parameterName] = [{
            ...datum
          }];
        } else {
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
