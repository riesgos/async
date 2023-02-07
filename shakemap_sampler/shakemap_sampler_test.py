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
import os
import lxml.etree as le
import pandas
import io
import datetime
import sampler
from openquake.hazardlib.imt import PGA, PGV, IA, SA
import openquake.hazardlib.correlation as crl
from openquake.hazardlib.site import Site, SiteCollection
from openquake.hazardlib.geo import Mesh, Point
import pylab 
import scipy.stats as stats
import scipy.linalg as spla
"""
Reads a normal shakemap (as it is the output of shakyground for earthquake scenarios)
:return: The shakemap with the random residuals (separated and mixed)
"""

    

#local testing
#change this directory where the test file shakemap.xml is located
this_dir = os.path.dirname(os.path.realpath(__file__))
file_name = os.path.join(this_dir, "testinputs", "shakemap.xml")
shakemap_outfile = os.path.join(this_dir, "out_shakemap_uncorr.xml")
shakemap_outfile_corr = os.path.join(this_dir, "out_shakemap_corr.xml")
random_seed = 123
event,columns,units,grid_data, event_specific_uncertainties, regular_grid = sampler.extract_shakemap_data(file_name)
grid_data,columns,units = sampler.create_uncorrelated_residuals(grid_data,columns,units,random_seed)

sampler.save_random_shakemap(shakemap_outfile,event,columns,units,grid_data, event_specific_uncertainties,regular_grid,random_seed)
#L=sampler.build_sparse_correlation_matrix(grid_data,PGA,tol=1e-2)
grid_data,columns,units = sampler.create_correlated_residuals(grid_data,columns,units,random_seed)
sampler.save_random_shakemap(shakemap_outfile_corr,event,columns,units,grid_data, event_specific_uncertainties,regular_grid,random_seed)

 
#test correlation model
print('test')
rand_seed=1234578
rand_idx=np.random.permutation(grid_data.index)   
npts=5000
first_idx=[rand_idx[i] for i in range(npts)] 
gx=grid_data['LAT'][first_idx]
gy=grid_data['LON'][first_idx]
rpga=grid_data['RESPGA'][first_idx]#obtain the residuals
sites = SiteCollection([Site(Point(x,y), 1, vs30measured=True, z1pt0=3.4, z2pt5=5.6, backarc=False) for x, y  in zip(gx, gy)])#dummy vs30. not needed in this test
corrmat=crl.jbcorrelation(sites, PGA, True)
print('corr mat done')
Lcorrmat=spla.sqrtm(corrmat)#np.linalg.cholesky(corrmat)
print('cholesky done')
Lcorrmatinv=np.linalg.inv(Lcorrmat)
print('inverse done')
Lcorrmatinv=[[Lcorrmatinv[i,j] for j in range(npts)] for i in range(npts)]
print('rebuild inverse done')
std_residuals=np.dot(Lcorrmatinv,list(rpga.array))#these should be independent, standard normal distributed random variables 
#print('mean: '+str(np.mean(std_residuals))+' std dev: '+str(np.std(std_residuals)))
stats.probplot(std_residuals, dist="norm", plot=pylab)
pylab.show()
