from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

bbox_input_router = APIRouter(prefix="/bbox-inputs")


@bbox_input_router.get("/", response_model=List[schemas.BboxInput])
def read_list(
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    job_id: Optional[str] = None,
    process_id: Optional[str] = None,
    db: Session = Depends(get_db),
):
    """Return the list of bbox inputs."""
    return crud.get_bbox_inputs(
        db,
        skip=skip,
        limit=limit,
        wps_identifier=wps_identifier,
        job_id=job_id,
        process_id=process_id,
    )


@bbox_input_router.get("/{bbox_input_id}", response_model=schemas.BboxInput)
def read_detail(bbox_input_id: int, db: Session = Depends(get_db)):
    """Return the bbox input with the given bbox input id or raise 404."""
    db_bbox_input = crud.get_bbox_input(db, bbox_input_id=bbox_input_id)
    if db_bbox_input is None:
        raise HTTPException(status_code=404, detail="Bbox input not found")
    return db_bbox_input
