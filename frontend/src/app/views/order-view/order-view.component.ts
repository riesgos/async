import { Component, HostBinding, OnInit } from '@angular/core';
import { OrderService } from 'src/app/services/order.service';

@Component({
  selector: 'app-order-view',
  templateUrl: './order-view.component.html',
  styleUrls: ['./order-view.component.scss']
})
export class OrderViewComponent implements OnInit {
  @HostBinding('class') class = 'content-container';

  public messageInput: string = 'Test Message';
  public response: string = '';

  constructor(private orderSvc: OrderService) { }

  ngOnInit(): void {
  }

  public send() {
    this.orderSvc.postOrder({
      order_constraints: {
        quakeledger: {
          literal_inputs: {
            gmpe: "MontalvaEtAl2016SInter",
            vsgrid: "FromSeismogeotechnicsMicrozonation"
          },
          complex_inputs: {
            quakeMlFile: {
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
            }

          }
        }
      }
    }).subscribe(success => {
      this.response = "Successfully posted order."
    })
  }

}
