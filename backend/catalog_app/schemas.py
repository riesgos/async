from pydantic import BaseModel

class ProductBase(BaseModel):
    service: str

class Product(ProductBase):
    id: int

    class Config:
        orm_mode = True
