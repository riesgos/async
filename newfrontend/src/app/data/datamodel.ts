import { DataModel } from "../components/order-form/order-form.component";



const eqEvents = [
    {
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
    },
];


export const dataModel: DataModel = {


    shakyground: {
        gmpe: ['MontalvaEtAl2016SInter', 'GhofraniAtkinson2014', 'AbrahamsonEtAl2015SInter', 'YoungsEtAl1997SInterNSHMP2008'],
        vsgrid: ['USGSSlopeBasedTopographyProxy', 'FromSeismogeotechnicsMicrozonation'],
        quakeMLFile: {
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": "[-72.3538,-31.9306]"
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
        }
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

};