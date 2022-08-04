from typing import List, Optional

from fastapi import APIRouter, Depends, Header, HTTPException
from sqlalchemy.orm import Session

from .. import crud, models, routers, schemas
from ..dependencies import get_db

order_router = APIRouter(prefix="/orders")


@order_router.get("/", response_model=List[schemas.Order])
def read_list(
    skip: int = 0,
    limit: int = 100,
    user_id: Optional[int] = None,
    db: Session = Depends(get_db),
):
    """Return the list of orders."""
    return crud.get_orders(db, skip=skip, limit=limit, user_id=user_id)


@order_router.get("/{order_id}", response_model=schemas.Order)
def read_detail(order_id: int, db: Session = Depends(get_db)):
    """Return the order with the given order id or raise 404."""
    db_order = crud.get_order(db, order_id=order_id)
    if db_order is None:
        raise HTTPException(status_code=404, detail="Order not found")
    return db_order


@order_router.post("/", response_model=schemas.Order)
def create_order(
    order: schemas.OrderPost,
    db: Session = Depends(get_db),
    x_apikey=Header(default=None),
):
    if not x_apikey:
        raise HTTPException(status_code=401, detail="Authentification needed")
    current_user = crud.get_user_by_apikey(db, apikey=x_apikey)
    if not current_user:
        raise HTTPException(status_code=401, detail="Authentification needed")
    new_order = models.Order(
        user_id=current_user.id, order_constraints=order.order_constraints
    )
    return crud.create_order(db, new_order)
