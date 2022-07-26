from typing import List

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

user_router = APIRouter(prefix="/users")


@user_router.get("/", response_model=List[schemas.User])
def read_list(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    """Return the list of users."""
    return crud.get_users(db, skip=skip, limit=limit)


@user_router.get("/{user_id}", response_model=schemas.User)
def read_detail(user_id: int, db: Session = Depends(get_db)):
    """Return the user with the given user id or raise 404."""
    db_user = crud.get_user(db, user_id=user_id)
    if db_user is None:
        raise HTTPException(status_code=404, detail="User not found")
    return db_user
