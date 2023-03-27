"""
A simple example of how one might go about calling the wps'es from a script
for use in Hugo's uncertainty analysis.
"""

#%%
from owslib.wps import WebProcessingService, monitorExecution, Input, printInputOutput



#%%

gfzUrl = "https://rz-vm140.gfz-potsdam.de/wps/WebProcessingService?service=WPS&version=1.0.0&request=GetCapabilities"
gfzWps = WebProcessingService(gfzUrl, version="1.0.0", verbose=True)

#%%
exposureSvcInfo  = gfzWps.describeprocess("org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess")
fragilitySvcInfo = gfzWps.describeprocess("org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess")
damageSvcInfo    = gfzWps.describeprocess("org.n52.gfz.riesgos.algorithm.impl.DeusProcess")
eqSvcInfo        = gfzWps.describeprocess("org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess")
# sysrelSvcInfo    = ftnJavaWps.describeprocess("org.n52.gfz.riesgos.algorithm.impl.SystemReliabilitySingleProcess")
# tsSvcInfo        = ftnPyWps.describeprocess("get_scenario")

# %%
# inputs = []
# for input in assetmasterInfo.dataInputs:
#     printInputOutput(input)
#     if input.defaultValue:
#         inputs.append((input.identifier, input.defaultValue))
#     else:
#         inputs.append((input.identifier, input.allowedValues[0]))


#%%
inputs = [
    ("lonmin", "-71.8"),
    ("lonmax", "-71.4"),
    ("latmin", "-33.2"),
    ("latmax", "-33.0"),
    ("schema", "SARA_v1.0"),
    ("assettype", "res"),
    ("querymode", "intersects"),
    ("model", "LimaCVT1_PD30_TI70_5000")
]
results = gfzWps.execute("org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess", inputs, mode='sync')


# %%
