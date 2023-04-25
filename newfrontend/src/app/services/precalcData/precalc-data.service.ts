import { Injectable } from '@angular/core';
import { UserOrder } from '../backend/backend.service';




const  dataPointKeys = ['id', 'eventId', 'longitude', 'latitude', 'depth', 'magnitude', 'rakeAngle', 'dipAngle', 'strikeAngle', 'seed', 'exposureModel', 'vulnerabilityEq', 'vulnerabilityTs'];
type DataPoint = {
  [key in typeof dataPointKeys[number]]: string | number
}


@Injectable({
  providedIn: 'root'
})
export class PrecalcDataService {

  private allData: DataPoint[] = [
      { 'id': 5, 'eventId': 512, 'longitude': -71.36179, 'latitude': -30.84444,   'depth': 10,        'magnitude': 7.25,  'rakeAngle': 90,  'dipAngle': 45.00072, 'strikeAngle': 30.10888, 'seed': 1040441759, 'exposureModel': 'ValpCommuna', 'vulnerabilityEq': 'SARA_v1.0', 'vulnerabilityTs': 'Medina_2019', }, 
      { 'id': 4, 'eventId': 500, 'longitude': -70.13472, 'latitude': -33.55629,   'depth': 15,        'magnitude': 6.55,  'rakeAngle': 90,  'dipAngle': 44.99961, 'strikeAngle': 0.03543688, 'seed': 4099493919, 'exposureModel': 'ValpCommuna', 'vulnerabilityEq': 'SARA_v1.0', 'vulnerabilityTs': 'Medina_2019', },
      { 'id': 3, 'eventId': 236, 'longitude': -69.13947, 'latitude': -33.59573,   'depth': 143.3632,  'magnitude': 6.5,   'rakeAngle': -90,   'dipAngle': 50.42967, 'strikeAngle': 34.41936, 'seed': 4193990057, 'exposureModel': 'ValpCommuna', 'vulnerabilityEq': 'SUPPASRI2013_v2.0', 'vulnerabilityTs': 'Medina_2019', }, 
      { 'id': 2, 'eventId': 134, 'longitude': -71.37228, 'latitude': -30.92143,   'depth': 10,        'magnitude': 6.85,  'rakeAngle': -45,   'dipAngle': 74.99951, 'strikeAngle': 304.9481, 'seed': 136179049, 'exposureModel': 'ValpCommuna', 'vulnerabilityEq': 'SUPPASRI2013_v2.0', 'vulnerabilityTs': 'Medina_2019', }, 
      { 'id': 1, 'eventId': 130, 'longitude': -71.56245, 'latitude': -31.0081,    'depth': 33.81842,  'magnitude': 7.75,  'rakeAngle': 90,  'dipAngle': 13.52028, 'strikeAngle': 7.606136, 'seed': 2237070650, 'exposureModel': 'ValpCommuna', 'vulnerabilityEq': 'SUPPASRI2013_v2.0', 'vulnerabilityTs': 'Medina_2019', },
  ];
  private allowedData: DataPoint[] = [];

  constructor() {
    this.allowedData = this.allData;
  }

  /**
   * reduces list of available datapoints
   */
  formSelect(key: string, value: string | number) {
      this.allowedData = this.allowedData.filter(dp => dp[key] === value);
  }

  /**
   * available data to form-template
   */
  toFormData(): { [key: string]: (string | number)[]; } {
      const formData: { [key: string]: (string | number)[]; } = {};
      for (const key of dataPointKeys) {
        const allowedValues = unique(this.allowedData.map(dp => dp[key]));
        formData[key] = allowedValues;
      }
      return formData;
  }


  toOrders(): UserOrder[] {
    throw new Error("Method not implemented.");
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