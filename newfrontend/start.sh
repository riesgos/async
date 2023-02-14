#!/bin/sh

npm ci
npm run ng serve -- --host=0.0.0.0 --configuration=development,behindProxy  --disable-host-check