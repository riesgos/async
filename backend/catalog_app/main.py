from typing import List

from environs import Env
from fastapi import APIRouter, Depends, FastAPI, HTTPException, Request
from sqlalchemy.orm import Session

from . import crud, routers, schemas
from .dependencies import get_db

env = Env()
ROOT_PATH = env.str("ROOT_PATH", "/")

app = FastAPI(
    openapi_url=f"{ROOT_PATH}/openapi.json",
    docs_url=f"{ROOT_PATH}/docs",
    redoc_url=f"{ROOT_PATH}/redoc",
)
prefix_router = APIRouter(prefix=ROOT_PATH)
prefix_router.include_router(routers.bbox_input_router)
prefix_router.include_router(routers.complex_input_router)
prefix_router.include_router(routers.complex_input_as_value_router)
prefix_router.include_router(routers.complex_output_router)
prefix_router.include_router(routers.complex_output_as_input_router)
prefix_router.include_router(routers.job_router)
prefix_router.include_router(routers.literal_input_router)
prefix_router.include_router(routers.order_job_ref_router)
prefix_router.include_router(routers.order_router)
prefix_router.include_router(routers.process_router)
prefix_router.include_router(routers.user_router)
app.include_router(prefix_router)
