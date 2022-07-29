from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
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
