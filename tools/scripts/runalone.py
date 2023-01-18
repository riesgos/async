"""
A simple example of how one might go about calling the wps'es from a script
for use in Hugo's uncertainty analysis.
"""

#%%
from owslib.wps import WebProcessingService, monitorExecution



#%%

gfzUrl = "https://rz-vm140.gfz-potsdam.de/wps/WebProcessingService?service=WPS&version=1.0.0&request=GetCapabilities"
ftnUrl = "https://riesgos.52north.org/javaps/service?service=WPS&version=2.0.0&request=GetCapabilities"

gfzWps = WebProcessingService(gfzUrl, version="1.0.0", verbose=True)
ftnWps = WebProcessingService(ftnUrl, version="2.0.0", verbose=True)

#%%
assetmasterInfo = gfzWps.describeprocess("org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess")

# %%
inputs = []
for input in assetmasterInfo.dataInputs:
    if input.defaultValue:
        inputs.append((input.identifier, input.defaultValue))
    else:
        inputs.append((input.identifier, input.allowedValues[0]))


#%%
results = gfzWps.execute("org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess", inputs, mode='sync')


# %%
