from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

complex_output_router = APIRouter(prefix="/complex-outputs")


@complex_output_router.get("/", response_model=List[schemas.ComplexOutput])
def read_list(
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[int] = None,
    process_id: Optional[int] = None,
    db: Session = Depends(get_db),
):
    """Return the list of complex outputs."""
    return crud.get_complex_outputs(
        db,
        skip=skip,
        limit=limit,
        wps_identifier=wps_identifier,
        job_id=job_id,
        process_id=process_id,
    )


@complex_output_router.get("/{complex_output_id}", response_model=schemas.ComplexOutput)
def read_detail(complex_output_id: int, db: Session = Depends(get_db)):
    """Return the complex output with the given complex output id or raise 404."""
    db_complex_output = crud.get_complex_output(db, complex_output_id=complex_output_id)
    if db_complex_output is None:
        raise HTTPException(status_code=404, detail="Complex output not found")
    return db_complex_output
