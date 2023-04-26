import { Injectable } from '@angular/core';
import { UserOrder } from '../backend/backend.service';
import { AppStateFormDatum } from '../appstate/appstate.service';


/**
 * @todo
 *  - defaultOrder from config
 *  - dataPoint from config
 *  - allData from config
 *  - dataPoint2Order configurable
 */

const  dataPointKeys = ['id', 'eventId', 'longitude', 'latitude', 'depth', 'magnitude', 'rakeAngle', 'dipAngle', 'strikeAngle', 'seed', 'exposureModel', 'vulnerabilityEq', 'vulnerabilityTs'];
type DataPoint = {
  [key in typeof dataPointKeys[number]]: string
}
const defaultOrder: UserOrder = {
  "order_constraints": {
      "assetmaster": {
          "literal_inputs": {
              "assettype": [
                  "res"
              ],
              "latmax": [
                  "-33.0"
              ],
              "latmin": [
                  "-33.2"
              ],
              "lonmax": [
                  "-71.4"
              ],
              "lonmin": [
                  "-71.8"
              ],
              "model": [
                  "ValpCVTBayesian"
              ],
              "querymode": [
                  "intersects"
              ],
              "schema": [
                  "SARA_v1.0"
              ]
          },
          "bbox_inputs": {},
          "complex_inputs": {}
      },
      "eq-deus": {
          "literal_inputs": {
              "schema": [
                  "SARA_v1.0"
              ]
          },
          "bbox_inputs": {},
          "complex_inputs": {}
      },
      "eq-modelprop": {
          "literal_inputs": {
              "assetcategory": [
                  "buildings"
              ],
              "losscategory": [
                  "structural"
              ],
              "schema": [
                  "SARA_v1.0"
              ],
              "taxonomies": [
                  "none"
              ]
          },
          "bbox_inputs": {},
          "complex_inputs": {}
      },
      "shakemapresampler": {
          "literal_inputs": {
              "random_seed": [
                  "1234"
              ]
          },
          "bbox_inputs": {},
          "complex_inputs": {}
      },
      "shakyground": {
          "literal_inputs": {
              "gmpe": [
                  "MontalvaEtAl2016SInter"
              ],
              "vsgrid": [
                  "USGSSlopeBasedTopographyProxy"
              ]
          },
          "bbox_inputs": {},
          "complex_inputs": {
              "quakeMLFile": [
                  {
                      "encoding": "UTF-8",
                      "input_value": "{\"type\":\"FeatureCollection\",\"features\":[{\"geometry\":{\"coordinates\":[-72.3538,-31.9306],\"type\":\"Point\"},\"id\":\"quakeml:quakeledger/80674883\",\"properties\":{\"description.text\":\"expert\",\"focalMechanism.nodalPlanes.nodalPlane1.dip.value\":\"20.0\",\"focalMechanism.nodalPlanes.nodalPlane1.rake.value\":\"90.0\",\"focalMechanism.nodalPlanes.nodalPlane1.strike.value\":\"9.0\",\"focalMechanism.nodalPlanes.preferredPlane\":\"nodalPlane1\",\"focalMechanism.publicID\":\"quakeml:quakeledger/80674883\",\"magnitude.creationInfo.value\":\"GFZ\",\"magnitude.mag.value\":\"8.0\",\"magnitude.publicID\":\"quakeml:quakeledger/80674883\",\"magnitude.type\":\"MW\",\"origin.creationInfo.value\":\"GFZ\",\"origin.depth.value\":\"12.7\",\"origin.publicID\":\"quakeml:quakeledger/80674883\",\"origin.time.value\":\"2019-01-01T00:00:00.000000Z\",\"preferredMagnitudeID\":\"quakeml:quakeledger/80674883\",\"preferredOriginID\":\"quakeml:quakeledger/80674883\",\"publicID\":\"quakeml:quakeledger/80674883\",\"type\":\"earthquake\"},\"type\":\"Feature\"}]}",
                      "mime_type": "application/vnd.geo+json",
                      "xmlschema": ""
                  }
              ]
          }
      },
      "ts-deus": {
          "literal_inputs": {
              "schema": [
                  "SUPPASRI2013_v2.0"
              ]
          },
          "bbox_inputs": {},
          "complex_inputs": {}
      },
      "ts-modelprop": {
          "literal_inputs": {
              "assetcategory": [
                  "buildings"
              ],
              "losscategory": [
                  "structural"
              ],
              "schema": [
                  "SARA_v1.0"
              ],
              "taxonomies": [
                  "none"
              ]
          },
          "bbox_inputs": {},
          "complex_inputs": {}
      },
      "tsunami": {
          "literal_inputs": {
              "lat": [
                  "-33.1"
              ],
              "lon": [
                  "-71.6"
              ],
              "mag": [
                  "8.0"
              ]
          },
          "bbox_inputs": {},
          "complex_inputs": {}
      }
  }
};


