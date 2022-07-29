from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

complex_input_as_value_router = APIRouter(prefix="/complex-inputs-as-values")


@complex_input_as_value_router.get("/", response_model=List[schemas.ComplexInputAsValue])
def read_list(
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[int] = None,
    db: Session = Depends(get_db),
):
    """Return the list of complex inputs."""
    return crud.get_complex_inputs_as_values(
        db, skip=skip, limit=limit, wps_identifier=wps_identifier, job_id=job_id
    )


@complex_input_as_value_router.get("/{complex_input_as_value_id}", response_model=schemas.ComplexInputAsValue)
def read_detail(complex_input_as_value_id: int, db: Session = Depends(get_db)):
    """Return the complex input with the given complex input id or raise 404."""
    db_complex_input_as_value = crud.get_complex_input_as_value(db, complex_input_as_value_id=complex_input_as_value_id)
    if db_complex_input_as_value is None:
        raise HTTPException(status_code=404, detail="Complex input as value not found")
    return db_complex_input_as_value
