from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from .. import crud, routers, schemas
from ..dependencies import get_db

job_router = APIRouter(prefix="/jobs")


@job_router.get("/", response_model=List[schemas.Job])
def read_list(
    skip: int = 0,
    limit: int = 100,
    process_id: Optional[int] = None,
    status: Optional[str] = None,
    order_id: Optional[int] = None,
    db: Session = Depends(get_db),
):
    """Return the list of jobs."""
    return crud.get_jobs(
        db,
        skip=skip,
        limit=limit,
        process_id=process_id,
        status=status,
        order_id=order_id,
    )


@job_router.get("/{job_id}", response_model=schemas.Job)
def read_detail(job_id: int, db: Session = Depends(get_db)):
    """Return the job with the given job id or raise 404."""
    db_job = crud.get_job(db, job_id=job_id)
    if db_job is None:
        raise HTTPException(status_code=404, detail="Job not found")
    return db_job
