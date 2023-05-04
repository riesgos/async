from catalog_app.main import config
from catalog_app.models import ComplexInput, Job, Process

from ..base import cleanup_db, client, session


def test_read_complex_input_list_empty(client):
    response = client.get(f"{config.root_path}/complex-inputs")
    assert response.status_code == 200
    assert response.json() == []


def test_read_complex_input_list_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    complex_input = ComplexInput(
        job=job,
        wps_identifier="shakemap",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job, complex_input])
    session.commit()
    response = client.get(f"{config.root_path}/complex-inputs")
    assert response.status_code == 200
    assert len(response.json()) == 1
    expected = {
        "id": complex_input.id,
        "job_id": job.id,
        "wps_identifier": "shakemap",
        "link": "https://download",
        "mime_type": "application/xml",
        "xmlschema": "https://shakemap",
        "encoding": "UTF-8",
    }
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_complex_input_list_filter_wps_identifier(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    complex_input1 = ComplexInput(
        job=job,
        wps_identifier="shakemap1",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_input2 = ComplexInput(
        job=job,
        wps_identifier="shakemap2",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job, complex_input1, complex_input2])
    session.commit()
    response = client.get(f"{config.root_path}/complex-inputs?wps_identifier=shakemap1")
    assert response.status_code == 200
    assert len(response.json()) == 1
    expected = {
        "id": complex_input1.id,
        "job_id": job.id,
        "wps_identifier": "shakemap1",
        "link": "https://download",
        "mime_type": "application/xml",
        "xmlschema": "https://shakemap",
        "encoding": "UTF-8",
    }
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_complex_input_list_filter_job_id(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job1 = Job(process=process, status="pending")
    job2 = Job(process=process, status="pending")
    complex_input1 = ComplexInput(
        job=job2,
        wps_identifier="shakemap1",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_input2 = ComplexInput(
        job=job1,
        wps_identifier="shakemap2",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job1, job2, complex_input1, complex_input2])
    session.commit()
    response = client.get(f"{config.root_path}/complex-inputs?job_id={job2.id}")
    assert response.status_code == 200
    assert len(response.json()) == 1
    expected = {
        "id": complex_input1.id,
        "job_id": job2.id,
        "wps_identifier": "shakemap1",
        "link": "https://download",
        "mime_type": "application/xml",
        "xmlschema": "https://shakemap",
        "encoding": "UTF-8",
    }
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_complex_input_list_filter_process_id(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de",
        wps_identifier="deus",
    )
    job1 = Job(process=process1, status="pending")
    job2 = Job(process=process2, status="pending")
    complex_input1 = ComplexInput(
        job=job2,
        wps_identifier="shakemap1",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_input2 = ComplexInput(
        job=job1,
        wps_identifier="shakemap2",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process1, process2, job1, job2, complex_input1, complex_input2])
    session.commit()
    response = client.get(f"{config.root_path}/complex-inputs?process_id={process2.id}")
    assert response.status_code == 200
    assert len(response.json()) == 1
    expected = {
        "id": complex_input1.id,
        "job_id": job2.id,
        "wps_identifier": "shakemap1",
        "link": "https://download",
        "mime_type": "application/xml",
        "xmlschema": "https://shakemap",
        "encoding": "UTF-8",
    }
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_complex_input_list_101(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(
        process=process,
        status="pending",
    )
    session.add_all([process, job])
    for i in range(101):
        complex_input = ComplexInput(
            job=job,
            wps_identifier="shakemap",
            link="https://download",
            mime_type="application/xml",
            xmlschema="https://shakemap",
            encoding="UTF-8",
        )
        session.add(complex_input)
        session.commit()
    # In the first page we have 100 complex inputs.
    response = client.get(f"{config.root_path}/complex-inputs")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{config.root_path}/complex-inputs?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_complex_input_detail_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    complex_input = ComplexInput(
        job=job,
        wps_identifier="shakemap",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job, complex_input])
    session.commit()
    response = client.get(f"{config.root_path}/complex-inputs/{complex_input.id}")
    assert response.status_code == 200
    expected = {
        "id": complex_input.id,
        "job_id": job.id,
        "wps_identifier": "shakemap",
        "link": "https://download",
        "mime_type": "application/xml",
        "xmlschema": "https://shakemap",
        "encoding": "UTF-8",
    }
    for key, value in expected.items():
        assert response.json()[key] == value
    assert "created_at" in response.json().keys()


def test_read_complex_input_detail_none(client):
    response = client.get(f"{config.root_path}/complex-inputs/-123")
    assert response.status_code == 404