@Injectable({
  providedIn: 'root'
})
export class PrecalcDataService {

  private allData: DataPoint[] = [
      { 'id': '5', 'eventId': '512', 'longitude': '-71.36179', 'latitude': '-30.84444',   'depth': '10',        'magnitude': '7.25',  'rakeAngle': '90',    'dipAngle': '45.00072', 'strikeAngle': '30.10888',   'seed': '1040441759', 'exposureModel': 'ValpCommuna', 'vulnerabilityEq': 'SARA_v1.0', 'vulnerabilityTs': 'Medina_2019', }, 
      { 'id': '4', 'eventId': '500', 'longitude': '-70.13472', 'latitude': '-33.55629',   'depth': '15',        'magnitude': '6.55',  'rakeAngle': '90',    'dipAngle': '44.99961', 'strikeAngle': '0.03543688', 'seed': '4099493919', 'exposureModel': 'ValpCommuna', 'vulnerabilityEq': 'SARA_v1.0', 'vulnerabilityTs': 'Medina_2019', },
      { 'id': '3', 'eventId': '236', 'longitude': '-69.13947', 'latitude': '-33.59573',   'depth': '143.3632',  'magnitude': '6.5',   'rakeAngle': '-90',   'dipAngle': '50.42967', 'strikeAngle': '34.41936',   'seed': '4193990057', 'exposureModel': 'ValpCommuna', 'vulnerabilityEq': 'SUPPASRI2013_v2.0', 'vulnerabilityTs': 'Medina_2019', }, 
      { 'id': '2', 'eventId': '134', 'longitude': '-71.37228', 'latitude': '-30.92143',   'depth': '10',        'magnitude': '6.85',  'rakeAngle': '-45',   'dipAngle': '74.99951', 'strikeAngle': '304.9481',   'seed': '136179049',  'exposureModel': 'ValpCommuna', 'vulnerabilityEq': 'SUPPASRI2013_v2.0', 'vulnerabilityTs': 'Medina_2019', }, 
      { 'id': '1', 'eventId': '130', 'longitude': '-71.56245', 'latitude': '-31.0081',    'depth': '33.81842',  'magnitude': '7.75',  'rakeAngle': '90',    'dipAngle': '13.52028', 'strikeAngle': '7.606136',   'seed': '2237070650', 'exposureModel': 'ValpCommuna', 'vulnerabilityEq': 'SUPPASRI2013_v2.0', 'vulnerabilityTs': 'Medina_2019', },
  ];
  private filters: Map<string, string> = new Map<string, string>();
  private allowedData: DataPoint[] = [];

  constructor() {
    this.reset();
  }

  reset() {
    this.filters = new Map();
    this.allowedData = this.allData;
  }

  countAvailable(): number {
    return this.allowedData.length;
  }

  /**
   * Reduces list of available datapoints
   * If `value === undefined`, then filter for `key` is removed again.
   */
  filter(key: string, value: string | undefined) {
    if (value === undefined || value === "undefined") {
        this.filters.delete(key);
    } else {
        this.filters.set(key, value);
    }
    let newAllowedData = this.allData;
    for (const [key, value] of this.filters.entries()) {
        newAllowedData = newAllowedData.filter(d => d[key] === value);
    }
    this.allowedData = newAllowedData;
  }

  /**
   * available data to form-template
   */
  toFormData(currentFormData: AppStateFormDatum[]): AppStateFormDatum[] {
      const formData: AppStateFormDatum[] = [];
      for (const key of dataPointKeys) {
        const allowedValues = unique([undefined, ...this.allowedData.map(dp => dp[key])]);
        const chosenValue = currentFormData.find(d => d.key === key)?.value;
        formData.push({
            key: key,
            options: allowedValues,
            value: chosenValue
        });
      }
      return formData;
  }


