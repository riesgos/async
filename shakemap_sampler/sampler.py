# -*- coding: utf-8 -*-
"""
Created on Mon Nov 28 10:01:50 2022

@author: Hugo Rosero
"""


#import shakemap
#import shakeml
import quakeml
import numpy as np
import random
import lxml.etree as le
import pandas
import io
import datetime
#import correlation as crl
#from openquake.hazardlib.site import Site, SiteCollection
#from openquake.hazardlib.geo import Mesh, Point, geodetic
from openquake.hazardlib.imt import PGA, PGV, IA, SA
import gstools as gs
"""
Reads a normal shakemap (as it is the output of shakyground for earthquake scenarios)
:return: The shakemap with the random residuals (separated and mixed)
"""







def extract_shakemap_data(shakemap_file):
    #intensity_map = shakemap.Shakemaps.from_file(shakemap_file).to_intensity_provider()
    try:
        shakeml = le.parse(shakemap_file)
    except:
        # maybe string
        parser = le.XMLParser(huge_tree=True, recover=True, encoding="utf-8")
        # shakeml = le.parse(io.StringIO(shakemlfile),parser)
        shakeml = le.parse(shakemap_file, parser)
        # try:
        #     inp = io.BytesIO(shakemap_file)
        # except TypeError:
        #     inp = io.StringIO(shakemap_file)
        # shakeml = le.parse(inp, parser)
    nsmap = shakeml.getroot().nsmap
    shakemlroot = shakeml.getroot()
    
    # find event
    smevent = shakeml.find("event", namespaces=nsmap)
    
    # event attributes
    index = [i for i in range(max(1, len(smevent)))]
    columns = [
            "eventID",
            "Agency",
            "Identifier",
            "year",
            "month",
            "day",
            "hour",
            "minute",
            "second",
            "timeError",
            "longitude",
            "latitude",
            "SemiMajor90",
            "SemiMinor90",
            "ErrorStrike",
            "depth",
            "depthError",
            "magnitude",
            "sigmaMagnitude",
            "rake",
            "dip",
            "strike",
            "type",
            "probability",
            "fuzzy",
        ]
    event = pandas.DataFrame(index=index, columns=columns)
    # event=pandas.Series()
    # assign event attributes
    event["eventID"] = smevent.attrib["event_id"]
    event["Agency"] = smevent.attrib["event_network"]
    (
        event["year"],
        event["month"],
        event["day"],
        event["hour"],
        event["minute"],
        event["second"],
    ) = quakeml.utc2event(smevent.attrib["event_timestamp"])
    event["depth"] = float(smevent.attrib["depth"])
    event["magnitude"] = float(smevent.attrib["magnitude"])
    event["longitude"] = float(smevent.attrib["lon"])
    event["latitude"] = float(smevent.attrib["lat"])
    #event["strike"] = float(smevent.attrib["strike"])
    #event["dip"] = float(smevent.attrib["dip"])
    #event["rake"] = float(smevent.attrib["rake"])
    event["type"] = shakemlroot.attrib["shakemap_event_type"]
    # FIXME: deal with shakeml.attrib: 'shakemap_id': 'us1000gez7', 'shakemap_version': '2', 'code_version': '3.5.1615', 'process_timestamp': '2018-08-21T23:32:17Z', 'shakemap_originator': 'us', 'map_status': 'RELEASED'
    # FIXME: deal with description
    # smevent.attrib['event_description']

    # FIXME:uncertainty
    elems_event_specific_uncertainties = shakeml.findall(
        "event_specific_uncertainty", namespaces=nsmap
    )
    index = [i for i in range(len(elems_event_specific_uncertainties))]
    columns = ["name", "value", "numsta"]
    event_specific_uncertainties = pandas.DataFrame(
        index=index, columns=columns
    )
    for i, el in enumerate(elems_event_specific_uncertainties):
        event_specific_uncertainties.iloc[i]["name"] = el.attrib["name"]
        event_specific_uncertainties.iloc[i].value = el.attrib["value"]
        event_specific_uncertainties.iloc[i].numsta = el.attrib["numsta"]

    # grid specification
    # NOTE:added indicator for structured and unstructured
    # TODO: derive regularity maybe...
    grid_specification = shakeml.find("grid_specification", namespaces=nsmap)
    try:
        regular_grid = bool(grid_specification.attrib["regular_grid"])
    except:
        # assume a regular grid
        regular_grid = True

    # TODO: actually necessary? Probably not...as is inherent to the grid if needed can be easily derived from pandas df
    # attributes: lon_min,lat_min,lon_max,lat_max,nominal_lon_spacing,nominal_lat_spacing,nlon,nlat

    # columns
    grid_fields = shakeml.findall("grid_field", namespaces=nsmap)

    # indices (start at 1) & argsort them
    column_idxs = [
        int(grid_field.attrib["index"]) - 1 for grid_field in grid_fields
    ]
    idxs_sorted = np.argsort(column_idxs)
    column_names = [grid_field.attrib["name"] for grid_field in grid_fields]
    columns = [column_names[idx] for idx in idxs_sorted]

    # get grid
    grid_data = io.StringIO(shakeml.find("grid_data", namespaces=nsmap).text)

    grid_data = pandas.read_csv(grid_data, sep=" ", header=None)
    grid_data.columns = columns

    # get units
    units = pandas.DataFrame(index=[0], columns=columns)
    for grid_field in grid_fields:
        units.iloc[0][grid_field.attrib["name"]] = grid_field.attrib["units"]

    return event,columns,units,grid_data, event_specific_uncertainties, regular_grid

