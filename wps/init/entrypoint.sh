#!/bin/sh

python3 /init_wps.py
# Stay running in order to allow us to run some other comamnds if we need it
tail -f /dev/null
