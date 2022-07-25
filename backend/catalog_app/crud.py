from sqlalchemy.orm import Session
from .models import Product

def get_products(db: Session, skip: int = 0, limit: int=100):
    return db.query(Product).offset(skip).limit(limit).all()

def get_product(db: Session, product_id: int):
    return db.query(Product).filter(Product.id == product_id).first()
