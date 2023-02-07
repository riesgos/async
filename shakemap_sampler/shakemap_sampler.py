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
import sys
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

    random_seed = 1234
    intensity_file = 'intensity_file.xml'  # riesgos-wps places this file automagically in this directory (but not passed as cli-argument)

    sysargs = sys.argv
    if len(sysargs) > 1:
        random_seed = float(sysargs[1])              # riesgos-wps passes this as a cli-argument (as string: "123.456")

    main(intensity_file, random_seed)

