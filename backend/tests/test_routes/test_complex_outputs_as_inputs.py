from catalog_app.main import ROOT_PATH
from catalog_app.models import ComplexOutput, ComplexOutputAsInput, Job, Process

from ..base import cleanup_db, client, session


def test_read_complex_output_as_input_list_empty(client):
    response = client.get(f"{ROOT_PATH}/complex-outputs-as-inputs")
    assert response.status_code == 200
    assert response.json() == []


def test_read_complex_output_as_input_list_one(session, client):
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
    complex_output_as_input = ComplexOutputAsInput(
        complex_output=complex_output, job=job, wps_identifier="intensity"
    )
    session.add_all([process, job, complex_output, complex_output_as_input])
    session.commit()
    response = client.get(f"{ROOT_PATH}/complex-outputs-as-inputs")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": complex_output_as_input.id,
            "job_id": job.id,
            "wps_identifier": "intensity",
            "complex_output_id": complex_output.id,
        }
    ]


def test_read_complex_output_as_input_list_filter_wps_identifier(session, client):
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
    complex_output_as_input1 = ComplexOutputAsInput(
        complex_output=complex_output, job=job, wps_identifier="intensity1"
    )
    complex_output_as_input2 = ComplexOutputAsInput(
        complex_output=complex_output, job=job, wps_identifier="intensity2"
    )
    session.add_all(
        [
            process,
            job,
            complex_output,
            complex_output_as_input1,
            complex_output_as_input2,
        ]
    )
    session.commit()
    response = client.get(
        f"{ROOT_PATH}/complex-outputs-as-inputs?wps_identifier=intensity1"
    )
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": complex_output_as_input1.id,
            "job_id": job.id,
            "wps_identifier": "intensity1",
            "complex_output_id": complex_output.id,
        }
    ]


def test_read_complex_output_as_input_list_filter_job_id(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job1 = Job(process=process, status="pending")
    job2 = Job(process=process, status="pending")
    complex_output = ComplexOutput(
        job=job1,
        wps_identifier="shakemap",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_output_as_input1 = ComplexOutputAsInput(
        complex_output=complex_output, job=job1, wps_identifier="intensity1"
    )
    complex_output_as_input2 = ComplexOutputAsInput(
        complex_output=complex_output, job=job2, wps_identifier="intensity2"
    )
    session.add_all(
        [
            process,
            job1,
            job2,
            complex_output,
            complex_output_as_input1,
            complex_output_as_input2,
        ]
    )
    session.commit()
    response = client.get(f"{ROOT_PATH}/complex-outputs-as-inputs?job_id={job1.id}")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": complex_output_as_input1.id,
            "job_id": job1.id,
            "wps_identifier": "intensity1",
            "complex_output_id": complex_output.id,
        }
    ]


def test_read_complex_output_as_input_list_101(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(
        process=process,
        status="pending",
    )
    complex_output = ComplexOutput(
        job=job,
        wps_identifier="shakemap",
        link="https://download",
        mime_type="application/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process, job, complex_output])
    for i in range(101):
        complex_output_as_input = ComplexOutputAsInput(
            complex_output=complex_output, job=job, wps_identifier="intensity"
        )
        session.add(complex_output_as_input)
        session.commit()
    # In the first page we have 100 complex outputs.
    response = client.get(f"{ROOT_PATH}/complex-outputs-as-inputs")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{ROOT_PATH}/complex-outputs-as-inputs?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_complex_output_as_input_detail_one(session, client):
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
    complex_output_as_input = ComplexOutputAsInput(
        complex_output=complex_output, job=job, wps_identifier="intensity"
    )
    session.add_all([process, job, complex_output, complex_output_as_input])
    session.commit()
    response = client.get(
        f"{ROOT_PATH}/complex-outputs-as-inputs/{complex_output_as_input.id}"
    )
    assert response.status_code == 200
    assert response.json() == {
        "id": complex_output_as_input.id,
        "job_id": job.id,
        "wps_identifier": "intensity",
        "complex_output_id": complex_output.id,
    }


def test_read_complex_output_as_input_detail_none(client):
    response = client.get(f"{ROOT_PATH}/complex-outputs-as-inputs/-123")
    assert response.status_code == 404
