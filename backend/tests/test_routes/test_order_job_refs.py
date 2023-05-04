from catalog_app.main import config
from catalog_app.models import Job, Order, OrderJobRef, Process, User

from ..base import cleanup_db, client, session


def test_read_order_job_ref_list_empty(client):
    response = client.get(f"{config.root_path}/order-job-refs")
    assert response.status_code == 200
    assert response.json() == []


def test_read_order_job_ref_list_one(session, client):
    user = User(email="homer.j@fox.at")
    order = Order(user=user)
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    order_job_ref = OrderJobRef(order=order, job=job)
    session.add_all([user, order, process, job, order_job_ref])
    session.commit()
    response = client.get(f"{config.root_path}/order-job-refs")
    assert response.status_code == 200
    expected = {
        "id": order_job_ref.id,
        "order_id": order.id,
        "job_id": job.id,
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_order_job_ref_list_filter_job_id(session, client):
    user = User(email="homer.j@fox.at")
    order = Order(user=user)
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job1 = Job(process=process, status="pending")
    job2 = Job(process=process, status="pending")
    order_job_ref1 = OrderJobRef(order=order, job=job1)
    order_job_ref2 = OrderJobRef(order=order, job=job2)
    session.add_all([user, order, process, job1, job2, order_job_ref1, order_job_ref2])
    session.commit()
    response = client.get(f"{config.root_path}/order-job-refs?job_id={job1.id}")
    assert response.status_code == 200
    expected = {
        "id": order_job_ref1.id,
        "order_id": order.id,
        "job_id": job1.id,
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_order_job_ref_list_filter_order_id(session, client):
    user = User(email="homer.j@fox.at")
    order1 = Order(user=user)
    order2 = Order(user=user)
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job1 = Job(process=process, status="pending")
    order_job_ref1 = OrderJobRef(order=order1, job=job1)
    order_job_ref2 = OrderJobRef(order=order2, job=job1)
    session.add_all(
        [user, order1, order2, process, job1, order_job_ref1, order_job_ref2]
    )
    session.commit()
    response = client.get(f"{config.root_path}/order-job-refs?order_id={order1.id}")
    assert response.status_code == 200
    expected = {
        "id": order_job_ref1.id,
        "order_id": order1.id,
        "job_id": job1.id,
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_order_job_ref_list_101(session, client):
    user = User(email="homer.j@fox.at")
    order = Order(user=user)
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    session.add_all([user, order, process])
    for i in range(101):
        job = Job(process=process, status="pending")
        order_job_ref = OrderJobRef(order=order, job=job)
        session.add_all([job, order_job_ref])
        session.commit()
    # In the first page we have 100 orders.
    response = client.get(f"{config.root_path}/order-job-refs")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{config.root_path}/order-job-refs?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_order_job_ref_detail_one(session, client):
    user = User(email="homer.j@fox.at")
    order = Order(user=user)
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    order_job_ref = OrderJobRef(order=order, job=job)
    session.add_all([user, order, process, job, order_job_ref])
    session.commit()
    response = client.get(f"{config.root_path}/order-job-refs/{order_job_ref.id}")
    assert response.status_code == 200
    expected = {
        "id": order_job_ref.id,
        "order_id": order.id,
        "job_id": job.id,
    }
    for key, value in expected.items():
        assert response.json()[key] == value
    assert "created_at" in response.json().keys()


def test_read_order_job_ref_detail_none(client):
    response = client.get(f"{config.root_path}/order-job-refs/-123")
    assert response.status_code == 404
