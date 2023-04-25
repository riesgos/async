import { Injectable } from '@angular/core';
import { UserOrder } from '../backend/backend.service';

@Injectable({
  providedIn: 'root'
})
export class PrecalcDataService {

  private allowedData: any[] = [];

  toOrders(): UserOrder[] {
      throw new Error("Method not implemented.");
  }

  /**
   * reduces list of available datapoints
   */
  formSelect(key: string, value: string | number) {
      throw new Error("Method not implemented.");
  }

  /**
   * available data to form-template
   */
  getFormData(): { [key: string]: (string | number)[]; } {
      throw new Error("Method not implemented.");
  }

  constructor() { }
}
