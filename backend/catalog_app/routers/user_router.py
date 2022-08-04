import binascii
import hashlib
import os
from base64 import b64encode
from typing import List

from fastapi import APIRouter, Depends, Header, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db
from ..models import User

user_router = APIRouter(prefix="/users")


@user_router.get("/", response_model=List[schemas.User])
def read_list(
    skip: int = 0,
    limit: int = 100,
    db: Session = Depends(get_db),
    x_apikey=Header(default=None),
):
    """Return the list of users."""
    current_user = crud.get_user_by_apikey(db, apikey=x_apikey)
    if not current_user:
        return []
    if not current_user.superuser:
        return [current_user]
    return crud.get_users(db, skip=skip, limit=limit)


@user_router.get("/{user_id}", response_model=schemas.User)
def read_detail(
    user_id: int, db: Session = Depends(get_db), x_apikey=Header(default=None)
):
    """Return the user with the given user id or raise 404."""
    if not x_apikey:
        raise HTTPException(status_code=401, detail="Authentification needed")
    current_user = crud.get_user_by_apikey(db, apikey=x_apikey)
    if not current_user:
        raise HTTPException(status_code=401, detail="Authentification needed")
    if not current_user.superuser:
        if current_user.id != user_id:
            raise HTTPException(
                status_code=403, detail="No permission to see user details"
            )
    db_user = crud.get_user(db, user_id=user_id)
    if db_user is None:
        raise HTTPException(status_code=404, detail="User not found")
    return db_user


@user_router.post("/register", response_model=schemas.User)
def register_user(
    user_credentials: schemas.UserCredentials, db: Session = Depends(get_db)
):
    existing_db_user = crud.get_user_by_email(db, email=user_credentials.email)
    if not user_credentials.password:
        raise HTTPException(status_code=400, detail="Password must be set")
    if existing_db_user is not None:
        raise HTTPException(status_code=409, detail="User exists already")
    # We create a new password.
    pw_salt = b64encode(os.urandom(8)).decode("ascii")
    pw_hash = b64encode(
        hashlib.new(
            "sha256",
            bytes(pw_salt + ":" + user_credentials.password, "utf-8"),
        ).digest()
    ).decode("ascii")
    password_hash = pw_salt + ":" + pw_hash

    apikey = binascii.b2a_hex(os.urandom(16)).decode("ascii")
    user = crud.create_user(
        db,
        User(email=user_credentials.email, password_hash=password_hash, apikey=apikey),
    )
    return user


@user_router.post("/login", response_model=schemas.UserSelfInformation)
def login_user(
    user_credentials: schemas.UserCredentials, db: Session = Depends(get_db)
):
    existing_db_user = crud.get_user_by_email(db, email=user_credentials.email)
    if not existing_db_user:
        raise HTTPException(status_code=400, detail="Wrong credentials")
    pw_salt, check_pw_hash = existing_db_user.password_hash.split(":", 1)
    pw_hash = b64encode(
        hashlib.new(
            "sha256",
            bytes(pw_salt + ":" + user_credentials.password, "utf-8"),
        ).digest()
    ).decode("ascii")
    if not pw_hash == check_pw_hash:
        raise HTTPException(status_code=400, detail="Wrong credentials")
    return schemas.UserSelfInformation(
        id=existing_db_user.id,
        email=existing_db_user.email,
        apikey=existing_db_user.apikey,
    )
