from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

complex_input_router = APIRouter(prefix="/complex-inputs")


@complex_input_router.get("/", response_model=List[schemas.ComplexInput])
def read_list(
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[int] = None,
    db: Session = Depends(get_db),
):
    """Return the list of complex inputs."""
    return crud.get_complex_inputs(
        db, skip=skip, limit=limit, wps_identifier=wps_identifier, job_id=job_id
    )


@complex_input_router.get("/{complex_input_id}", response_model=schemas.ComplexInput)
def read_detail(complex_input_id: int, db: Session = Depends(get_db)):
    """Return the complex input with the given complex input id or raise 404."""
    db_complex_input = crud.get_complex_input(db, complex_input_id=complex_input_id)
    if db_complex_input is None:
        raise HTTPException(status_code=404, detail="Complex input not found")
    return db_complex_input
