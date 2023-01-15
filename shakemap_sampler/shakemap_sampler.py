#!/usr/bin/env python3

# Copyright © 2021-2022 Helmholtz Centre Potsdam GFZ German Research Centre for
# Geosciences, Potsdam, Germany
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
"""
This is the Shakemap-Sampler-Service.

"""

import argparse
import glob
import os

import sampler



def main(intensity_file, random_seed):

    current_dir = os.path.dirname(os.path.realpath(__file__))
    file_name = os.path.join(current_dir, intensity_file)
    random_seed = int(random_seed)

    correlated = True
    event,columns,units,grid_data, event_specific_uncertainties, regular_grid = sampler.extract_shakemap_data(file_name)
    if correlated:
        grid_data,columns,units = sampler.create_correlated_residuals(grid_data,columns,units,random_seed)
    else:
        grid_data,columns,units = sampler.create_uncorrelated_residuals(grid_data,columns,units,random_seed)
  
    result = sampler.build_random_shakemap(event,columns,units,grid_data, event_specific_uncertainties,regular_grid,random_seed)
    # print("<?xml version='1.0' encoding='UTF-8'?>" + result.strip())
    print(result)

if __name__ == "__main__":
    # parser = argparse.ArgumentParser(
    #     description="Return resampled shakemap for shakemap input"
    # )
    # parser.add_argument("random_seed", help="random_seed")
    # parser.add_argument("intensity_file", help="intensity_file")
    # args = parser.parse_args()

    # main(args.intensity_file, args.random_seed)


    output = """<?xml version='1.0' encoding='UTF-8'?><ns1:shakemap_grid xmlns:ns1="http://earthquake.usgs.gov/eqcenter/shakemap" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://earthquake.usgs.gov/eqcenter/shakemap" code_version="shakyground 0.1" event_id="quakeml:quakeledger/466776" map_status="RELEASED" process_timestamp="2019-08-08T12:31:27.890823Z" shakemap_event_type="stochastic" shakemap_id="quakeml:quakeledger/466776" shakemap_originator="GFZ" shakemap_version="1" xsi:schemaLocation="http://earthquake.usgs.gov http://earthquake.usgs.gov/eqcenter/shakemap/xml/schemas/shakemap.xsd"><event depth="97.99416" event_description="" event_id="quakeml:quakeledger/466776" event_network="nan" event_timestamp="90175-01-01T00:00:00.000000Z" lat="-33.60348" lon="-70.43199" magnitude="7.4"/><grid_specification lat_max="-32.65" lat_min="-34.5583333333" lon_max="-69.6416666667" lon_min="-71.225" nlat="230" nlon="191" nominal_lat_spacing="0.008333" nominal_lon_spacing="0.008333" regular_grid="1"/><event_specific_uncertainty name="pga" numsta="" value="0.0"/><event_specific_uncertainty name="pgv" numsta="" value="0.0"/><event_specific_uncertainty name="mi" numsta="" value="0.0"/><event_specific_uncertainty name="psa03" numsta="" value="0.0"/><event_specific_uncertainty name="psa10" numsta="" value="0.0"/><event_specific_uncertainty name="psa30" numsta="" value="0.0"/><grid_field index="1" name="LON" units="dd"/><grid_field index="2" name="LAT" units="dd"/><grid_field index="3" name="PGA" units="g"/><grid_field index="4" name="STDPGA" units="g"/><grid_data>
-71.21666666670001 -32.65 0.056063764 0.7362585
-71.2083333333 -32.65 0.057824578 0.7362585
-71.2 -32.65 0.06327598 0.7362585
-71.1916666667 -32.65 0.059195064 0.7362585
-71.1833333333 -32.65 0.053293496 0.7362585
-71.175 -32.65 0.054376625 0.7362585
-69.65 -34.5583333333 0.053477768 0.7362585
-69.6416666667 -34.5583333333 0.05383628 0.7362585
</grid_data></ns1:shakemap_grid>
"""
    print(output)
