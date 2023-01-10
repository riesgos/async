import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { PulsarService } from 'src/app/services/pulsar/pulsar.service';


// interface Order {
//   [wrapperName: string]: WrapperConstraints
// }

// interface WrapperConstraints {
//   literal_inputs: {
//     [parameterName: string]: string[]
//   },
//   bbox_inputs: {
//     [parameterName: string]: BboxConstraint[]
//   },
//   complex_inputs: {
//     [parameterName: string]: ComplexConstraint[]
//   }
// }

// interface BboxConstraint {
//   'lower_corner_x': number
//   'lower_corner_y': number
//   'upper_corner_x': number
//   'upper_corner_y': number
//   'crs': string
// }

// interface ComplexConstraint {
//   'link'?: string
//   'input_value'?: string
//   'mime_type': string
//   'xmlschema': string
//   'encoding': string 
// }



@Component({
  selector: 'app-place-order',
  templateUrl: './place-order.component.html',
  styleUrls: ['./place-order.component.css']
})
export class PlaceOrderComponent implements OnInit {

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
  public dataModel = {

    quakeledger: {
        lonmin: {
          'lower_corner_x': 0,
          'lower_corner_y': 0,
          'upper_corner_x': 0,
          'upper_corner_y': 0,
          'crs': 'EPSG:4326'
        },
        mmin: 6.6,
        mmax: 8.5,
        zmin: 5,
        zmax: 140,
        p: 0.1,
        etype: ['observed', 'deaggregation', 'stochastic', 'expert'],
        tlon: -71.5730623712764,
        tlat : -33.1299174879672
    },
  
    shakyground: {
      gmpe: ['MontalvaEtAl2016SInter', 'GhofraniAtkinson2014', 'AbrahamsonEtAl2015SInter', 'YoungsEtAl1997SInterNSHMP2008'],
      vsgrid: ['USGSSlopeBasedTopographyProxy', 'FromSeismogeotechnicsMicrozonation']
      // quakeMLFile: any
      // shakeMapFile: any
    },
  
    assetmaster: {
      lonmin: -71.8, 
      lonmax: -71.4,
      latmin: -33.2,
      latmax: -33.0,
      schema: [ 'SARA_v1.0', 'Mavrouli_et_al_2014', 'Torres_Corredor_et_al_2017' ],
      assettype: 'res',
      querymode: 'intersects',
      model: ['ValpCVTBayesian', 'ValpCommuna', 'ValpRegularOriginal', 'ValpRegularGrid', 'LimaCVT1_PD30_TI70_5000', 'LimaCVT2_PD30_TI70_10000', 'LimaCVT3_PD30_TI70_50000', 'LimaCVT4_PD40_TI60_5000', 'LimaCVT5_PD40_TI60_10000', 'LimaCVT6_PD40_TI60_50000'],
      // selectedrowsgeojson: any
    },
  
    'eq-modelprop': {
      schema: ['SARA_v1.0', 'HAZUS_v1.0', 'SUPPASRI2013_v2.0', 'Mavrouli_et_al_2014', 'Torres_Corredor_et_al_2017', 'Medina_2019'],
      assetcategory: 'buildings',
      losscategory: 'structural',
      taxonomies: 'none'
    },
  
    'eq-deus': {
      // schema: any
      // intensity: any
      // exposure: any
      // fragility: any
    }
  
  }

  public formGroup: FormGroup;

  constructor(private pulsar: PulsarService) {
    this.formGroup = this.makeFormGroup(this.dataModel);
    this.formGroup.valueChanges.subscribe(v => console.log(v))
  }

  onSendOrderClicked() {
    this.pulsar.postOrder({
      order_constraints: {
        assetmaster: {
          literal_inputs: {
            lonmin:     ['-71.8'],
            lonmax:     ['-71.4'],
            latmin:     ['-33.2'],
            latmax:     ['-33.0'],
            schema:     ['SARA_v1.0'],
            assettype:  ['res'],
            querymode:  ['intersects'],
            model:      ['ValpCVTBayesian'],
          }
        }
      }
    }).subscribe(success => console.log(`order transmitted with ${success ? 'success' : 'failure'}`));
  }

  ngOnInit(): void {}

  private makeFormGroup(data: any): FormGroup {
    const controls: any = {};
    for (const key in data) {

      const value = data[key];

      if (this.isNull(value)) {
        controls[key] = new FormControl('');
      }
      else if (this.isArray(value)) {
        controls[key] = new FormControl(value[0], [Validators.required, this.createOneOfValidator(value)]);
      }
      else if (this.isString(value) || this.isNumber(value)) {
        controls[key] = new FormControl(value, [Validators.required]);
      }
      else {
        controls[key] = this.makeFormGroup(value);
      }

    }
    const fg = new FormGroup(controls);
    return fg;
  }

  private createOneOfValidator(options: any[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      const oneOfOptions = options.includes(value);
      return !oneOfOptions ? {notOneOfOptions: true} : null;
    } 
  }

  public isNull(value: any): boolean {
    return (value === undefined || value === null);
  }

  public isArray(value: any): boolean {
    return (Array.isArray(value) && value.length > 0);
  }

  public isString(value: any): boolean {
    return (typeof value === 'string' || value instanceof String);
  }

  public isNumber(value: any): boolean {
    return (!isNaN(value) || typeof value === 'number');
  }

  public isObject(value: any): boolean {
    return typeof value === 'object' && !this.isArray(value) && !this.isString(value) && !this.isNumber(value);
  }

}
