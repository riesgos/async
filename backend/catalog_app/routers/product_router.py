from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

product_router = APIRouter(prefix="/products")


@product_router.get("/", response_model=List[schemas.Product])
def read_list(
    skip: int = 0,
    limit: int = 100,
    product_type_id: Optional[int] = None,
    order_id: Optional[int] = None,
    db: Session = Depends(get_db),
):
    """Return the list of products."""
    return crud.get_products(
        db,
        skip=skip,
        limit=limit,
        product_type_id=product_type_id,
        order_id=order_id,
    )


@product_router.get("/{product_id}", response_model=schemas.Product)
def read_detail(product_id: int, db: Session = Depends(get_db)):
    """Return the job with the given product id or raise 404."""
    db_product = crud.get_product(db, product_id=product_id)
    if db_product is None:
        raise HTTPException(status_code=404, detail="Product not found")
    return db_product
