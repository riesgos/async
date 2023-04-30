#%%
import csv
import json

# %%
fh = open('precalculated.csv', 'r')
reader = csv.DictReader(fh, delimiter=';')
entries = [entry for entry in reader]
entries[0]

#%%

def rename(entry):
    return {
        'id': entry['ID'],
        'eventId': entry['eventID'],
        'longitude': entry['longitude'],
        'latitude': entry['latitude'],
        'depth': entry['depth'],
        'magnitude': entry['magnitude'],
        'rakeAngle': entry['rake'],
        'dipAngle': entry['dip'],
        'strikeAngle': entry['strike'],
        'seed': entry['seeds_gm'],
        'exposureModel': entry['exposure_model'],
        'vulnerabilityEq': entry['vulnerability_model_eq'],
        'vulnerabilityTs': entry['vulnerability_model_ts'],
    }

renamedEntries = [rename(entry) for entry in entries]

#%%

def isInt(strval):
    return "," not in strval and strval.replace('-', '').isdigit()

def toInt(strval):
    return int(strval)

def isFloat(strval):
    return "," in strval and strval.replace(',', '').replace('-', '').isdigit()

def toFloat(strval):
    return float(strval.replace(",", "."))

def format(entry):
    for key, value in entry.items():
        if isInt(value):
            value = toInt(value)
        elif isFloat(value):
            value = toFloat(value)
        entry[key] = value
    return entry

formattedEntries = [format(entry) for entry in renamedEntries]

#%%
fh = open('precalculated.json', 'w')
json.dump(renamedEntries, fh)

# %%
