#!/bin/sh

pip install requests
python3 -u /init_wps.py
# Stay running in order to allow us to run some other comamnds if we need it
tail -f /dev/null