def create_uncorrelated_residuals(grid_data,columns,units,random_seed):    
    if 'PGA' in columns and 'STDPGA' in columns:
        median_values=grid_data['PGA']
        std_values=grid_data['STDPGA']
        random.seed(random_seed)
        #generates uncorrelated stantard normal random values, one for each point in the shakemap.
        #results are reproducible in this way with the fixed random seed
        random_normal_values=np.random.normal(loc=0.0,scale=1.,size=median_values.shape)
        #stores the random residuals
        grid_data['RESPGA']=random_normal_values
        units['RESPGA']='g'
        columns.append('RESPGA')
        #the GMPE is the form ln(rand_IM) = ln(IM)+stdIM*randnorm
        #We know IM (it is in PGA), hence we get rand_IM = IM*exp(stdIM*randnorm)
        grid_data['MEDPGA']=median_values
        units['MEDPGA']='g'
        columns.append('MEDPGA')
        #storage in PGA for being read by downstream services
        grid_data['PGA']=median_values*np.exp(random_normal_values*std_values)
    return grid_data,columns,units



def create_correlated_residuals(grid_data,columns,units,random_seed): 
    ##### uses only JB2009CorrelationModel #####
    if 'PGA' in columns and 'STDPGA' in columns:
        median_values=grid_data['PGA']
        std_values=grid_data['STDPGA']
        gx=grid_data['LAT']
        gy=grid_data['LON']
        #z=np.ones(median_values.shape)#fake vs30 values. They're not needed for the correlation model, since we use the case with clusters
        #sites1 = SiteCollection([Site(Point(x,y), z, vs30measured=True, z1pt0=3.4, z2pt5=5.6, backarc=False) for x, y,z  in zip(gx, gy, z)])
        b_value=jbcorrelation_length(PGA, True)
        corr_model = gs.Exponential(latlon=True, len_scale=b_value/(3*gs.EARTH_RADIUS))
        srf=gs.SRF(corr_model,mean=0,seed=random_seed)
        
        #generates uncorrelated stantard normal random values, one for each point in the shakemap.
        #results are reproducible in this way with the fixed random seed
        #uncorr_residuals_norm=np.random.normal(loc=0.0,scale=1.,size=median_values.shape)
        #crl_model = crl.JB2009CorrelationModel(True)
        #Only computing PGA
        #corr_residuals_norm = apply_correlation(sites1,PGA,uncorr_residuals_norm,L)
        corr_residuals_norm = srf((gx,gy))
        corr_residuals_norm = np.asarray(corr_residuals_norm)
        #stores uncorrelated the random variables
        #grid_data['UNCRESPGA']=uncorr_residuals_norm
        #units['UNCRESPGA']='g'
        #columns.append('UNCRESPGA')
        #stores the random residuals
        grid_data['RESPGA']=corr_residuals_norm 
        units['RESPGA']='g'
        columns.append('RESPGA')
        #the GMPE is the form ln(rand_IM) = ln(IM)+stdIM*randnorm
        #We know IM (it is in PGA), hence we get rand_IM = IM*exp(stdIM*randnorm)
        grid_data['MEDPGA']=median_values
        units['MEDPGA']='g'
        columns.append('MEDPGA')
        #storage in PGA for being read by downstream services
        grid_data['PGA']=median_values*np.exp(corr_residuals_norm *std_values)
    return grid_data,columns,units


