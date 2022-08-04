from catalog_app.main import ROOT_PATH
from catalog_app.models import (
    ComplexOutput,
    ComplexOutputAsInput,
    Job,
    Order,
    OrderJobRef,
    Process,
    User,
)

from ..base import cleanup_db, client, session


def test_read_product_list_empty(client):
    response = client.get(f"{ROOT_PATH}/products")
    assert response.status_code == 200
    assert response.json() == []


def test_read_product_list_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="Succeeded")
    session.add_all([process, job])
    session.commit()
    response = client.get(f"{ROOT_PATH}/products")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": job.id,
            "name": f"shakyground output ({job.id})",
            "product_type_id": process.id,
        }
    ]


def test_read_product_list_not_successful(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="Failed")
    session.add_all([process, job])
    session.commit()
    response = client.get(f"{ROOT_PATH}/products")
    assert response.status_code == 200
    assert response.json() == []


def test_read_product_list_filter_product_type_id(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="Succeeded")
    job2 = Job(process=process2, status="Succeeded")
    session.add_all([process1, process2, job1, job2])
    session.commit()
    response = client.get(f"{ROOT_PATH}/products?product_type_id={process1.id}")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": job1.id,
            "product_type_id": process1.id,
            "name": f"shakyground output ({job1.id})",
        }
    ]


def test_read_product_list_filter_order_id(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    user = User(email="homer.j@fox.at")
    order1 = Order(user=user)
    order2 = Order(user=user)
    job1 = Job(process=process1, status="Succeeded")
    job2 = Job(process=process1, status="Succeeded")
    order_job_ref1 = OrderJobRef(order=order1, job=job1)
    order_job_ref2 = OrderJobRef(order=order2, job=job2)
    session.add_all(
        [user, order1, order2, process1, job1, job2, order_job_ref1, order_job_ref2]
    )
    session.commit()
    response = client.get(f"{ROOT_PATH}/products?order_id={order1.id}")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": job1.id,
            "product_type_id": process1.id,
            "name": f"shakyground output ({job1.id})",
        }
    ]


def test_read_product_list_101(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    session.add(process)
    for i in range(101):
        job = Job(
            process=process,
            status="Succeeded",
        )
        session.add(job)
        session.commit()
    # In the first page we have 100 jobs.
    response = client.get(f"{ROOT_PATH}/products")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{ROOT_PATH}/products?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_product_detail_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="Succeeded")
    session.add_all([process, job])
    session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job.id}")
    assert response.status_code == 200
    assert response.json() == {
        "id": job.id,
        "product_type_id": process.id,
        "name": f"shakyground output ({job.id})",
    }


def test_read_product_detail_not_successful(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="failed")
    session.add_all([process, job])
    session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job.id}")
    assert response.status_code == 404


def test_read_product_detail_none(client):
    response = client.get(f"{ROOT_PATH}/products/-123")
    assert response.status_code == 404


def test_read_derived_product_list_empty(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="Succeeded")
    session.add_all([process, job])
    session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job.id}/derived-products")
    assert response.status_code == 200
    assert response.json() == []


def test_read_derived_product_list_one(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="Succeeded")
    job2 = Job(process=process2, status="Succeeded")
    complex_output = ComplexOutput(
        job=job1,
        wps_identifier="shakemap",
        link="https://",
        mime_type="text/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_output_as_input = ComplexOutputAsInput(
        job=job2, wps_identifier="intensity", complex_output=complex_output
    )
    session.add_all(
        [process1, process2, job1, job2, complex_output, complex_output_as_input]
    )
    session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job1.id}/derived-products")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": job2.id,
            "product_type_id": process2.id,
            "name": f"deus output ({job2.id})",
        }
    ]


def test_read_derived_product_list_one_no_matter_how_many_inputs(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="Succeeded")
    job2 = Job(process=process2, status="Succeeded")
    session.add_all([process1, process2, job1, job2])
    for i in range(20):
        complex_output = ComplexOutput(
            job=job1,
            wps_identifier=f"shakemap{i}",
            link="https://",
            mime_type="text/xml",
            xmlschema="https://shakemap",
            encoding="UTF-8",
        )
        complex_output_as_input = ComplexOutputAsInput(
            job=job2, wps_identifier=f"intensity{i}", complex_output=complex_output
        )
        session.add_all([complex_output, complex_output_as_input])
        session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job1.id}/derived-products")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": job2.id,
            "product_type_id": process2.id,
            "name": f"deus output ({job2.id})",
        }
    ]


