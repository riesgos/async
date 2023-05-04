import base64
import binascii
import hashlib
import os

from sqlalchemy import (
    JSON,
    Boolean,
    Column,
    DateTime,
    Float,
    ForeignKey,
    Integer,
    String,
    Text,
)
from sqlalchemy.orm import relationship

from .database import Base
from .utils import utc_now


class Process(Base):
    __tablename__ = "processes"
    id = Column(Integer, primary_key=True)
    wps_url = Column(String(256))
    wps_identifier = Column(String(256))
    created_at = Column(DateTime(timezone=True), default=utc_now, nullable=False)
    jobs = relationship("Job", back_populates="process")


class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True)
    email = Column(String(256))
    password_hash = Column(String(256))
    apikey = Column(String(256))
    superuser = Column(Boolean, default=False)
    created_at = Column(DateTime(timezone=True), default=utc_now, nullable=False)
    orders = relationship("Order", back_populates="user")

    @staticmethod
    def generate_new_password_hash(password):
        pw_salt = base64.b64encode(os.urandom(8)).decode("ascii")
        pw_hash = base64.b64encode(
            hashlib.new(
                "sha256",
                bytes(pw_salt + ":" + password, "utf-8"),
            ).digest()
        ).decode("ascii")
        password_hash = pw_salt + ":" + pw_hash
        return password_hash

    @staticmethod
    def generate_new_apikey():
        return binascii.b2a_hex(os.urandom(16)).decode("ascii")

    @staticmethod
    def is_password_hash(password, password_hash):
        pw_salt, check_pw_hash = password_hash.split(":", 1)
        pw_hash = base64.b64encode(
            hashlib.new(
                "sha256",
                bytes(pw_salt + ":" + password, "utf-8"),
            ).digest()
        ).decode("ascii")
        return pw_hash == check_pw_hash


class Order(Base):
    __tablename__ = "orders"
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    user = relationship("User", back_populates="orders")
    order_constraints = Column(JSON)
    created_at = Column(DateTime(timezone=True), default=utc_now, nullable=False)
    order_job_refs = relationship("OrderJobRef", back_populates="order")


class Job(Base):
    __tablename__ = "jobs"
    id = Column(Integer, primary_key=True)
    process_id = Column(Integer, ForeignKey("processes.id"))
    process = relationship("Process", back_populates="jobs")
    status = Column(String(16))
    created_at = Column(DateTime(timezone=True), default=utc_now, nullable=False)

    order_job_refs = relationship("OrderJobRef", back_populates="job")
    complex_outputs = relationship("ComplexOutput", back_populates="job")
    complex_outputs_as_inputs = relationship(
        "ComplexOutputAsInput", back_populates="job"
    )
    complex_inputs = relationship("ComplexInput", back_populates="job")
    complex_inputs_as_values = relationship("ComplexInputAsValue", back_populates="job")
    literal_inputs = relationship("LiteralInput", back_populates="job")
    bbox_inputs = relationship("BboxInput", back_populates="job")


class OrderJobRef(Base):
    __tablename__ = "order_job_refs"
    id = Column(Integer, primary_key=True)
    order_id = Column(Integer, ForeignKey("orders.id"))
    order = relationship("Order", back_populates="order_job_refs")
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="order_job_refs")
    created_at = Column(DateTime(timezone=True), default=utc_now, nullable=False)


class ComplexOutput(Base):
    __tablename__ = "complex_outputs"
    id = Column(Integer, primary_key=True)
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="complex_outputs")
    wps_identifier = Column(String(256))
    link = Column(String(1024))
    mime_type = Column(String(64))
    xmlschema = Column(String(256))
    encoding = Column(String(16))
    created_at = Column(DateTime(timezone=True), default=utc_now, nullable=False)
    inputs = relationship("ComplexOutputAsInput", back_populates="complex_output")


class ComplexOutputAsInput(Base):
    __tablename__ = "complex_outputs_as_inputs"
    id = Column(Integer, primary_key=True)
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="complex_outputs_as_inputs")
    wps_identifier = Column(String(256))
    complex_output_id = Column(Integer, ForeignKey("complex_outputs.id"))
    complex_output = relationship("ComplexOutput", back_populates="inputs")
    created_at = Column(DateTime(timezone=True), default=utc_now, nullable=False)


class ComplexInput(Base):
    __tablename__ = "complex_inputs"
    id = Column(Integer, primary_key=True)
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="complex_inputs")
    wps_identifier = Column(String(256))
    link = Column(String(1024))
    mime_type = Column(String(64))
    xmlschema = Column(String(256))
    encoding = Column(String(16))
    created_at = Column(DateTime(timezone=True), default=utc_now, nullable=False)


class ComplexInputAsValue(Base):
    __tablename__ = "complex_inputs_as_values"
    id = Column(Integer, primary_key=True)
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="complex_inputs_as_values")
    wps_identifier = Column(String(256))
    input_value = Column(Text)
    mime_type = Column(String(64))
    xmlschema = Column(String(256))
    encoding = Column(String(16))
    created_at = Column(DateTime(timezone=True), default=utc_now, nullable=False)


class LiteralInput(Base):
    __tablename__ = "literal_inputs"
    id = Column(Integer, primary_key=True)
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="literal_inputs")
    wps_identifier = Column(String(256))
    input_value = Column(Text)
    created_at = Column(DateTime(timezone=True), default=utc_now, nullable=False)


class BboxInput(Base):
    __tablename__ = "bbox_inputs"
    id = Column(Integer, primary_key=True)
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="bbox_inputs")
    wps_identifier = Column(String(256))
    lower_corner_x = Column(Float)
    lower_corner_y = Column(Float)
    upper_corner_x = Column(Float)
    upper_corner_y = Column(Float)
    crs = Column(String(32))
    created_at = Column(DateTime(timezone=True), default=utc_now, nullable=False)