  toOrders(): UserOrder[] {
    const orders: UserOrder[] = [];
    for (const dp of this.allowedData) {
      orders.push(this.toOrder(dp));
    }
    return orders;
  }

  /**
   * Converts form-data to a user-order.
   * For this, form key-value-pairs are copied into a default-user-order.
   * Unforunately, this requires hard-coding where a specific key/val pair should be pasted into that user-order-structure.
   * I cannot think of a way to generalize this... maybe in a future version.
   */
  private toOrder(dp: DataPoint): UserOrder {
    const order: UserOrder = structuredClone(defaultOrder);
    for (const [key, val] of Object.entries(dp)) {
      switch (key) {
        // @TODO: shakyground.gmpe and .vsgrid are currently not being configured through the form.
        // @HugoRosero : should those parameters be configurable?
        case 'id':
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "id"], val);
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "magnitude.publicID"], val);
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "origin.publicID"], val);
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "preferredMagnitudeID"], val);
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "preferredOriginID"], val);
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "publicID"], val);
            break
        case 'eventId':
            // @HugoRosero: where should I write this? This is not a wps-input anywhere.
            break
        case 'longitude':
            order.order_constraints['tsunami'].literal_inputs!['lon'] = [val];
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "geometry", "coordinates", 0], val);
            break
        case 'latitude':
            order.order_constraints['tsunami'].literal_inputs!['lat'] = [val];
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "geometry", "coordinates", 0], val);
            break
        case 'depth':
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "origin.depth.value"], val);
            break
        case 'magnitude':
            order.order_constraints['tsunami'].literal_inputs!['mag'] = [val];
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "magnitude.mag.value"], val);
            break
        case 'rakeAngle':
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "focalMechanism.nodalPlanes.nodalPlane1.rake.value"], val);
            break
        case 'dipAngle':
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "focalMechanism.nodalPlanes.nodalPlane1.dip.value"], val);
            break
        case 'strikeAngle':
            this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "focalMechanism.nodalPlanes.nodalPlane1.strike.value"], val);
            break
        case 'seed':
            order.order_constraints['shakemapresampler'].literal_inputs!['random_seed'] = [val];
            break
        case 'exposureModel':
            order.order_constraints['assetmaster'].literal_inputs!['model'] = [val];
            break
        case 'vulnerabilityEq':
            order.order_constraints['assetmaster'].literal_inputs!['schema'] = [val];
            order.order_constraints['eq-deus'].literal_inputs!['schema'] = [val];  // @nbrinckm: should this line be under `case: 'vulnerabilityTs'` instead?
            order.order_constraints['eq-modelprop'].literal_inputs!['schema'] = [val];
            break
        case 'vulnerabilityTs':
            order.order_constraints['ts-deus'].literal_inputs!['schema'] = [val];
            order.order_constraints['ts-modelprop'].literal_inputs!['schema'] = [val];
            break
        default:
            throw Error(`Encountered unknown key '${key}' (value: '${val}') when attempting to create an order from form-entry`);
      }
    }
    return order;
  }

  private mutateQuakeMLFileInPlace(order: UserOrder, keyPath: (string | number)[], val: string) {
    const fcs = order.order_constraints['shakyground'].complex_inputs!['quakeMLFile'][0].input_value;
    const fcsUpdated = this.injectIntoFeatureCollectionString(fcs, keyPath, val);
    order.order_constraints['shakyground'].complex_inputs!['quakeMLFile'][0].input_value = fcsUpdated;
    return order;
  }

  private injectIntoFeatureCollectionString(featureCollectionString: string, keyPath: (string | number)[], newValue: string): string {
    const fc = JSON.parse(featureCollectionString);

    let head = fc;
    const lastPathEl = keyPath.pop()!;
    for (const key of keyPath) {
        head = head[key];
    }
    head[lastPathEl] = newValue;

    const fcs = JSON.stringify(fc);
    return fcs;
  }

}



function unique(data: any[]): any[] {
  const u: any[] = [];
  for (const entry of data) {
    if (!u.includes(entry)) {
      u.push(entry);
    }
  }
  return u;
}
