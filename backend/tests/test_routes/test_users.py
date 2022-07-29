from catalog_app.main import ROOT_PATH
from catalog_app.models import User

from ..base import cleanup_db, client, session


def test_read_user_list_empty(client):
    response = client.get(f"{ROOT_PATH}/users")
    assert response.status_code == 200
    assert response.json() == []


def test_read_user_list_one(session, client):
    user = User(email="homer.j@fox.at")
    session.add(user)
    session.commit()
    response = client.get(f"{ROOT_PATH}/users")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": user.id,
            "email": "homer.j@fox.at",
        }
    ]


def test_read_user_list_101(session, client):
    for i in range(101):
        user = User(email="homer.j@fox.at")
        session.add(user)
        session.commit()
    # In the first page we have 100 users.
    response = client.get(f"{ROOT_PATH}/users")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{ROOT_PATH}/users?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_user_detail_one(session, client):
    user = User(email="homer.j@fox.at")
    session.add(user)
    session.commit()
    response = client.get(f"{ROOT_PATH}/users/{user.id}")
    assert response.status_code == 200
    assert response.json() == {
        "id": user.id,
        "email": "homer.j@fox.at",
    }


def test_read_user_detail_none(client):
    response = client.get(f"{ROOT_PATH}/users/-123")
    assert response.status_code == 404
