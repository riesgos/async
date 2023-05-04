from catalog_app.main import config
from catalog_app.models import BboxInput, Job, Process

from ..base import cleanup_db, client, session


def test_read_bbox_input_list_empty(client):
    response = client.get(f"{config.root_path}/bbox-inputs")
    assert response.status_code == 200
    assert response.json() == []


def test_read_bbox_input_list_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    bbox_input = BboxInput(
        job=job,
        wps_identifier="bbox",
        lower_corner_x=1.1,
        lower_corner_y=52,
        upper_corner_x=2.2,
        upper_corner_y=53,
        crs="epsg:4326",
    )
    session.add_all([process, job, bbox_input])
    session.commit()
    response = client.get(f"{config.root_path}/bbox-inputs")
    assert response.status_code == 200
    expected = {
        "id": bbox_input.id,
        "job_id": job.id,
        "wps_identifier": "bbox",
        "lower_corner_x": 1.1,
        "lower_corner_y": 52,
        "upper_corner_x": 2.2,
        "upper_corner_y": 53,
        "crs": "epsg:4326",
    }
    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_bbox_input_list_filter_wps_identifier(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    bbox_input1 = BboxInput(
        job=job,
        wps_identifier="bbox1",
        lower_corner_x=1.1,
        lower_corner_y=52,
        upper_corner_x=2.2,
        upper_corner_y=53,
        crs="epsg:4326",
    )
    bbox_input2 = BboxInput(
        job=job,
        wps_identifier="bbox2",
        lower_corner_x=1.1,
        lower_corner_y=52,
        upper_corner_x=2.2,
        upper_corner_y=53,
        crs="epsg:4326",
    )
    session.add_all([process, job, bbox_input1, bbox_input2])
    session.commit()
    response = client.get(f"{config.root_path}/bbox-inputs?wps_identifier=bbox2")
    assert response.status_code == 200
    assert len(response.json()) == 1
    expected = {
        "id": bbox_input2.id,
        "job_id": job.id,
        "wps_identifier": "bbox2",
        "lower_corner_x": 1.1,
        "lower_corner_y": 52,
        "upper_corner_x": 2.2,
        "upper_corner_y": 53,
        "crs": "epsg:4326",
    }
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_bbox_input_list_filter_process_id(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="pending")
    job2 = Job(process=process2, status="pending")
    bbox_input1 = BboxInput(
        job=job1,
        wps_identifier="bbox1",
        lower_corner_x=1.1,
        lower_corner_y=52,
        upper_corner_x=2.2,
        upper_corner_y=53,
        crs="epsg:4326",
    )
    bbox_input2 = BboxInput(
        job=job2,
        wps_identifier="bbox2",
        lower_corner_x=1.1,
        lower_corner_y=52,
        upper_corner_x=2.2,
        upper_corner_y=53,
        crs="epsg:4326",
    )
    session.add_all([process1, process2, job1, job2, bbox_input1, bbox_input2])
    session.commit()
    response = client.get(f"{config.root_path}/bbox-inputs?process_id={process2.id}")
    assert response.status_code == 200
    assert len(response.json()) == 1
    expected = {
        "id": bbox_input2.id,
        "job_id": job2.id,
        "wps_identifier": "bbox2",
        "lower_corner_x": 1.1,
        "lower_corner_y": 52,
        "upper_corner_x": 2.2,
        "upper_corner_y": 53,
        "crs": "epsg:4326",
    }
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_bbox_input_list_filter_job_id(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job1 = Job(process=process, status="pending")
    job2 = Job(process=process, status="pending")
    bbox_input1 = BboxInput(
        job=job1,
        wps_identifier="bbox1",
        lower_corner_x=1.1,
        lower_corner_y=52,
        upper_corner_x=2.2,
        upper_corner_y=53,
        crs="epsg:4326",
    )
    bbox_input2 = BboxInput(
        job=job2,
        wps_identifier="bbox2",
        lower_corner_x=1.1,
        lower_corner_y=52,
        upper_corner_x=2.2,
        upper_corner_y=53,
        crs="epsg:4326",
    )
    session.add_all([process, job1, job2, bbox_input1, bbox_input2])
    session.commit()
    response = client.get(f"{config.root_path}/bbox-inputs?job_id={job2.id}")
    assert response.status_code == 200
    assert len(response.json()) == 1
    expected = {
        "id": bbox_input2.id,
        "job_id": job2.id,
        "wps_identifier": "bbox2",
        "lower_corner_x": 1.1,
        "lower_corner_y": 52,
        "upper_corner_x": 2.2,
        "upper_corner_y": 53,
        "crs": "epsg:4326",
    }
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_bbox_input_list_101(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(
        process=process,
        status="pending",
    )
    session.add_all([process, job])
    for i in range(101):
        bbox_input = BboxInput(
            job=job,
            wps_identifier="bbox",
            lower_corner_x=1.1,
            lower_corner_y=52,
            upper_corner_x=2.2,
            upper_corner_y=53,
            crs="epsg:4326",
        )
        session.add(bbox_input)
        session.commit()
    # In the first page we have 100 bbox inputs.
    response = client.get(f"{config.root_path}/bbox-inputs")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{config.root_path}/bbox-inputs?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_bbox_input_detail_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    bbox_input = BboxInput(
        job=job,
        wps_identifier="bbox",
        lower_corner_x=1.1,
        lower_corner_y=52,
        upper_corner_x=2.2,
        upper_corner_y=53,
        crs="epsg:4326",
    )
    session.add_all([process, job, bbox_input])
    session.commit()
    response = client.get(f"{config.root_path}/bbox-inputs/{bbox_input.id}")
    assert response.status_code == 200
    expected = {
        "id": bbox_input.id,
        "job_id": job.id,
        "wps_identifier": "bbox",
        "lower_corner_x": 1.1,
        "lower_corner_y": 52,
        "upper_corner_x": 2.2,
        "upper_corner_y": 53,
        "crs": "epsg:4326",
    }
    for key, value in expected.items():
        assert response.json()[key] == value
    assert "created_at" in response.json().keys()


def test_read_bbox_input_detail_none(client):
    response = client.get(f"{config.root_path}/bbox-inputs/-123")
    assert response.status_code == 404
