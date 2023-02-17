# RIESGOS 2.0: Async sprint

## User stories

 - As a user, I want to see past results in a catalogue, 
    - so that I have an overview of the spread of possible values 
 - As a user, I want to be able to request new data by specifying parameters like: "I'm interested in eq's of magnitude 7.5 and using the USGS-grid", *leaving out* some other parameters, 
    - so that I can fill any gaps in the catalogue where I'm interested in data that isn't already there.
    - all conditional on one selected eq from catalog (=mag, location, angle etc)
   


## Architecture

- One central message-bus (pulsar)
- One database listing:
   - product-id
   - data (or link to location of data)
   - sources: list of product-id's of all the products that have been used to calculate this data
   - configuration-values?
- WPS'es wrapped in [wrapper](https://github.com/arnevogt/asyncwrapper)




## Deployment at LRZ


         outside vm   │ inside vm                                       inside vm  │ outside vm
                      │                                                            │
                      │                                                            │
                      │                                                            │
                      │                                                  /riesgosfiles/<id>
                      │                                                     ┌──────┼┐       ┌─────┐
                      │                    ┌─────────────┐◄─────────────────┼ proxy│┼───────┤wps  │
                      │                    │filestorage  │                  └──────┼┘       └─────┘
                      │                    └─────────────┘◄─────────────┐          │           ▲
                      │                                                 │          │           │
                      │                                                 │          │           │
                      │                                                 │          │           │
               /queue/ws/v2/                                            │          │           │
┌──────────┐   ┌────┴─┼┐                  ┌──────────────┐          ┌───┴─────┐    │           │
│ browser  ├──►│ proxy│┼─────────────────►│ queue        │◄─────────┤ wrapper ├────┼───────────┘
└────┬─────┘   └──────┼┘                  └──────────────┘          └───┬─────┘    │
     │                │                                                 │          │
     │         /backend/api                                             │          │
     │         ┌────┴─┼┐   ┌───────────┐     ┌──────────────┐           │          │
     └────────►│ proxy│┼──►│ fast-api  │───► │ database     │◄──────────┘          │
     order     └──────┼┘   └───────────┘     └──────────────┘     jobForOrder      │
                      │ order                                                      │
                      │                                                            │
                      │                                                            │




- :8081 -> Pulsar
- :8082/wps
- :8082/manager
- :8082/geoserver
- :9090 -> filestorage


Getting products:
postgres=# 
select co.wps_identifier, co.link, p.wps_identifier, ojr.order_id 
    from complex_outputs as co 
    join jobs as j on co.job_id = j.id 
    join processes as p on j.process_id = p.id 
    join order_job_refs as ojr on j.id = ojr.job_id;
    
   wps_identifier    |                                     link                                      |                    wps_identifier                     | order_id 
---------------------+-------------------------------------------------------------------------------+-------------------------------------------------------+----------
 selectedRows        | http://filestorage:9000/riesgosfiles/542D7AC160040F79AC93451BC6133257E3EB3871 | org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess   |        1
 selectedRows        | http://filestorage:9000/riesgosfiles/542D7AC160040F79AC93451BC6133257E3EB3871 | org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess   |        2
 selectedRows        | http://filestorage:9000/riesgosfiles/7B9EE041AB9C14EED5787BB37D8DBDFADA33DA0C | org.n52.gfz.riesgos.algorithm.impl.QuakeledgerProcess |        2
 selectedRows        | http://filestorage:9000/riesgosfiles/605E2A910BB53A241F70D7A515E1D227884B90E1 | org.n52.gfz.riesgos.algorithm.impl.QuakeledgerProcess |        2
 shakeMapFile        | http://filestorage:9000/riesgosfiles/B312B26FBAE05197046986FD1148E9D6DB7D10DA | org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess |        2
 shakeMapFile        | http://filestorage:9000/riesgosfiles/6A06F49A7996BEC897729CDBE88539A23526F9D0 | org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess |        2
 selectedRows        | http://filestorage:9000/riesgosfiles/542D7AC160040F79AC93451BC6133257E3EB3871 | org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess   |        3
 selectedRows        | http://filestorage:9000/riesgosfiles/542D7AC160040F79AC93451BC6133257E3EB3871 | org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess   |        4
 selectedRowsGeoJson | http://filestorage:9000/riesgosfiles/51768D5104799C07C72250C9F8E37853D4300B1D | org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess |        4


