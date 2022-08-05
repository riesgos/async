from typing import Optional

from pydantic import BaseModel


class BboxInputBase(BaseModel):
    job_id: int
    wps_identifier: str
    lower_corner_x: float
    lower_corner_y: float
    upper_corner_x: float
    upper_corner_y: float
    crs: str


class BboxInput(BboxInputBase):
    id: int

    class Config:
        orm_mode = True


class ComplexInputBase(BaseModel):
    job_id: int
    wps_identifier: str
    link: str
    mime_type: str
    xmlschema: Optional[str]
    encoding: str


class ComplexInput(ComplexInputBase):
    id: int

    class Config:
        orm_mode = True


class ComplexInputAsValueBase(BaseModel):
    job_id: int
    wps_identifier: str
    input_value: str
    mime_type: str
    xmlschema: Optional[str]
    encoding: str


class ComplexInputAsValue(ComplexInputAsValueBase):
    id: int

    class Config:
        orm_mode = True


class ComplexOutputBase(BaseModel):
    job_id: int
    wps_identifier: str
    link: str
    mime_type: str
    xmlschema: Optional[str]
    encoding: str


class ComplexOutput(ComplexOutputBase):
    id: int

    class Config:
        orm_mode = True


class ComplexOutputAsInputBase(BaseModel):
    job_id: int
    wps_identifier: str
    complex_output_id: int


class ComplexOutputAsInput(ComplexOutputAsInputBase):
    id: int

    class Config:
        orm_mode = True


class JobBase(BaseModel):
    process_id: int
    status: str


class Job(JobBase):
    id: int

    class Config:
        orm_mode = True


class LiteralInputBase(BaseModel):
    job_id: int
    wps_identifier: str
    input_value: str


class LiteralInput(LiteralInputBase):
    id: int

    class Config:
        orm_mode = True


class OrderBase(BaseModel):
    user_id: int
    order_constraints: dict


class Order(OrderBase):
    id: int

    class Config:
        orm_mode = True


class OrderPost(BaseModel):
    order_constraints: dict


class OrderJobRefBase(BaseModel):
    order_id: int
    job_id: int


class OrderJobRef(OrderJobRefBase):
    id: int

    class Config:
        orm_mode = True


class ProcessBase(BaseModel):
    wps_url: str
    wps_identifier: str


class Process(ProcessBase):
    id: int

    class Config:
        orm_mode = True


class UserBase(BaseModel):
    email: str


class User(UserBase):
    id: int

    class Config:
        orm_mode = True


class ProductType(BaseModel):
    id: int
    name: str


class Product(BaseModel):
    id: int
    product_type_id: int
    name: str


class UserCredentials(BaseModel):
    email: str
    password: str


class UserSelfInformation(BaseModel):
    id: int
    email: str
    apikey: str
