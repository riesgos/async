from typing import List

from fastapi import Depends, APIRouter, HTTPException
from sqlalchemy.orm import Session

from ..dependencies import get_db
from .. import schemas, crud, routers

product_router = APIRouter(prefix="/products")


@product_router.get("/", response_model=List[schemas.Product])
def read_products(skip: int = 0, limit: int = 100, db: Session=Depends(get_db)):
    """Return the list of products."""
    return crud.get_products(db, skip=skip, limit=limit)

@product_router.get("/{product_id}", response_model=schemas.Product)
def read_product(product_id: int, db: Session = Depends(get_db)):
    """Return the product with the given product id or raise 404."""
    db_product = crud.get_product(db, product_id=product_id)
    if db_product is None:
        raise HTTPException(status_code=404, detail="Product not found")
    return db_product
