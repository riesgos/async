from typing import Optional

from sqlalchemy.orm import Session

from .models import (
    BboxInput,
    ComplexInput,
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
    db: Session, skip: int = 0, limit: int = 100, wps_identifier: Optional[str] = None
):
    query = db.query(Process)
    if wps_identifier is not None:
        query = query.filter(Process.wps_identifier == wps_identifier)
    return query.offset(skip).limit(limit).all()


def get_process(db: Session, process_id: int):
    return db.query(Process).filter(Process.id == process_id).first()


def get_users(db: Session, skip: int = 0, limit: int = 100):
    return db.query(User).offset(skip).limit(limit).all()


def get_user(db: Session, user_id: int):
    return db.query(User).filter(User.id == user_id).first()
