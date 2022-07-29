#!/bin/sh

pip install -e .
uvicorn catalog_app.main:app --reload --host 0.0.0.0 --port 8000
