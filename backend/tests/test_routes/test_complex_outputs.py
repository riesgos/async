from catalog_app.main import config
from catalog_app.models import ComplexOutput, Job, Process

from ..base import cleanup_db, client, session


def test_read_complex_output_list_empty(client):
    response = client.get(f"{config.root_path}/complex-outputs")
    assert response.status_code == 200
    assert response.json() == []


def test_read_complex_output_list_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    complex_output = ComplexOutput(
        job=job,
        wps_identifier="shakemap",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job, complex_output])
    session.commit()
    response = client.get(f"{config.root_path}/complex-outputs")
    assert response.status_code == 200
    expected = {
        "id": complex_output.id,
        "job_id": job.id,
        "wps_identifier": "shakemap",
        "link": "https://download",
        "mime_type": "application/xml",
        "xmlschema": "https://shakemap",
        "encoding": "UTF-8",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_complex_output_list_filter_wps_identifier(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    complex_output1 = ComplexOutput(
        job=job,
        wps_identifier="shakemap1",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_output2 = ComplexOutput(
        job=job,
        wps_identifier="shakemap2",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job, complex_output1, complex_output2])
    session.commit()
    response = client.get(
        f"{config.root_path}/complex-outputs?wps_identifier=shakemap1"
    )
    assert response.status_code == 200
    expected = {
        "id": complex_output1.id,
        "job_id": job.id,
        "wps_identifier": "shakemap1",
        "link": "https://download",
        "mime_type": "application/xml",
        "xmlschema": "https://shakemap",
        "encoding": "UTF-8",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_complex_output_list_filter_job_id(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job1 = Job(process=process, status="pending")
    job2 = Job(process=process, status="pending")
    complex_output1 = ComplexOutput(
        job=job1,
        wps_identifier="shakemap1",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_output2 = ComplexOutput(
        job=job2,
        wps_identifier="shakemap2",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job1, job2, complex_output1, complex_output2])
    session.commit()
    response = client.get(f"{config.root_path}/complex-outputs?job_id={job1.id}")
    assert response.status_code == 200
    expected = {
        "id": complex_output1.id,
        "job_id": job1.id,
        "wps_identifier": "shakemap1",
        "link": "https://download",
        "mime_type": "application/xml",
        "xmlschema": "https://shakemap",
        "encoding": "UTF-8",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_complex_output_list_filter_process_id(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="pending")
    job2 = Job(process=process2, status="pending")
    complex_output1 = ComplexOutput(
        job=job1,
        wps_identifier="shakemap1",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_output2 = ComplexOutput(
        job=job2,
        wps_identifier="shakemap2",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process1, process2, job1, job2, complex_output1, complex_output2])
    session.commit()
    response = client.get(
        f"{config.root_path}/complex-outputs?process_id={process1.id}"
    )
    assert response.status_code == 200
    expected = {
        "id": complex_output1.id,
        "job_id": job1.id,
        "wps_identifier": "shakemap1",
        "link": "https://download",
        "mime_type": "application/xml",
        "xmlschema": "https://shakemap",
        "encoding": "UTF-8",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_complex_output_list_filter_mime_type(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="pending")
    job2 = Job(process=process2, status="pending")
    complex_output1 = ComplexOutput(
        job=job1,
        wps_identifier="shakemap1",
        link="https://download",
        mime_type="abc",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_output2 = ComplexOutput(
        job=job2,
        wps_identifier="shakemap2",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process1, process2, job1, job2, complex_output1, complex_output2])
    session.commit()
    response = client.get(f"{config.root_path}/complex-outputs?mime_type=abc")
    assert response.status_code == 200
    expected = {
        "id": complex_output1.id,
        "job_id": job1.id,
        "wps_identifier": "shakemap1",
        "link": "https://download",
        "mime_type": "abc",
        "xmlschema": "https://shakemap",
        "encoding": "UTF-8",
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_complex_output_list_101(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(
        process=process,
        status="pending",
    )
    session.add_all([process, job])
    for i in range(101):
        complex_output = ComplexOutput(
            job=job,
            wps_identifier="shakemap",
            link="https://download",
            mime_type="application/xml",
            xmlschema="https://shakemap",
            encoding="UTF-8",
        )
        session.add(complex_output)
        session.commit()
    # In the first page we have 100 complex outputs.
    response = client.get(f"{config.root_path}/complex-outputs")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{config.root_path}/complex-outputs?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_complex_output_detail_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    complex_output = ComplexOutput(
        job=job,
        wps_identifier="shakemap",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job, complex_output])
    session.commit()
    response = client.get(f"{config.root_path}/complex-outputs/{complex_output.id}")
    assert response.status_code == 200
    expected = {
        "id": complex_output.id,
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


def test_read_complex_output_detail_none(client):
    response = client.get(f"{config.root_path}/complex-outputs/-123")
    assert response.status_code == 404
