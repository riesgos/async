from catalog_app.main import config
from catalog_app.models import Job, LiteralInput, Process

from ..base import cleanup_db, client, session


def test_read_literal_input_list_empty(client):
    response = client.get(f"{config.root_path}/literal-inputs")
    assert response.status_code == 200
    assert response.json() == []


def test_read_literal_input_list_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    literal_input = LiteralInput(
        job=job, wps_identifier="gmpe", input_value="Abrahamson"
    )
    session.add_all([process, job, literal_input])
    session.commit()
    response = client.get(f"{config.root_path}/literal-inputs")
    assert response.status_code == 200
    expected = {
        "id": literal_input.id,
        "job_id": job.id,
        "wps_identifier": "gmpe",
        "input_value": "Abrahamson",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_literal_input_list_filter_wps_identifier(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    literal_input1 = LiteralInput(
        job=job, wps_identifier="gmpe", input_value="Abrahamson"
    )
    literal_input2 = LiteralInput(job=job, wps_identifier="vsgrid", input_value="usgs")
    session.add_all([process, job, literal_input1, literal_input2])
    session.commit()
    response = client.get(f"{config.root_path}/literal-inputs?wps_identifier=gmpe")
    assert response.status_code == 200
    expected = {
        "id": literal_input1.id,
        "job_id": job.id,
        "wps_identifier": "gmpe",
        "input_value": "Abrahamson",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_literal_input_list_filter_job_id(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job1 = Job(process=process, status="pending")
    job2 = Job(process=process, status="pending")
    literal_input1 = LiteralInput(
        job=job1, wps_identifier="gmpe", input_value="Abrahamson"
    )
    literal_input2 = LiteralInput(job=job2, wps_identifier="vsgrid", input_value="usgs")
    session.add_all([process, job1, job2, literal_input1, literal_input2])
    session.commit()
    response = client.get(f"{config.root_path}/literal-inputs?job_id={job1.id}")
    assert response.status_code == 200
    expected = {
        "id": literal_input1.id,
        "job_id": job1.id,
        "wps_identifier": "gmpe",
        "input_value": "Abrahamson",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_literal_input_list_filter_process_id(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="pending")
    job2 = Job(process=process2, status="pending")
    literal_input1 = LiteralInput(
        job=job1, wps_identifier="gmpe", input_value="Abrahamson"
    )
    literal_input2 = LiteralInput(job=job2, wps_identifier="vsgrid", input_value="usgs")
    session.add_all([process1, process2, job1, job2, literal_input1, literal_input2])
    session.commit()
    response = client.get(f"{config.root_path}/literal-inputs?process_id={process1.id}")
    assert response.status_code == 200
    expected = {
        "id": literal_input1.id,
        "job_id": job1.id,
        "wps_identifier": "gmpe",
        "input_value": "Abrahamson",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_literal_input_list_101(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(
        process=process,
        status="pending",
    )
    session.add_all([process, job])
    for i in range(101):
        literal_input = LiteralInput(
            job=job,
            wps_identifier="gmpe",
            input_value="Abrahamson",
        )
        session.add(literal_input)
        session.commit()
    # In the first page we have 100 literal inputs.
    response = client.get(f"{config.root_path}/literal-inputs")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{config.root_path}/literal-inputs?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_literal_input_detail_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    literal_input = LiteralInput(
        job=job,
        wps_identifier="gmpe",
        input_value="Abrahamson",
    )
    session.add_all([process, job, literal_input])
    session.commit()
    response = client.get(f"{config.root_path}/literal-inputs/{literal_input.id}")
    assert response.status_code == 200
    expected = {
        "id": literal_input.id,
        "job_id": job.id,
        "wps_identifier": "gmpe",
        "input_value": "Abrahamson",
    }
    for key, value in expected.items():
        assert response.json()[key] == value
    assert "created_at" in response.json().keys()


def test_read_literal_input_detail_none(client):
    response = client.get(f"{config.root_path}/literal-inputs/-123")
    assert response.status_code == 404
