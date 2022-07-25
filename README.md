# RIESGOS 2.0: Async sprint

## User stories

 - As a user, I want to see past results in a catalogue, 
    - so that I have an overview of the spread of possible values 
 - As a user, I want to be able to request new data by specifying parameters like: "I'm interested in eq's of magnitude 7.5 and using the USGS-grid", *leaving out* some other parameters, 
    - so that I can fill any gaps in the catalogue where I'm interested in data that isn't already there.


## Architecture

- One central message-bus (pulsar)
- One database listing:
   - product-id
   - data (or link to location of data)
   - sources: list of product-id's of all the products that have been used to calculate this data
   - configuration-values?
- WPS'es wrapped in [wrapper](https://github.com/arnevogt/asyncwrapper)