def test_read_derived_product_list_none_as_failed(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="Succeeded")
    job2 = Job(process=process2, status="Failed")
    complex_output = ComplexOutput(
        job=job1,
        wps_identifier="shakemap",
        link="https://",
        mime_type="text/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_output_as_input = ComplexOutputAsInput(
        job=job2, wps_identifier="intensity", complex_output=complex_output
    )
    session.add_all(
        [process1, process2, job1, job2, complex_output, complex_output_as_input]
    )
    session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job1.id}/derived-products")
    assert response.status_code == 200
    assert response.json() == []


def test_read_derived_product_list_101(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="Succeeded")
    complex_output = ComplexOutput(
        job=job1,
        wps_identifier="shakemap",
        link="https://",
        mime_type="text/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    session.add_all([process1, process2, job1, complex_output])
    session.commit()
    for i in range(101):
        jobN = Job(process=process2, status="Succeeded")

        complex_output_as_input = ComplexOutputAsInput(
            job=jobN, wps_identifier="intensity", complex_output=complex_output
        )
        session.add_all([job1, jobN, complex_output_as_input])
        session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job1.id}/derived-products")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{ROOT_PATH}/products/{job1.id}/derived-products?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_base_product_list_empty(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    job = Job(process=process, status="Succeeded")
    session.add_all([process, job])
    session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job.id}/base-products")
    assert response.status_code == 200
    assert response.json() == []


def test_read_base_product_list_one(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="Succeeded")
    job2 = Job(process=process2, status="Succeeded")
    complex_output = ComplexOutput(
        job=job1,
        wps_identifier="shakemap",
        link="https://",
        mime_type="text/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_output_as_input = ComplexOutputAsInput(
        job=job2, wps_identifier="intensity", complex_output=complex_output
    )
    session.add_all(
        [process1, process2, job1, job2, complex_output, complex_output_as_input]
    )
    session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job2.id}/base-products")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": job1.id,
            "product_type_id": process1.id,
            "name": f"shakyground output ({job1.id})",
        }
    ]


def test_read_base_product_list_one_no_matter_how_many_inputs(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="Succeeded")
    job2 = Job(process=process2, status="Succeeded")
    session.add_all([process1, process2, job1, job2])
    for i in range(20):
        complex_output = ComplexOutput(
            job=job1,
            wps_identifier=f"shakemap{i}",
            link="https://",
            mime_type="text/xml",
            xmlschema="https://shakemap",
            encoding="UTF-8",
        )
        complex_output_as_input = ComplexOutputAsInput(
            job=job2, wps_identifier=f"intensity{i}", complex_output=complex_output
        )
        session.add_all([complex_output, complex_output_as_input])
        session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job2.id}/base-products")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": job1.id,
            "product_type_id": process1.id,
            "name": f"shakyground output ({job1.id})",
        }
    ]


def test_read_base_product_list_none_as_not_successful(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process1, status="Failed")
    job2 = Job(process=process2, status="Succeeded")
    complex_output = ComplexOutput(
        job=job1,
        wps_identifier="shakemap",
        link="https://",
        mime_type="text/xml",
        xmlschema="https://shakemap",
        encoding="UTF-8",
    )
    complex_output_as_input = ComplexOutputAsInput(
        job=job2, wps_identifier="intensity", complex_output=complex_output
    )
    session.add_all(
        [process1, process2, job1, job2, complex_output, complex_output_as_input]
    )
    session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job2.id}/base-products")
    assert response.status_code == 200
    assert response.json() == []


def test_read_base_product_list_101(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    job1 = Job(process=process2, status="Succeeded")
    session.add_all([process1, process2, job1])
    session.commit()
    for i in range(101):
        jobN = Job(process=process1, status="Succeeded")
        complex_output = ComplexOutput(
            job=jobN,
            wps_identifier="shakemap",
            link="https://",
            mime_type="text/xml",
            xmlschema="https://shakemap",
            encoding="UTF-8",
        )

        complex_output_as_input = ComplexOutputAsInput(
            job=job1, wps_identifier="intensity", complex_output=complex_output
        )
        session.add_all([jobN, complex_output, complex_output_as_input])
        session.commit()
    response = client.get(f"{ROOT_PATH}/products/{job1.id}/base-products")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{ROOT_PATH}/products/{job1.id}/base-products?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1
