from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

order_job_ref_router = APIRouter(prefix="/order-job-refs")


@order_job_ref_router.get("/", response_model=List[schemas.OrderJobRef])
def read_list(
    skip: int = 0,
    limit: int = 100,
    order_id: Optional[int] = None,
    job_id: Optional[int] = None,
    db: Session = Depends(get_db),
):
    """Return the list of order job ref."""
    return crud.get_order_job_refs(
        db, skip=skip, limit=limit, order_id=order_id, job_id=job_id
    )


@order_job_ref_router.get("/{order_job_ref_id}", response_model=schemas.OrderJobRef)
def read_detail(order_job_ref_id: int, db: Session = Depends(get_db)):
    """Return the order with the given order job ref id or raise 404."""
    db_order_job_ref = crud.get_order_job_ref(db, order_job_ref_id=order_job_ref_id)
    if db_order_job_ref is None:
        raise HTTPException(status_code=404, detail="Order job ref not found")
    return db_order_job_ref
