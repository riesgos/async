import datetime

import pytz


def utc_now():
    return pytz.utc.localize(datetime.datetime.now())
