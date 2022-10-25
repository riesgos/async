import { BboxInput, ComplexInput, LiteralInput } from "./pulsar.service";


export interface Step {
    [parameter: string]: {
        [key: string]: LiteralInput | BboxInput | ComplexInput
    };
}

export interface Model {
    [step: string]: Step
}


/**
 * { 
 *      processName: {
 *          inputName: {
 *              inputValueAsString: inputValue
 *          }
 *      }
 * }
 */
export const model: Model = {
    assetMaster: { 
        model: {
            'LimaCVT1_PD30_TI70_5000': 'LimaCVT1_PD30_TI70_5000',
            'LimaCVT2_PD30_TI70_10000': 'LimaCVT2_PD30_TI70_10000',
            'LimaCVT3_PD30_TI70_50000': 'LimaCVT3_PD30_TI70_50000',
            'LimaCVT4_PD40_TI60_5000': 'LimaCVT4_PD40_TI60_5000',
            'LimaCVT5_PD40_TI60_10000': 'LimaCVT5_PD40_TI60_10000',
            'LimaCVT6_PD40_TI60_50000': 'LimaCVT6_PD40_TI60_50000',
            'LimaBlocks': 'LimaBlocks'
        }
    },
    modelProp: { 
        schema: {
            "SARA_v1.0": "SARA_v1.0"
        }
    },
    
    shakyground: {
        quakeMLFile: {
            "quakeml:quakeledger/80674883": {
                input_value: JSON.stringify({
                    "type": "FeatureCollection",
                    "features": [{
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [
                                -72.3538,
                                -31.9306
                            ]
                        },
                        "properties": {
                            "publicID": "quakeml:quakeledger/80674883",
                            "preferredOriginID": "quakeml:quakeledger/80674883",
                            "preferredMagnitudeID": "quakeml:quakeledger/80674883",
                            "type": "earthquake",
                            "description.text": "expert",
                            "origin.publicID": "quakeml:quakeledger/80674883",
                            "origin.time.value": "2019-01-01T00:00:00.000000Z",
                            "origin.depth.value": "12.7",
                            "origin.creationInfo.value": "GFZ",
                            "magnitude.publicID": "quakeml:quakeledger/80674883",
                            "magnitude.mag.value": "8.0",
                            "magnitude.type": "MW",
                            "magnitude.creationInfo.value": "GFZ",
                            "focalMechanism.publicID": "quakeml:quakeledger/80674883",
                            "focalMechanism.nodalPlanes.nodalPlane1.strike.value": "9.0",
                            "focalMechanism.nodalPlanes.nodalPlane1.dip.value": "20.0",
                            "focalMechanism.nodalPlanes.nodalPlane1.rake.value": "90.0",
                            "focalMechanism.nodalPlanes.preferredPlane": "nodalPlane1"
                        },
                        "id": "quakeml:quakeledger/80674883"
                    }]
                }),
                mime_type: "application/vnd.geo+json",
                xmlschema: "",
                encoding: "UTF-8"
            },
            "quakeml:quakeledger/80674884": {
                input_value: JSON.stringify({
                    "type": "FeatureCollection",
                    "features": [{
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [
                                -72.4418,
                                -32.3742
                            ]
                        },
                        "properties": {
                            "publicID": "quakeml:quakeledger/80674884",
                            "preferredOriginID": "quakeml:quakeledger/80674884",
                            "preferredMagnitudeID": "quakeml:quakeledger/80674884",
                            "type": "earthquake",
                            "description.text": "expert",
                            "origin.publicID": "quakeml:quakeledger/80674884",
                            "origin.time.value": "2019-01-01T00:00:00.000000Z",
                            "origin.depth.value": "12.7",
                            "origin.creationInfo.value": "GFZ",
                            "magnitude.publicID": "quakeml:quakeledger/80674884",
                            "magnitude.mag.value": "8.0",
                            "magnitude.type": "MW",
                            "magnitude.creationInfo.value": "GFZ",
                            "focalMechanism.publicID": "quakeml:quakeledger/80674884",
                            "focalMechanism.nodalPlanes.nodalPlane1.strike.value": "9.0",
                            "focalMechanism.nodalPlanes.nodalPlane1.dip.value": "20.0",
                            "focalMechanism.nodalPlanes.nodalPlane1.rake.value": "90.0",
                            "focalMechanism.nodalPlanes.preferredPlane": "nodalPlane1"
                        },
                        "id": "quakeml:quakeledger/80674884"
                    }]
                }),
                mime_type: "application/vnd.geo+json",
                xmlschema: "",
                encoding: "UTF-8"
            }
        },
        gmpe: {
            "MontalvaEtAl2016SInter": "MontalvaEtAl2016SInter",
            "GhofraniAtkinson2014": "GhofraniAtkinson2014",
            "AbrahamsonEtAl2015SInter": "AbrahamsonEtAl2015SInter",
            "YoungsEtAl1997SInterNSHMP2008": "YoungsEtAl1997SInterNSHMP2008",
        },
        vsgrid: {
            "USGSSlopeBasedTopographyProxy": "USGSSlopeBasedTopographyProxy",
            "FromSeismogeotechnicsMicrozonation": "FromSeismogeotechnicsMicrozonation",
        }
    },
};