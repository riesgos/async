from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

product_type_router = APIRouter(prefix="/product-types")


@product_type_router.get("/", response_model=List[schemas.ProductType])
def read_list(
    skip: int = 0,
    limit: int = 100,
    db: Session = Depends(get_db),
):
    """Return the list of product types."""
    return crud.get_product_types(
        db,
        skip=skip,
        limit=limit,
    )


@product_type_router.get("/{product_type_id}", response_model=schemas.ProductType)
def read_detail(product_type_id: int, db: Session = Depends(get_db)):
    """Return the job with the given product type id or raise 404."""
    db_product_type = crud.get_product_type(db, product_type_id=product_type_id)
    if db_product_type is None:
        raise HTTPException(status_code=404, detail="Product type not found")
    return db_product_type
