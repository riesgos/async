import { Component, HostBinding, OnInit } from '@angular/core';
import { DbService } from 'src/app/services/db.service';
import { UntypedFormBuilder, FormControl, UntypedFormGroup } from '@angular/forms';
import { BboxParameterConstraints, ComplexParameterConstraints, isBboxInput, isComplexInput, isLiteralInput, LiteralParameterConstraints, ParameterConstraints, PulsarService, ServiceConstraints, UserOrder } from 'src/app/services/pulsar.service';
import { model } from '../../services/model';

@Component({
  selector: 'app-order-view',
  templateUrl: './order-view.component.html',
  styleUrls: ['./order-view.component.scss']
})
export class OrderViewComponent implements OnInit {
  @HostBinding('class') class = 'content-container';

  public formGroup: UntypedFormGroup;
  public model = model;
  public state: 'ready' | 'running' | 'done' = 'ready';


  constructor(
    private orderSvc: PulsarService,
    private fb: UntypedFormBuilder,
  ) {
    const formData: any = {};
    for (const step in this.model) {
      const stepFormData: any = {};
      for (const para in this.model[step]) {
        stepFormData[para] = null;
      }
      formData[step] = this.fb.group(stepFormData);
    }
    this.formGroup = this.fb.group(formData);

    this.formGroup.valueChanges.subscribe(val => console.log(val));
  }

  ngOnInit(): void {}

  public submit() {
    this.send(this.formGroup.value);
  }

  public resetForm() {
    
  }

  private send(data: any) {
    this.state = 'running';

    const order: UserOrder = {
      order_constraints: {}
    };
    for (const processName in data) {
      const literalInputs: LiteralParameterConstraints = {};
      const bboxInputs: BboxParameterConstraints = {};
      const complexInputs: ComplexParameterConstraints = {};
      const serviceConstraints: ServiceConstraints = {
        literal_inputs: literalInputs,
        bbox_inputs: bboxInputs,
        complex_inputs: complexInputs
      };
      for (const inputName in data[processName]) {
        const inputValue = data[processName][inputName];
        if (inputValue !== null) {
          if (isLiteralInput(inputValue)) {
            literalInputs[inputName] = [inputValue];
          }
          else if (isBboxInput(inputValue)) {
            bboxInputs[inputName] = [inputValue];
          }
          else if (isComplexInput(inputValue)) {
            complexInputs[inputName] = [inputValue];
          }
        }
      }
      order.order_constraints[processName] = serviceConstraints;
    }


    this.orderSvc.postOrder(order).subscribe(success => {
      console.log('Success: ', success);
      this.state = 'done';
    });

    // this.orderSvc.postOrder({
    //   order_constraints: {
    //     shakyground: {
    //       literal_inputs: {
    //         gmpe: ["MontalvaEtAl2016SInter"],
    //         vsgrid: ["FromSeismogeotechnicsMicrozonation"]
    //       },
    //       complex_inputs: {
    //         quakeMLFile: [{
    //           input_value: JSON.stringify({
    //             "features": [
    //               {
    //                 "geometry": {
    //                   "coordinates": [
    //                     -77.9318,
    //                     -12.1908
    //                   ],
    //                   "type": "Point"
    //                 },
    //                 "id": "quakeml:quakeledger/peru_70000011",
    //                 "properties": {
    //                   "description.text": "observed",
    //                   "focalMechanism.nodalPlanes.nodalPlane1.dip.value": "20.0",
    //                   "focalMechanism.nodalPlanes.nodalPlane1.rake.value": "90.0",
    //                   "focalMechanism.nodalPlanes.nodalPlane1.strike.value": "329.0",
    //                   "focalMechanism.nodalPlanes.preferredPlane": "nodalPlane1",
    //                   "focalMechanism.publicID": "quakeml:quakeledger/peru_70000011",
    //                   "magnitude.creationInfo.value": "GFZ",
    //                   "magnitude.mag.value": "9.0",
    //                   "magnitude.publicID": "quakeml:quakeledger/peru_70000011",
    //                   "magnitude.type": "MW",
    //                   "origin.creationInfo.value": "GFZ",
    //                   "origin.depth.value": "8.0",
    //                   "origin.publicID": "quakeml:quakeledger/peru_70000011",
    //                   "origin.time.value": "1746-10-28T00:00:00.000000Z",
    //                   "preferredMagnitudeID": "quakeml:quakeledger/peru_70000011",
    //                   "preferredOriginID": "quakeml:quakeledger/peru_70000011",
    //                   "publicID": "quakeml:quakeledger/peru_70000011",
    //                   "selected": true,
    //                   "type": "earthquake"
    //                 },
    //                 "type": "Feature"
    //               }
    //             ],
    //             "type": "FeatureCollection"
    //           }),
    //           mime_type: "application/vnd.geo+json",
    //           xmlschema: "",
    //           encoding: "UTF-8"
    //         }]
    //       }
    //     }
    //   }
    // }).subscribe(success => {
    //   console.log("success: ", success);
    //   this.state = 'done';
    // })
  }

}