def jbcorrelation_length( imt, vs30_clustering=False):
        """
        Returns the Jayaram-Baker correlation model.
        :param imt:
            Intensity Measure Type (PGA or SA)
        :param vs30_clustering:
            flag, defalt false
        """


        # formulae are from page 1700
        if imt.period < 1:
            if not vs30_clustering:
                # case 1, eq. (17)
                b = 8.5 + 17.2 * imt.period
            else:
                # case 2, eq. (18)
                b = 40.7 - 15.0 * imt.period
        else:
            # both cases, eq. (19)
            b = 22.0 + 3.7 * imt.period

        # eq. (20)
        #return numpy.exp((- 3.0 / b) * distances)
        return b


def build_random_shakemap(event,columns,units,grid_data, event_specific_uncertainties,regular_grid,random_seed):
    nsmap = {
        "xsi": "http://www.w3.org/2001/XMLSchema-instance",
        None: "http://earthquake.usgs.gov/eqcenter/shakemap",
    }
    schemaLocation = le.QName("{" + nsmap["xsi"] + "}schemaLocation")

    # processing attributes
    code_version = le.QName("code_version")
    shakemap_version = le.QName("shakemap_version")
    process_timestamp = le.QName("process_timestamp")
    #shakemap_originator = le.QName("shakemap_originator")
    now = datetime.datetime.utcnow()
    now = pandas.Series(
        {
            "year": now.year,
            "month": now.month,
            "day": now.day,
            "hour": now.hour,
            "minute": now.minute,
            "second": now.second + now.microsecond / 10.0 ** 6,
        }
    )

    event_id = le.QName("event_id")
    shakemap_id = le.QName("shakemap_id")
    map_status = le.QName("map_status")
    shakemap_event_type = le.QName("shakemap_event_type")
    shakemap_originator = le.QName("shakemap_originator")

    shakeml = le.Element(
        "shakemap_grid",
        {
            schemaLocation: "http://earthquake.usgs.gov http://earthquake.usgs.gov/eqcenter/shakemap/xml/schemas/shakemap.xsd",
            event_id: event.iloc[0]["eventID"],
            # FIXME: same as eventID!? No should be related to measure, gmpe etc....
            shakemap_id: event.iloc[0]["eventID"],
            # NOTE: not shakemap standard
            code_version: "shakyground 0.1",
            shakemap_version: "1",
            process_timestamp:quakeml.event2utc(now),
            #process_timestamp: "{:04d}-{:02d}-{:02d}T{:02d}:{:02d}:{:09f}Z".format(int(now.year),int(max(now.month, 1)),int(max(now.day, 1)),int(now.hour),int(now.minute),now.second, ),
            #shakemap_originator: provider,
            map_status: "RELEASED",
            shakemap_event_type: event.iloc[0]["type"],
            shakemap_originator: f"GFZ with random seed: {str(random_seed)}"
        },
        nsmap=nsmap,
    )

    # write event data
    # <event event_id="us1000gez7" magnitude="7.3" depth="123.18" lat="10.739200" lon="-62.910600" event_timestamp="2018-08-21T21:31:42UTC"                  event_network="us" event_description="OFFSHORE SUCRE, VENEZUELA" />
    magnitude = le.QName("magnitude")
    depth = le.QName("depth")
    lat = le.QName("lat")
    lon = le.QName("lon")
    strike = le.QName("strike")
    rake = le.QName("rake")
    dip = le.QName("dip")
    event_timestamp = le.QName("event_timestamp")
    event_network = le.QName("event_network")
    event_description = le.QName("event_description")
    # seed = le.QName("seed")
    smevent = le.SubElement(
        shakeml,
        "event",
        {
            event_id: str(event.iloc[0]["eventID"]),
            magnitude: str(event.iloc[0]["magnitude"]),
            depth: str(event.iloc[0]["depth"]),
            lat: str(event.iloc[0]["latitude"]),
            lon: str(event.iloc[0]["longitude"]),
            strike: str(event.iloc[0]["strike"]),
            rake: str(event.iloc[0]["rake"]),
            dip: str(event.iloc[0]["dip"]),
            event_timestamp: str(quakeml.event2utc(event.iloc[0])),
            event_network: str(event.iloc[0]["Agency"]),
            event_description: "",
            #seed:str(random_seed),
        },
        nsmap=nsmap,
    )

    # write metadata on grid
    # <grid_specification lon_min="-67.910600" lat_min="5.829200" lon_max="-57.910600" lat_max="15.649200" nominal_lon_spacing="0.016667" nominal_lat_spacing="0.016672" nlon="601" nlat="590" />
    lon_min = le.QName("lon_min")
    lat_min = le.QName("lat_min")
    lon_max = le.QName("lon_max")
    lat_max = le.QName("lat_max")
    nominal_lon_spacing = le.QName("nominal_lon_spacing")
    nominal_lat_spacing = le.QName("nominal_lat_spacing")
    nlon = le.QName("nlon")
    nlat = le.QName("nlat")
    reg_grid = le.QName("regular_grid")
    # get plon and plat
    if regular_grid:
        grid_specification = le.SubElement(
            shakeml,
            "grid_specification",
            {
                lon_min: str(grid_data.LON.min()),
                lat_min: str(grid_data.LAT.min()),
                lon_max: str(grid_data.LON.max()),
                lat_max: str(grid_data.LAT.max()),
                nominal_lon_spacing: str(
                    round(abs(np.mean(np.diff(grid_data.LON.unique())[:-1])), 6)
                ),
                nominal_lat_spacing: str(
                    round(abs(np.mean(np.diff(grid_data.LAT.unique())[:-1])), 6)
                ),
                nlon: str(len(grid_data.LON.unique())),
                nlat: str(len(grid_data.LAT.unique())),
                reg_grid: "1",
            },
            nsmap=nsmap,
        )
    else:
        grid_specification = le.SubElement(
            shakeml,
            "grid_specification",
            {
                lon_min: str(grid_data.LON.min()),
                lat_min: str(grid_data.LAT.min()),
                lon_max: str(grid_data.LON.max()),
                lat_max: str(grid_data.LAT.max()),
                reg_grid: "0",
            },
            nsmap=nsmap,
        )


    list_event_specific_uncertainty = []
    name = le.QName("name")
    value = le.QName("value")
    numsta = le.QName("numsta")
    for i in range(
        len(event_specific_uncertainties)
    ):  # ["pga","pgv","mi","psa03","psa10","psa30"]:
        list_event_specific_uncertainty.append(
            le.SubElement(
                shakeml,
                "event_specific_uncertainty",
                {
                    name: str(event_specific_uncertainties.iloc[i]["name"]),
                    value: str(
                        event_specific_uncertainties.iloc[i]["value"]
                    ),
                    numsta: str(
                        event_specific_uncertainties.iloc[i]["numsta"]
                    ),
                },
                nsmap=nsmap,
            )
        )

    # grid field specification
    # <grid_field index="1" name="LON" units="dd" />
    # <grid_field index="2" name="LAT" units="dd" />
    # <grid_field index="3" name="PGA" units="pctg" />
    # <grid_field index="4" name="PGV" units="cms" />
    # <grid_field index="5" name="MMI" units="intensity" />
    # <grid_field index="6" name="PSA03" units="pctg" />
    # <grid_field index="7" name="PSA10" units="pctg" />
    # <grid_field index="8" name="PSA30" units="pctg" />
    # <grid_field index="9" name="STDPGA" units="ln(pctg)" />
    # <grid_field index="10" name="URAT" units="" />
    # <grid_field index="11" name="SVEL" units="ms" />
    index = le.QName("index")
    _name = le.QName("name")
    _units = le.QName("units")
    grid_fields = []
    
    for i, col in enumerate(grid_data.columns):
        
        grid_fields.append(
            le.SubElement(
                shakeml,
                "grid_field",
                {
                    index: str(i + 1),
                    _name: col,
                    _units: str(units.iloc[0][col]),
                },  # starts at 1
                nsmap=nsmap,
            )
        )

    # grid data
    grid_data_out = le.SubElement(shakeml, "grid_data", nsmap=nsmap)
    grid_data_out.text = "\n" + grid_data.to_csv(sep=" ", header=False, index=False)
    # grid_data.text = '\n'+grid_data.to_string(header=False,index=False,justify='left')
    
    # with open(shakemap_outfile, 'w') as f:
    #     f.write(le.tostring(shakeml, pretty_print=True, encoding="unicode"))
    return le.tostring(shakeml, pretty_print=True, encoding="unicode")

    


    
   

