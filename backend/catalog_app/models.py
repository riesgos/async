from sqlalchemy import Column, Float, ForeignKey, Integer, String, Text
from sqlalchemy.orm import relationship

from .database import Base


class Process(Base):
    __tablename__ = "processes"
    id = Column(Integer, primary_key=True)
    wps_url = Column(String(256))
    wps_identifier = Column(String(256))
    jobs = relationship("Job", back_populates="process")


class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True)
    email = Column(String(256))
    orders = relationship("Order", back_populates="user")


class Order(Base):
    __tablename__ = "orders"
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    user = relationship("User", back_populates="orders")
    constraints = relationship("OrderConstraint", back_populates="order")
    order_job_refs = relationship("OrderJobRef", back_populates="order")


class OrderConstraint(Base):
    __tablename__ = "order_constraints"
    id = Column(Integer, primary_key=True)
    order_id = Column(Integer, ForeignKey("orders.id"))
    order = relationship("Order", back_populates="constraints")
    key = Column(String(256))
    constraint_value = Column(String(256))


class Job(Base):
    __tablename__ = "jobs"
    id = Column(Integer, primary_key=True)
    process_id = Column(Integer, ForeignKey("processes.id"))
    process = relationship("Process", back_populates="jobs")
    status = Column(String(16))

    order_job_refs = relationship("OrderJobRef", back_populates="job")
    complex_outputs = relationship("ComplexOutput", back_populates="job")
    complex_outputs_as_inputs = relationship(
        "ComplexOutputAsInput", back_populates="job"
    )
    complex_inputs = relationship("ComplexInput", back_populates="job")
    literal_inputs = relationship("LiteralInput", back_populates="job")
    bbox_inputs = relationship("BboxInput", back_populates="job")


class OrderJobRef(Base):
    __tablename__ = "order_job_refs"
    id = Column(Integer, primary_key=True)
    order_id = Column(Integer, ForeignKey("orders.id"))
    order = relationship("Order", back_populates="order_job_refs")
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="order_job_refs")


class ComplexOutput(Base):
    __tablename__ = "complex_outputs"
    id = Column(Integer, primary_key=True)
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="complex_outputs")
    wps_identifier = Column(String(256))
    link = Column(String(1024))
    mime_type = Column(String(64))
    xmlschema = Column(String(256))
    inputs = relationship("ComplexOutputAsInput", back_populates="complex_output")


class ComplexOutputAsInput(Base):
    __tablename__ = "complex_outputs_as_inputs"
    id = Column(Integer, primary_key=True)
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="complex_outputs_as_inputs")
    wps_identifier = Column(String(256))
    complex_output_id = Column(Integer, ForeignKey("complex_outputs.id"))
    complex_output = relationship("ComplexOutput", back_populates="inputs")


class ComplexInput(Base):
    __tablename__ = "complex_inputs"
    id = Column(Integer, primary_key=True)
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="complex_inputs")
    wps_identifier = Column(String(256))
    link = Column(String(1024))
    mime_type = Column(String(64))
    xmlschema = Column(String(256))


class LiteralInput(Base):
    __tablename__ = "literal_inputs"
    id = Column(Integer, primary_key=True)
    job_id = Column(Integer, ForeignKey("jobs.id"))
    job = relationship("Job", back_populates="literal_inputs")
    wps_identifier = Column(String(256))
    input_value = Column(Text)


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
