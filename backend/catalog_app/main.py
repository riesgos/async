from typing import List
from contextlib import contextmanager

from fastapi import APIRouter, Depends, FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session

from . import crud, routers, schemas, models
from .config import Config
from .dependencies import get_db


# Read with pydantic
config = Config()

app = FastAPI(
    openapi_url=f"{config.root_path}/openapi.json",
    docs_url=f"{config.root_path}/docs",
    redoc_url=f"{config.root_path}/redoc",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


prefix_router = APIRouter(prefix=config.root_path)
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
prefix_router.include_router(routers.product_router)
prefix_router.include_router(routers.product_type_router)
prefix_router.include_router(routers.user_router)
app.include_router(prefix_router)


@app.on_event("startup")
def insert_initial_user():
    with contextmanager(get_db)() as db:
        if config.initial_user_email and config.initial_user_password:
            password_hash = models.User.generate_new_password_hash(
                config.initial_user_password
            )
            existing_user = crud.get_user_by_email(db, config.initial_user_email)
            if existing_user:
                existing_user.password_hash = password_hash
                crud.update_user(db, existing_user)
            else:
                new_user = models.User(
                    email=config.initial_user_email,
                    password_hash=password_hash,
                    apikey=models.User.generate_new_apikey(),
                )
                crud.create_user(db, new_user)
