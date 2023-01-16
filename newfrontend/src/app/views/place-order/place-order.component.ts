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
      tlat: -33.1299174879672
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
      schema: ['SARA_v1.0', 'Mavrouli_et_al_2014', 'Torres_Corredor_et_al_2017'],
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

    shakemapresampler: {
      // 'intensity_file': 
      'random_seed': ['1']
    },

    'eq-deus': {
      // schema: any
      // intensity: any
      // exposure: any
      // fragility: any
    }

  }


  constructor(private pulsar: PulsarService) {}

  onSendOrderClicked() {
    this.pulsar.postOrder({
      order_constraints: {
        assetmaster: {
          literal_inputs: {
            lonmin: ['-71.8'],
            lonmax: ['-71.4'],
            latmin: ['-33.2'],
            latmax: ['-33.0'],
            schema: ['SARA_v1.0'],
            assettype: ['res'],
            querymode: ['intersects'],
            model: ['ValpCVTBayesian'],
          },
        },

        'eq-modelprop': {
          literal_inputs: {
            'schema': ['SARA_v1.0'],
            'assetcategory': ['buildings'],
            'losscategory': ['structural'],
            'taxonomies': ['none'],
          }
        },

        quakeledger: {
          literal_inputs: {
            mmin: ['6.6'],
            mmax: ['8.5'],
            zmin: ['5'],
            zmax: ['140'],
            p: ['0.1'],
            tlon: ['-71.5730623712764'],
            tlat: ['-33.1299174879672'],
            etype: ['observed'],
          },
          bbox_inputs: {
            'input-boundingbox': [{
              'lower_corner_x': -72,
              'lower_corner_y': -34,
              'upper_corner_x': -70,
              'upper_corner_y': -32,
              'crs': 'EPSG:4326'
            }]
          },

        },

        shakyground: {
          literal_inputs: {
            gmpe: ['MontalvaEtAl2016SInter'],
            vsgrid: ['USGSSlopeBasedTopographyProxy'],
          },
          complex_inputs: {
            quakeMLFile: [{
                input_value: JSON.stringify({
                          "features": [
                            {
                              "geometry": {
                                "coordinates": [
                                  -77.9318,
                                  -12.1908
                                ],
                                "type": "Point"
                              },
                              "id": "quakeml:quakeledger/peru_70000011",
                              "properties": {
                                "description.text": "observed",
                                "focalMechanism.nodalPlanes.nodalPlane1.dip.value": "20.0",
                                "focalMechanism.nodalPlanes.nodalPlane1.rake.value": "90.0",
                                "focalMechanism.nodalPlanes.nodalPlane1.strike.value": "329.0",
                                "focalMechanism.nodalPlanes.preferredPlane": "nodalPlane1",
                                "focalMechanism.publicID": "quakeml:quakeledger/peru_70000011",
                                "magnitude.creationInfo.value": "GFZ",
                                "magnitude.mag.value": "9.0",
                                "magnitude.publicID": "quakeml:quakeledger/peru_70000011",
                                "magnitude.type": "MW",
                                "origin.creationInfo.value": "GFZ",
                                "origin.depth.value": "8.0",
                                "origin.publicID": "quakeml:quakeledger/peru_70000011",
                                "origin.time.value": "1746-10-28T00:00:00.000000Z",
                                "preferredMagnitudeID": "quakeml:quakeledger/peru_70000011",
                                "preferredOriginID": "quakeml:quakeledger/peru_70000011",
                                "publicID": "quakeml:quakeledger/peru_70000011",
                                "selected": true,
                                "type": "earthquake"
                              },
                              "type": "Feature"
                            }
                          ],
                          "type": "FeatureCollection"
                }),
                mime_type: "application/vnd.geo+json",
                xmlschema: "",
                encoding: "UTF-8"
            }]
          }
        },

        shakemapresampler: {
          literal_inputs: {
            'random_seed': ['1']
          }
        },

        'eq-deus': {
          literal_inputs: {
            schema: ['SARA_v1.0']
          }
        }

      }
    }).subscribe(success => console.log(`order transmitted with ${success ? 'success' : 'failure'}`));
  }

  ngOnInit(): void { }

}
