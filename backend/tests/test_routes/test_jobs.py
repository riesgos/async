from catalog_app.main import config
from catalog_app.models import Job, Order, OrderJobRef, Process, User

from ..base import cleanup_db, client, session


def test_read_job_list_empty(client):
    response = client.get(f"{config.root_path}/jobs")
    assert response.status_code == 200
    assert response.json() == []


def test_read_job_list_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    session.add_all([process, job])
    session.commit()
    response = client.get(f"{config.root_path}/jobs")
    assert response.status_code == 200
    expected = {
        "id": job.id,
        "process_id": process.id,
        "status": "pending",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_job_list_filter_process_id(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="pending")
    job2 = Job(process=process2, status="pending")
    session.add_all([process1, process2, job1, job2])
    session.commit()
    response = client.get(f"{config.root_path}/jobs?process_id={process1.id}")
    assert response.status_code == 200
    expected = {
        "id": job1.id,
        "process_id": process1.id,
        "status": "pending",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_job_list_filter_status(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job1 = Job(process=process1, status="running")
    job2 = Job(process=process1, status="pending")
    session.add_all([process1, job1, job2])
    session.commit()
    response = client.get(f"{config.root_path}/jobs?status=running")
    assert response.status_code == 200
    expected = {
        "id": job1.id,
        "process_id": process1.id,
        "status": "running",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_job_list_filter_order_id(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    user = User(email="homer.j@fox.at")
    order1 = Order(user=user)
    order2 = Order(user=user)
    job1 = Job(process=process1, status="running")
    job2 = Job(process=process1, status="pending")
    order_job_ref1 = OrderJobRef(order=order1, job=job1)
    order_job_ref2 = OrderJobRef(order=order2, job=job2)
    session.add_all(
        [user, order1, order2, process1, job1, job2, order_job_ref1, order_job_ref2]
    )
    session.commit()
    response = client.get(f"{config.root_path}/jobs?order_id={order1.id}")
    assert response.status_code == 200
    expected = {
        "id": job1.id,
        "process_id": process1.id,
        "status": "running",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_job_list_101(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    session.add(process)
    for i in range(101):
        job = Job(
            process=process,
            status="pending",
        )
        session.add(job)
        session.commit()
    # In the first page we have 100 jobs.
    response = client.get(f"{config.root_path}/jobs")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{config.root_path}/jobs?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_job_detail_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    session.add_all([process, job])
    session.commit()
    response = client.get(f"{config.root_path}/jobs/{job.id}")
    assert response.status_code == 200
    expected = {
        "id": job.id,
        "process_id": process.id,
        "status": "pending",
    }
    for key, value in expected.items():
        assert response.json()[key] == value
    assert "created_at" in response.json().keys()


def test_read_job_detail_none(client):
    response = client.get(f"{config.root_path}/jobs/-123")
    assert response.status_code == 404
