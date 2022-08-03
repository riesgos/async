from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

complex_output_as_input_router = APIRouter(prefix="/complex-outputs-as-inputs")


@complex_output_as_input_router.get(
    "/", response_model=List[schemas.ComplexOutputAsInput]
)
def read_list(
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[int] = None,
    process_id: Optional[int] = None,
    db: Session = Depends(get_db),
):
    """Return the list of complex outputs as inputs."""
    return crud.get_complex_outputs_as_inputs(
        db,
        skip=skip,
        limit=limit,
        wps_identifier=wps_identifier,
        job_id=job_id,
        process_id=process_id,
    )


@complex_output_as_input_router.get(
    "/{complex_output_as_input_id}", response_model=schemas.ComplexOutputAsInput
)
def read_detail(complex_output_as_input_id: int, db: Session = Depends(get_db)):
    """Return the complex output with the given complex output as input id or raise 404."""
    db_complex_output_as_input = crud.get_complex_output_as_input(
        db, complex_output_as_input_id=complex_output_as_input_id
    )
    if db_complex_output_as_input is None:
        raise HTTPException(status_code=404, detail="Complex output as input not found")
    return db_complex_output_as_input
