from typing import Optional

from sqlalchemy.orm import Session

from . import schemas
from .models import (
    BboxInput,
    ComplexInput,
    ComplexInputAsValue,
    ComplexOutput,
    ComplexOutputAsInput,
    Job,
    LiteralInput,
    Order,
    OrderJobRef,
    Process,
    User,
)


def get_bbox_inputs(
    db: Session,
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[int] = None,
):
    query = db.query(BboxInput)
    if wps_identifier is not None:
        query = query.filter(BboxInput.wps_identifier == wps_identifier)
    if job_id is not None:
        query = query.filter(BboxInput.job_id == job_id)
    return query.offset(skip).limit(limit).all()


def get_bbox_input(db: Session, bbox_input_id: int):
    return db.query(BboxInput).filter(BboxInput.id == bbox_input_id).first()


def get_complex_inputs(
    db: Session,
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[int] = None,
):
    query = db.query(ComplexInput)
    if wps_identifier is not None:
        query = query.filter(ComplexInput.wps_identifier == wps_identifier)
    if job_id is not None:
        query = query.filter(ComplexInput.job_id == job_id)
    return query.offset(skip).limit(limit).all()


def get_complex_input(db: Session, complex_input_id: int):
    return db.query(ComplexInput).filter(ComplexInput.id == complex_input_id).first()


def get_complex_inputs_as_values(
    db: Session,
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[int] = None,
):
    query = db.query(ComplexInputAsValue)
    if wps_identifier is not None:
        query = query.filter(ComplexInputAsValue.wps_identifier == wps_identifier)
    if job_id is not None:
        query = query.filter(ComplexInputAsValue.job_id == job_id)
    return query.offset(skip).limit(limit).all()


def get_complex_input_as_value(db: Session, complex_input_as_value_id: int):
    return (
        db.query(ComplexInputAsValue)
        .filter(ComplexInputAsValue.id == complex_input_as_value_id)
        .first()
    )


def get_complex_outputs(
    db: Session,
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[int] = None,
):
    query = db.query(ComplexOutput)
    if wps_identifier is not None:
        query = query.filter(ComplexOutput.wps_identifier == wps_identifier)
    if job_id is not None:
        query = query.filter(ComplexOutput.job_id == job_id)
    return query.offset(skip).limit(limit).all()


def get_complex_output(db: Session, complex_output_id: int):
    return db.query(ComplexOutput).filter(ComplexOutput.id == complex_output_id).first()


def get_complex_outputs_as_inputs(
    db: Session,
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[int] = None,
):
    query = db.query(ComplexOutputAsInput)
    if wps_identifier is not None:
        query = query.filter(ComplexOutputAsInput.wps_identifier == wps_identifier)
    if job_id is not None:
        query = query.filter(ComplexOutputAsInput.job_id == job_id)
    return query.offset(skip).limit(limit).all()


def get_complex_output_as_input(db: Session, complex_output_as_input_id: int):
    return (
        db.query(ComplexOutputAsInput)
        .filter(ComplexOutputAsInput.id == complex_output_as_input_id)
        .first()
    )


def get_jobs(
    db: Session,
    skip: int = 0,
    limit: int = 100,
    process_id: Optional[int] = None,
    status: Optional[str] = None,
    order_id: Optional[int] = None,
):
    query = db.query(Job)
    if process_id is not None:
        query = query.filter(Job.process_id == process_id)
    if status is not None:
        query = query.filter(Job.status == status)
    if order_id is not None:
        query = query.join(OrderJobRef).filter(OrderJobRef.order_id == order_id)
    return query.offset(skip).limit(limit).all()


def get_job(db: Session, job_id: int):
    return db.query(Job).filter(Job.id == job_id).first()


def get_literal_inputs(
    db: Session,
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[int] = None,
):
    query = db.query(LiteralInput)
    if wps_identifier is not None:
        query = query.filter(LiteralInput.wps_identifier == wps_identifier)
    if job_id is not None:
        query = query.filter(LiteralInput.job_id == job_id)
    return query.offset(skip).limit(limit).all()


def get_literal_input(db: Session, literal_input_id: int):
    return db.query(LiteralInput).filter(LiteralInput.id == literal_input_id).first()


def get_orders(
    db: Session, skip: int = 0, limit: int = 100, user_id: Optional[int] = None
):
    query = db.query(Order)
    if user_id is not None:
        query = query.filter(Order.user_id == user_id)
    return query.offset(skip).limit(limit).all()


