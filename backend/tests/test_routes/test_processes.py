from catalog_app.main import config
from catalog_app.models import Process

from ..base import cleanup_db, client, session


def test_read_process_list_empty(client):
    response = client.get(f"{config.root_path}/processes")
    assert response.status_code == 200
    assert response.json() == []


def test_read_process_list_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    session.add(process)
    session.commit()
    response = client.get(f"{config.root_path}/processes")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": process.id,
            "wps_url": "https://rz-vm140.gfz-potsdam.de",
            "wps_identifier": "shakyground",
        }
    ]


def test_read_process_list_filter_wps_identifier(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="deus")
    session.add_all([process1, process2])
    session.commit()
    response = client.get(f"{config.root_path}/processes?wps_identifier=shakyground")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": process1.id,
            "wps_url": "https://rz-vm140.gfz-potsdam.de",
            "wps_identifier": "shakyground",
        }
    ]


def test_read_process_list_filter_wps_url(session, client):
    process1 = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    process2 = Process(wps_url="https://rz-vm141.gfz-potsdam.de", wps_identifier="deus")
    session.add_all([process1, process2])
    session.commit()
    response = client.get(
        f"{config.root_path}/processes",
        params={
            "wps_url": "https://rz-vm140.gfz-potsdam.de",
        },
    )
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": process1.id,
            "wps_url": "https://rz-vm140.gfz-potsdam.de",
            "wps_identifier": "shakyground",
        }
    ]


def test_read_process_list_101(session, client):
    for i in range(101):
        process = Process(
            wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
        )
        session.add(process)
        session.commit()
    # In the first page we have 100 processes.
    response = client.get(f"{config.root_path}/processes")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{config.root_path}/processes?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_process_detail_one(session, client):
    process = Process(
        wps_url="https://rz-vm140.gfz-potsdam.de", wps_identifier="shakyground"
    )
    session.add(process)
    session.commit()
    response = client.get(f"{config.root_path}/processes/{process.id}")
    assert response.status_code == 200
    assert response.json() == {
        "id": process.id,
        "wps_url": "https://rz-vm140.gfz-potsdam.de",
        "wps_identifier": "shakyground",
    }


def test_read_process_detail_none(client):
    response = client.get(f"{config.root_path}/processes/-123")
    assert response.status_code == 404
