from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

literal_input_router = APIRouter(prefix="/literal-inputs")


@literal_input_router.get("/", response_model=List[schemas.LiteralInput])
def read_list(
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[int] = None,
    process_id: Optional[int] = None,
    db: Session = Depends(get_db),
):
    """Return the list of literal inputs."""
    return crud.get_literal_inputs(
        db,
        skip=skip,
        limit=limit,
        wps_identifier=wps_identifier,
        job_id=job_id,
        process_id=process_id,
    )


@literal_input_router.get("/{literal_input_id}", response_model=schemas.LiteralInput)
def read_detail(literal_input_id: int, db: Session = Depends(get_db)):
    """Return the literal input with the given literal input id or raise 404."""
    db_literal_input = crud.get_literal_input(db, literal_input_id=literal_input_id)
    if db_literal_input is None:
        raise HTTPException(status_code=404, detail="Literal input not found")
    return db_literal_input