def get_order(db: Session, order_id: int):
    return db.query(Order).filter(Order.id == order_id).first()


def get_order_job_refs(
    db: Session,
    skip: int = 0,
    limit: int = 100,
    order_id: Optional[int] = None,
    job_id: Optional[int] = None,
):
    query = db.query(OrderJobRef)
    if order_id is not None:
        query = query.filter(OrderJobRef.order_id == order_id)
    if job_id is not None:
        query = query.filter(OrderJobRef.job_id == job_id)
    return query.offset(skip).limit(limit).all()


def get_order_job_ref(db: Session, order_job_ref_id: int):
    return db.query(OrderJobRef).filter(OrderJobRef.id == order_job_ref_id).first()


def get_processes(
    db: Session,
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    wps_url: Optional[str] = None,
):
    query = db.query(Process)
    if wps_identifier is not None:
        query = query.filter(Process.wps_identifier == wps_identifier)
    if wps_url is not None:
        query = query.filter(Process.wps_url == wps_url)
    return query.offset(skip).limit(limit).all()


def get_process(db: Session, process_id: int):
    return db.query(Process).filter(Process.id == process_id).first()


def get_users(db: Session, skip: int = 0, limit: int = 100):
    return db.query(User).offset(skip).limit(limit).all()


def get_user(db: Session, user_id: int):
    return db.query(User).filter(User.id == user_id).first()


def get_product_types(db: Session, skip: int = 0, limit: int = 100):
    processes = (
        db.query(Process)
        .join(Process.jobs)
        .filter_by(status="Succeeded")
        .offset(skip)
        .limit(limit)
        .all()
    )
    result = []
    for process in processes:
        result.append(
            schemas.ProductType(name=process.wps_identifier + " output", id=process.id)
        )
    return result


def get_product_type(db: Session, product_type_id: int):
    process = (
        db.query(Process)
        .filter(Process.id == product_type_id)
        .join(Process.jobs)
        .filter_by(status="Succeeded")
        .first()
    )
    if not process:
        return None
    return schemas.ProductType(name=process.wps_identifier + " output", id=process.id)


def get_products(
    db: Session,
    skip: int = 0,
    limit: int = 0,
    product_type_id: Optional[int] = None,
    order_id: Optional[int] = None,
):
    query = db.query(Job).filter(Job.status == "Succeeded").join(Job.process)
    if product_type_id is not None:
        query = query.filter(Job.process_id == product_type_id)
    if order_id is not None:
        query = query.join(OrderJobRef).filter(OrderJobRef.order_id == order_id)
    jobs = query.offset(skip).limit(100)
    result = []
    for job in jobs:
        result.append(
            schemas.Product(
                id=job.id,
                product_type_id=job.process_id,
                name=f"{job.process.wps_identifier} output ({job.id})",
            )
        )
    return result


def get_product(db: Session, product_id: int):
    job = (
        db.query(Job)
        .join(Job.process)
        .filter(Job.id == product_id)
        .filter(Job.status == "Succeeded")
        .first()
    )
    if not job:
        return None
    return schemas.Product(
        id=job.id,
        product_type_id=job.process_id,
        name=f"{job.process.wps_identifier} output ({job.id})",
    )


def get_derived_products(
    db: Session,
    product_id: int,
    skip: int = 0,
    limit: int = 0,
):
    query = (
        db.query(Job)
        .filter(Job.status == "Succeeded")
        .join(Job.process)
        .join(Job.complex_outputs_as_inputs)
        .join(ComplexOutputAsInput.complex_output)
        .filter(ComplexOutput.job_id == product_id)
    )
    jobs = query.offset(skip).limit(100)
    result = []
    for job in jobs:
        result.append(
            schemas.Product(
                id=job.id,
                product_type_id=job.process_id,
                name=f"{job.process.wps_identifier} output ({job.id})",
            )
        )
    return result


def get_base_products(
    db: Session,
    product_id: int,
    skip: int = 0,
    limit: int = 0,
):
    query = (
        db.query(Job)
        .filter(Job.status == "Succeeded")
        .join(Job.process)
        .join(Job.complex_outputs)
        .join(ComplexOutput.inputs)
        .filter(ComplexOutputAsInput.job_id == product_id)
    )
    jobs = query.offset(skip).limit(100)
    result = []
    for job in jobs:
        result.append(
            schemas.Product(
                id=job.id,
                product_type_id=job.process_id,
                name=f"{job.process.wps_identifier} output ({job.id})",
            )
        )
    return result
