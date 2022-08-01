const port = 8080;
const base = `http://localhost:${port}`
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
                    const complexOutputs = [
                        {
                            id: 1,
                            job_id: 1,
                            job: `${base}/api/jobs/1`,
                            wps_identifier: 'QuakeledgerProcess',
                            name: "Earthquakes 1",
                            link: '',
                            mime_type: 'application/vnd.geo+json',
                            xmlschema: '',
                            encoding: 'utf-8',
                            inputs: `${base}/api/complex-inputs/1`,
                        },
                        {
                            id: 2,
                            job_id: 1,
                            job: `${base}/api/jobs/1`,
                            wps_identifier: 'QuakeledgerProcess',
                            name: "Earthquakes 2",
                            link: '',
                            mime_type: 'application/vnd.geo+json',
                            xmlschema: '',
                            encoding: 'utf-8',
                            inputs: `${base}/api/complex-inputs/2`,
                        },
                        {
                            id: 3,
                            job_id: 1,
                            job: `${base}/api/jobs/1`,
                            wps_identifier: 'QuakeledgerProcess',
                            name: "Earthquakes 3",
                            link: '',
                            mime_type: 'application/vnd.geo+json',
                            xmlschema: '',
                            encoding: 'utf-8',
                            inputs: `${base}/api/complex-inputs/3`,
                        },

                        {
                            id: 4,
                            job_id: 2,
                            job: `${base}/api/jobs/2`,
                            wps_identifier: 'ShakygroundProcess',
                            name: "ShakeMap File 1",
                            link: '',
                            mime_type: 'text/xml',
                            xmlschema: '',
                            encoding: 'utf-8',
                            inputs: `${base}/api/complex-inputs/4`,
                        },
                        {
                            id: 5,
                            job_id: 2,
                            job: `${base}/api/jobs/2`,
                            wps_identifier: 'ShakygroundProcess',
                            name: "shakeMap File 2",
                            link: '',
                            mime_type: 'text/xml',
                            xmlschema: '',
                            encoding: 'utf-8',
                            inputs: `${base}/api/complex-inputs/5`,
                        },
                        {
                            id: 6,
                            job_id: 2,
                            job: `${base}/api/jobs/2`,
                            wps_identifier: 'ShakygroundProcess',
                            name: "ShakeMap File 3",
                            link: '',
                            mime_type: 'text/xml',
                            xmlschema: '',
                            encoding: 'utf-8',
                            inputs: `${base}/api/complex-inputs/6`,
                        },
                        {
                            id: 7,
                            job_id: 3,
                            job: `${base}/api/jobs/3`,
                            wps_identifier: 'AssetmasterProcess',
                            name: "Exposure model 1",
                            link: '',
                            mime_type: '',
                            xmlschema: '',
                            encoding: '',
                            inputs: `${base}/api/complex-inputs/7`,
                        },
                        {
                            id: 8,
                            job_id: 3,
                            job: `${base}/api/jobs/3`,
                            wps_identifier: 'AssetmasterProcess',
                            name: "Exposure model 2",
                            link: '',
                            mime_type: '',
                            xmlschema: '',
                            encoding: '',
                            inputs: `${base}/api/complex-inputs/8`,
                        },
                        {
                            id: 9,
                            job_id: 3,
                            job: `${base}/api/jobs/3`,
                            wps_identifier: 'AssetmasterProcess',
                            name: "Exposure model 3",
                            link: '',
                            mime_type: '',
                            xmlschema: '',
                            encoding: '',
                            inputs: `${base}/api/complex-inputs/9`,
                        },


                        {
                            id: 10,
                            job_id: 4,
                            job: `${base}/api/jobs/4`,
                            wps_identifier: 'ModelpropProcess',
                            name: "Economic loss Earthquake  1",
                            link: '',
                            mime_type: '',
                            xmlschema: '',
                            encoding: '',
                            inputs: `${base}/api/complex-inputs/10`,
                        },
                        {
                            id: 11,
                            job_id: 4,
                            job: `${base}/api/jobs/4`,
                            wps_identifier: 'ModelpropProcess',
                            name: "Economic loss Earthquake  2",
                            link: '',
                            mime_type: '',
                            xmlschema: '',
                            encoding: '',
                            inputs: `${base}/api/complex-inputs/11`,
                        },


                        {
                            id: 12,
                            job_id: 5,
                            job: `${base}/api/jobs/5`,
                            wps_identifier: 'DeusProcess',
                            name: "Exposure and damage Earthquake 1",
                            link: '',
                            mime_type: '',
                            xmlschema: '',
                            encoding: '',
                            inputs: `${base}/api/complex-inputs/12`,
                        },
                        {
                            id: 13,
                            job_id: 5,
                            job: `${base}/api/jobs/5`,
                            wps_identifier: 'DeusProcess',
                            name: "Exposure and damage Earthquake 1",
                            link: '',
                            mime_type: '',
                            xmlschema: '',
                            encoding: '',
                            inputs: `${base}/api/complex-inputs/13`,
                        },



                        {
                            id: 14,
                            job_id: 6,
                            job: `${base}/api/jobs/6`,
                            wps_identifier: 'SystemReliabilitySingleProcess',
                            name: "Damage Consumer Areas 1",
                            link: '',
                            mime_type: '',
                            xmlschema: '',
                            encoding: '',
                            inputs: `${base}/api/complex-inputs/14`,
                        },
                        {
                            id: 15,
                            job_id: 6,
                            job: `${base}/api/jobs/6`,
                            wps_identifier: 'SystemReliabilitySingleProcess',
                            name: "Damage Consumer Areas 2",
                            link: '',
                            mime_type: '',
                            xmlschema: '',
                            encoding: '',
                            inputs: `${base}/api/complex-inputs/15`,
                        },
                        {
                            id: 16,
                            job_id: 6,
                            job: `${base}/api/jobs/6`,
                            wps_identifier: 'SystemReliabilitySingleProcess',
                            name: "Damage Consumer Areas 3",
                            link: '',
                            mime_type: '',
                            xmlschema: '',
                            encoding: '',
                            inputs: `${base}/api/complex-inputs/16`,
                        }

                    ];
                    return res.send(complexOutputs);
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
                    return res.send([]);
                }
            },
            '/api/users': {
                'get': (req, res, next) => {
                    return res.send([]);
                }
            }
        }
    }
];