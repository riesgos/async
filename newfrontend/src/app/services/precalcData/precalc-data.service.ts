import { Injectable } from '@angular/core';
import { UserOrder } from '../backend/backend.service';
import { AppStateFormDatum } from '../appstate/appstate.service';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, map, tap } from 'rxjs';


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
type DataPoint = {[key: string]: string};


@Injectable({
    providedIn: 'root'
})
export class PrecalcDataService {

    private allData: DataPoint[] = [];
    private allowedData: DataPoint[] = [];
    private allowedValues: {[key: string]: string[] } = {};
    private filters: Map<string, string> = new Map<string, string>();
    private dataPointKeys: string[] = [];

    constructor(private http: HttpClient) {}

    init(): Observable<boolean> {
        const dependentParameters$ = this.http.get<{ data: DataPoint[] }>(`assets/dependent_parameters.json`);
        const independentParameters$ = this.http.get<{ data: any[] }>(`assets/independent_parameters.json`);
        return forkJoin([dependentParameters$, independentParameters$]).pipe(
            tap(([dependentParameters, independentParameters]) => {
                this.allData = dependentParameters.data;
                this.dataPointKeys = Object.keys(this.allData[0]);
                this.reset();
            }),
            map(_ => true)
        );
    }

    reset() {
        this.filters = new Map();
        this.allowedData = this.allData;
        for (const key of this.dataPointKeys) {
            this.allowedValues[key] = unique([undefined, ...this.allowedData.map(dp => dp[key])]);
        }
    }

    countAvailable(): number {
        return this.allowedData.length; // @TODO: times nr of free paras
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
            newAllowedData = newAllowedData.filter(d => ("" + d[key]) === value);
        }
        this.allowedData = newAllowedData;
        for (const key of this.dataPointKeys) {
            this.allowedValues[key] = unique([undefined, ...this.allowedData.map(dp => dp[key])]);
        }
    }

    /**
     * available data to form-template
     */
    toFormData(currentFormData: AppStateFormDatum[]): AppStateFormDatum[] {
        const formData: AppStateFormDatum[] = [];
        console.log(this.allowedValues)
        for (const key of this.dataPointKeys) {
            const allowedValues = this.allowedValues[key];
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
        for (let [key, val] of Object.entries(dp)) {
            val = `${val}`; // making sure that val is a string
            switch (key) {
                // @TODO: shakyground.gmpe and .vsgrid are currently not being configured through the form.
                // @HugoRosero : should those parameters be configurable?
                case 'id':
                    this.mutateQuakeMLFileInPlace(order, ["features", 0, "id"], `quakeml:quakeledger/${val}`);
                    this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "magnitude.publicID"], `quakeml:quakeledger/${val}`);
                    this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "origin.publicID"], `quakeml:quakeledger/${val}`);
                    this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "preferredMagnitudeID"], `quakeml:quakeledger/${val}`);
                    this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "preferredOriginID"], `quakeml:quakeledger/${val}`);
                    this.mutateQuakeMLFileInPlace(order, ["features", 0, "properties", "publicID"], `quakeml:quakeledger/${val}`);
                    break
                case 'eventId':
                    // @HugoRosero: where should I write this? This is not a wps-input anywhere.
                    break
                case 'longitude':
                    order.order_constraints['tsunami'].literal_inputs!['lon'] = [val];
                    this.mutateQuakeMLFileInPlace(order, ["features", 0, "geometry", "coordinates", 0], parseFloat(val));
                    break
                case 'latitude':
                    order.order_constraints['tsunami'].literal_inputs!['lat'] = [val];
                    this.mutateQuakeMLFileInPlace(order, ["features", 0, "geometry", "coordinates", 1], parseFloat(val));
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
                    order.order_constraints['eq-modelprop'].literal_inputs!['schema'] = [val];
                    order.order_constraints['eq-deus'].literal_inputs!['schema'] = [val];
                    order.order_constraints['ts-deus'].literal_inputs!['schema'] = [val];
                    break
                case 'vulnerabilityTs':
                    order.order_constraints['ts-modelprop'].literal_inputs!['schema'] = [val];
                    break
                default:
                    throw Error(`Encountered unknown key '${key}' (value: '${val}') when attempting to create an order from form-entry`);
            }
        }
        return order;
    }

    private mutateQuakeMLFileInPlace(order: UserOrder, keyPath: (string | number)[], val: string | number) {
        const fcs = order.order_constraints['shakyground'].complex_inputs!['quakeMLFile'][0].input_value;
        const fcsUpdated = this.injectIntoFeatureCollectionString(fcs, keyPath, val);
        order.order_constraints['shakyground'].complex_inputs!['quakeMLFile'][0].input_value = fcsUpdated;
        return order;
    }

    private injectIntoFeatureCollectionString(featureCollectionString: string, keyPath: (string | number)[], newValue: string | number): string {
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
    console.log('unique start ...')
    const u = new Set();
    for (const entry of data) {
        u.add(entry);
    }
    const out = [...u.values()];
    console.log('... unique end')
    return out;
}
