from catalog_app.main import ROOT_PATH
from catalog_app.models import ComplexInputAsValue, Job, Process

from ..base import cleanup_db, client, session


def test_read_complex_input_list_empty(client):
    response = client.get(f"{ROOT_PATH}/complex-inputs-as-values")
    assert response.status_code == 200
    assert response.json() == []


def test_read_complex_input_list_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    complex_input = ComplexInputAsValue(
        job=job,
        wps_identifier="shakemap",
        input_value="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job, complex_input])
    session.commit()
    response = client.get(f"{ROOT_PATH}/complex-inputs-as-values")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": complex_input.id,
            "job_id": job.id,
            "wps_identifier": "shakemap",
            "input_value": "https://download",
            "mime_type": "application/xml",
            "xmlschema": "https://shakemap",
            "encoding": "UTF-8",
        }
    ]


def test_read_complex_input_list_filter_wps_identifier(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    complex_input1 = ComplexInputAsValue(
        job=job,
        wps_identifier="shakemap1",
        input_value="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_input2 = ComplexInputAsValue(
        job=job,
        wps_identifier="shakemap2",
        input_value="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job, complex_input1, complex_input2])
    session.commit()
    response = client.get(f"{ROOT_PATH}/complex-inputs-as-values?wps_identifier=shakemap1")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": complex_input1.id,
            "job_id": job.id,
            "wps_identifier": "shakemap1",
            "input_value": "https://download",
            "mime_type": "application/xml",
            "xmlschema": "https://shakemap",
            "encoding": "UTF-8",
        }
    ]


def test_read_complex_input_list_filter_job_id(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job1 = Job(process=process, status="pending")
    job2 = Job(process=process, status="pending")
    complex_input1 = ComplexInputAsValue(
        job=job2,
        wps_identifier="shakemap1",
        input_value="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_input2 = ComplexInputAsValue(
        job=job1,
        wps_identifier="shakemap2",
        input_value="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job1, job2, complex_input1, complex_input2])
    session.commit()
    response = client.get(f"{ROOT_PATH}/complex-inputs-as-values?job_id={job2.id}")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": complex_input1.id,
            "job_id": job2.id,
            "wps_identifier": "shakemap1",
            "input_value": "https://download",
            "mime_type": "application/xml",
            "xmlschema": "https://shakemap",
            "encoding": "UTF-8",
        }
    ]


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
        complex_input = ComplexInputAsValue(
            job=job,
            wps_identifier="shakemap",
            input_value="https://download",
            mime_type="application/xml",
            xmlschema="https://shakemap",
            encoding="UTF-8",
        )
        session.add(complex_input)
        session.commit()
    # In the first page we have 100 complex inputs.
    response = client.get(f"{ROOT_PATH}/complex-inputs-as-values")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{ROOT_PATH}/complex-inputs-as-values?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_complex_input_detail_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="pending")
    complex_input = ComplexInputAsValue(
        job=job,
        wps_identifier="shakemap",
        input_value="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job, complex_input])
    session.commit()
    response = client.get(f"{ROOT_PATH}/complex-inputs-as-values/{complex_input.id}")
    assert response.status_code == 200
    assert response.json() == {
        "id": complex_input.id,
        "job_id": job.id,
        "wps_identifier": "shakemap",
        "input_value": "https://download",
        "mime_type": "application/xml",
        "xmlschema": "https://shakemap",
        "encoding": "UTF-8",
    }


def test_read_complex_input_detail_none(client):
    response = client.get(f"{ROOT_PATH}/complex-inputs-as-values/-123")
    assert response.status_code == 404
