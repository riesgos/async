from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

order_constraint_router = APIRouter(prefix="/order-constraints")


@order_constraint_router.get("/", response_model=List[schemas.OrderConstraint])
def read_list(
    skip: int = 0,
    limit: int = 100,
    order_id: Optional[int] = None,
    key: Optional[str] = None,
    db: Session = Depends(get_db),
):
    """Return the list of order constraints."""
    return crud.get_order_constraints(
        db, skip=skip, limit=limit, order_id=order_id, key=key
    )


@order_constraint_router.get(
    "/{order_constraint_id}", response_model=schemas.OrderConstraint
)
def read_detail(order_constraint_id: int, db: Session = Depends(get_db)):
    """Return the order with the given order constraint id or raise 404."""
    db_order_constraint = crud.get_order_constraint(
        db, order_constraint_id=order_constraint_id
    )
    if db_order_constraint is None:
        raise HTTPException(status_code=404, detail="Order constraint not found")
    return db_order_constraint
