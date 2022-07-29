from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

process_router = APIRouter(prefix="/processes")


@process_router.get("/", response_model=List[schemas.Process])
def read_list(
    skip: int = 0,
    limit: int = 100,
    wps_identifier: Optional[str] = None,
    wps_url: Optional[str] = None,
    db: Session = Depends(get_db),
):
    """Return the list of processes."""
    return crud.get_processes(
        db, skip=skip, limit=limit, wps_identifier=wps_identifier, wps_url=wps_url
    )


@process_router.get("/{process_id}", response_model=schemas.Process)
def read_detail(process_id: int, db: Session = Depends(get_db)):
    """Return the process with the given process id or raise 404."""
    db_process = crud.get_process(db, process_id=process_id)
    if db_process is None:
        raise HTTPException(status_code=404, detail="Process not found")
    return db_process
