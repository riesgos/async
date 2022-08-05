const port = 8080;
const base = `http://localhost:${port}`


/** @type {import("./fastAPI-Types/index").Product[]} */
const products = [
    {
        "id": 1,
        "name": "shakyground output (1)",
        "product_type_id": 1,
    },
    {
        "id": 2,
        "name": "shakyground output (2)",
        "product_type_id": 1,
    },
    {
        "id": 3,
        "name": "shakyground output (3)",
        "product_type_id": 1,
    },
    {
        "id": 4,
        "product_type_id": 2,
        "name": "assetmaster output (4)"
    },
    {
        "id": 5,
        "product_type_id": 2,
        "name": "assetmaster output (5)"
    },
    {
        "id": 6,
        "product_type_id": 2,
        "name": "assetmaster output (6)"
    },
    {
        "id": 7,
        "product_type_id": 3,
        "name": "modelprop output (7)"
    },
    {
        "id": 8,
        "product_type_id": 4,
        "name": "deus output (8)"
    },
    {
        "id": 9,
        "product_type_id": 4,
        "name": "deus output (9)"
    },
    {
        "id": 9,
        "product_type_id": 4,
        "name": "deus output (9)"
    },
    {
        "id": 10,
        "product_type_id": 5,
        "name": "systemReliability output (10)"
    },
    {
        "id": 11,
        "product_type_id": 5,
        "name": "systemReliability output (11)"
    }
];


/** @type {import("./fastAPI-Types/index").ProductType[]} */
const productTypes = [
    {
        "name": "shakyground output",
        "id": 1,
    },
    {
        "name": "assetmaster output",
        "id": 2,
    },
    {
        "name": "modelprop output",
        "id": 3,
    },
    {
        "name": "deus output",
        "id": 4,
    },
    {
        "name": "systemReliability output",
        "id": 5,
    }
];

/** @type {import("./fastAPI-Types/index").Process[]} */
const processes = [
    {
        "id": 1,
        "wps_url": "https://rz-vm140.gfz-potsdam.de",
        "wps_identifier": "shakyground"
    },
    {
        "id": 2,
        "wps_url": "https://rz-vm140.gfz-potsdam.de",
        "wps_identifier": "shakyground",
    }
];


/** @type {import("./fastAPI-Types/index").complexOutputs[]} */
const complexOutputs = [
    {
        id: 1,
        job_id: 1,
        wps_identifier: 'shakemap1',
        link: 'https://earthquake.usgs.gov/realtime/product/shakemap/us6000i89c/us/1659681612105/download/cont_pga.json',
        mime_type: 'application/vnd.geo+json',
        xmlschema: '',
        encoding: 'utf-8',
    },
    {
        id: 2,
        job_id: 2,
        wps_identifier: 'shakemap2',
        link: 'https://earthquake.usgs.gov/realtime/product/shakemap/us6000i89c/us/1659681612105/download/cont_psa3p0.json',
        mime_type: 'application/vnd.geo+json',
        xmlschema: '',
        encoding: 'utf-8',
    }
];

module.exports = [
    {
        'id': 'api1',
        'port': port,
        'log': true,
        // backend\catalog_app\main.py
        // backend\tests\test_routes\test_complex_outputs.py
        'routes': {
            '/api/bbox-inputs': {
                'get': (req, res, next) => {
                    const inputs = [{
                        job_id: 1,
                        job: `${base}/api/jobs/1`,
                        wps_identifier: '',
                        lower_corner_x: -79.757,
                        lower_corner_y: -9.775,
                        upper_corner_x: -65.682,
                        upper_corner_y: -3.362,
                        crs: 'EPSG:4326'
                    }];
                    return res.send(inputs);
                }
            },
            '/api/complex-inputs-as-values': {
                'get': (req, res, next) => {
                    return res.send([]);
                }
            },
            '/api/complex-inputs': {
                'get': (req, res, next) => {
                    const complexInputs = [{
                        id: 1,
                        job_id: 1,
                        job: `${base}/api/jobs/1`,
                        wps_identifier: 'QuakeledgerProcess',
                        input_value: [],
                        mime_type: '',
                        xmlschema: '',
                        encoding: ''
                    }, {
                        id: 2,
                        job_id: 2,
                        job: `${base}/api/jobs/2`,
                        wps_identifier: 'ShakygroundProcess',
                        input_value: [],
                        mime_type: '',
                        xmlschema: '',
                        encoding: ''
                    },
                    {
                        id: 3,
                        job_id: 3,
                        job: `${base}/api/jobs/3`,
                        wps_identifier: 'AssetmasterProcess',
                        input_value: [],
                        mime_type: '',
                        xmlschema: '',
                        encoding: ''
                    }];
                    return res.send(complexInputs);
                }
            },
            '/api/complex-outputs-as-inputs': {
                'get': (req, res, next) => {
                    return res.send([]);
                }
            },
            '/api/complex-outputs': {
                'get': (req, res, next) => {
                    if (req.query.job_id) {
                        return res.send(complexOutputs.filter(o => o.job_id == req.query.job_id));
                    } else {
                        return res.send(complexOutputs);
                    }
                }
            },
            '/api/jobs': {
                'get': (req, res, next) => {
                    const jobs = [{
                        id: 1,
                        process_id: 1,
                        process: `${base}/api/processes/1`,
                        status: '',
                        order_job_refs: `${base}/api/...`,
                        complex_outputs: `${base}/api/...`,
                        complex_outputs_as_inputs: `${base}/api/...`,
                        complex_inputs: `${base}/api/...`,
                        complex_inputs_as_values: `${base}/api/...`,
                        literal_inputs: `${base}/api/...`,
                        bbox_inputs: `${base}/api/...`,
                    }];
                    return res.send(jobs);
                }
            },
            '/api/literal-inputs': {
                'get': (req, res, next) => {
                    return res.send([]);
                }
            },
            '/api/order-job-refs': {
                'get': (req, res, next) => {
                    return res.send([]);
                }
            },
            '/api/orders': {
                'get': (req, res, next) => {
                    return res.send([]);
                }
            },
            '/api/processes': {
                'get': (req, res, next) => {
                    return res.send(processes);
                }
            },
            '/api/processes/:process_id': {
                'get': (req, res, next) => {
                    return res.send(processes.filter(i => i.id == req.params.process_id));
                }
            },
            '/api/users': {
                'get': (req, res, next) => {
                    return res.send([]);
                }
            },
            '/api/products': {
                'get': (req, res, next) => {
                    return res.send(products);
                }
            },
            '/api/product-types': {
                'get': (req, res, next) => {
                    return res.send(productTypes);
                }
            }
        }
    }
];