from catalog_app.main import ROOT_PATH
from catalog_app.models import Job, Order, OrderJobRef, Process, User

from ..base import cleanup_db, client, session


def test_read_product_type_list_empty(client):
    response = client.get(f"{ROOT_PATH}/product-types")
    assert response.status_code == 200
    assert response.json() == []


def test_read_product_types_list_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="Succeeded")
    session.add_all([process, job])
    session.commit()
    response = client.get(f"{ROOT_PATH}/product-types")
    assert response.status_code == 200
    assert response.json() == [
        {
            "name": "shakyground output",
            "id": process.id,
        }
    ]


def test_read_product_types_list_one_multiple_jobs(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job1 = Job(process=process, status="Succeeded")
    job2 = Job(process=process, status="Succeeded")
    session.add_all([process, job1, job2])
    session.commit()
    response = client.get(f"{ROOT_PATH}/product-types")
    assert response.status_code == 200
    # Still just one
    assert response.json() == [
        {
            "name": "shakyground output",
            "id": process.id,
        }
    ]


def test_read_product_types_list_no_successful_jobs(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job1 = Job(process=process, status="pending")
    job2 = Job(process=process, status="failed")
    session.add_all([process, job1, job2])
    session.commit()
    response = client.get(f"{ROOT_PATH}/product-types")
    assert response.status_code == 200
    # Still just one
    assert response.json() == []


def test_read_product_types_list_101(session, client):
    for i in range(101):
        process = Process(
            wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier=f"shakyground {i}"
        )
        session.add(process)
        job = Job(
            process=process,
            status="Succeeded",
        )
        session.add(job)
        session.commit()
    # In the first page we have 100 jobs.
    response = client.get(f"{ROOT_PATH}/product-types")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{ROOT_PATH}/product-types?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_product_type_detail_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="Succeeded")
    session.add_all([process, job])
    session.commit()
    response = client.get(f"{ROOT_PATH}/product-types/{process.id}")
    assert response.status_code == 200
    assert response.json() == {
        "id": process.id,
        "name": "shakyground output",
    }


def test_read_product_type_detail_no_succesful_job(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="failed")
    session.add_all([process, job])
    session.commit()
    response = client.get(f"{ROOT_PATH}/product-types/{process.id}")
    assert response.status_code == 404


def test_read_product_type_detail_none(client):
    response = client.get(f"{ROOT_PATH}/product-types/-123")
    assert response.status_code == 404
