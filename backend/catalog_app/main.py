from typing import List

from environs import Env
from fastapi import FastAPI, Request, Depends, APIRouter, HTTPException
from sqlalchemy.orm import Session

from .dependencies import get_db
from . import schemas, crud, routers

env = Env()
ROOT_PATH = env.str("ROOT_PATH", "/")

app = FastAPI(
    openapi_url=f"{ROOT_PATH}/openapi.json",
    docs_url=f"{ROOT_PATH}/docs",
    redoc_url=f"{ROOT_PATH}/redoc",
)
prefix_router = APIRouter(prefix=ROOT_PATH)
prefix_router.include_router(routers.product_router)
app.include_router(prefix_router)
