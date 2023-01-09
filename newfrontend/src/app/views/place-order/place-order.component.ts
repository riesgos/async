import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';


interface Order {
  [wrapperName: string]: WrapperConstraints
}

interface WrapperConstraints {
  literal_inputs: {
    [parameterName: string]: string[]
  },
  bbox_inputs: {
    [parameterName: string]: BboxConstraint[]
  },
  complex_inputs: {
    [parameterName: string]: ComplexConstraint[]
  }
}

interface BboxConstraint {
  'lower_corner_x': number
  'lower_corner_y': number
  'upper_corner_x': number
  'upper_corner_y': number
  'crs': string
}

interface ComplexConstraint {
  'link'?: string
  'input_value'?: string
  'mime_type': string
  'xmlschema': string
  'encoding': string 
}



type dataModel = {

  quakeledger: {
      lonmin: number
      mmin: number
      mmax: number
      zmin: number
      zmax: number
      p: number
      etype: 'observed' | 'deaggregation' | 'stochastic' | 'expert'
      tlon: number
      tlat : number
  },

  shakyground: {
    gmpe: 'MontalvaEtAl2016SInter' | 'GhofraniAtkinson2014' | 'AbrahamsonEtAl2015SInter' | 'YoungsEtAl1997SInterNSHMP2008'
    vsgrid: 'USGSSlopeBasedTopographyProxy' | 'FromSeismogeotechnicsMicrozonation'
    quakeMLFile: any
    shakeMapFile: any
  }

  assetmaster: {
    lonmin: number
    lonmax: number
    latmin: number
    latmax: number
    schema: 'SARA_v1.0'
    assettype: 'res'
    querymode: 'intersects'
    model: 'ValpCVTBayesian' | 'ValpCommuna' | 'ValpRegularOriginal' | 'ValpRegularGrid' | 'LimaCVT1_PD30_TI70_5000' | 'LimaCVT2_PD30_TI70_10000' | 'LimaCVT3_PD30_TI70_50000' | 'LimaCVT4_PD40_TI60_5000' | 'LimaCVT5_PD40_TI60_10000' | 'LimaCVT6_PD40_TI60_50000'
    selectedrowsgeojson: any
  },

  'eq-modelprop': {
    schema: 'SARA_v1.0'
    assetcategory: 'buildings'
    losscategory: 'structural'
    taxonomies: 'none'
  }

  'eq-deus': {
    schema: any
    intensity: any
    exposure: any
    fragility: any
  }

}

@Component({
  selector: 'app-place-order',
  templateUrl: './place-order.component.html',
  styleUrls: ['./place-order.component.css']
})
export class PlaceOrderComponent implements OnInit {

  public orderForm = new FormGroup<Order>({
    'quakeledger': new FormGroup({

    })
  })

  constructor() {}

  ngOnInit(): void {}

}
