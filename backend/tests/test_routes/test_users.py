from catalog_app.main import ROOT_PATH
from catalog_app.models import User

from ..base import cleanup_db, client, session


def test_read_user_list_empty(client):
    # Here it also makes no difference if we have auth or not.
    response = client.get(f"{ROOT_PATH}/users")
    assert response.status_code == 200
    assert response.json() == []


def test_read_user_list_two_self_auth(session, client):
    user1 = User(email="homer.j@fox.at", apikey="123")
    session.add(user1)
    user2 = User(email="bar.d@fox.at", apikey="124")
    session.add(user2)
    session.commit()
    response = client.get(f"{ROOT_PATH}/users", headers={"X-APIKEY": "123"})
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": user1.id,
            "email": "homer.j@fox.at",
        }
    ]


def test_read_user_list_two_self_auth_superuser(session, client):
    user1 = User(email="homer.j@fox.at", apikey="123", superuser=True)
    session.add(user1)
    user2 = User(email="bar.d@fox.at", apikey="124")
    session.add(user2)
    session.commit()
    response = client.get(f"{ROOT_PATH}/users", headers={"X-APIKEY": "123"})
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": user1.id,
            "email": "homer.j@fox.at",
        },
        {
            "id": user2.id,
            "email": "bar.d@fox.at",
        },
    ]


def test_read_user_list_one_no_auth(session, client):
    user = User(email="homer.j@fox.at", apikey="123")
    session.add(user)
    session.commit()
    response = client.get(f"{ROOT_PATH}/users")
    assert response.status_code == 200
    assert response.json() == []


def test_read_user_list_101(session, client):
    superuser = User(email="root@localhost", apikey="123", superuser=True)
    session.add(superuser)
    session.commit()
    for i in range(100):
        user = User(email="homer.j@fox.at")
        session.add(user)
        session.commit()
    # In the first page we have 100 users.
    response = client.get(f"{ROOT_PATH}/users", headers={"X-APIKEY": "123"})
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{ROOT_PATH}/users?skip=100", headers={"X-APIKEY": "123"})
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_user_detail_one_self_auth(session, client):
    user = User(email="homer.j@fox.at", apikey=123)
    session.add(user)
    session.commit()
    response = client.get(f"{ROOT_PATH}/users/{user.id}", headers={"X-APIKEY": "123"})
    assert response.status_code == 200
    assert response.json() == {
        "id": user.id,
        "email": "homer.j@fox.at",
    }


def test_read_user_detail_one_different_auth(session, client):
    user1 = User(email="homer.j@fox.at", apikey=123)
    user2 = User(email="bar.d@fox.at")
    session.add_all([user1, user2])
    session.commit()
    response = client.get(f"{ROOT_PATH}/users/{user2.id}", headers={"X-APIKEY": "123"})
    assert response.status_code == 403


def test_read_user_detail_one_different_auth_superuser(session, client):
    user1 = User(email="homer.j@fox.at", apikey=123, superuser=True)
    user2 = User(email="bar.d@fox.at")
    session.add_all([user1, user2])
    session.commit()
    response = client.get(f"{ROOT_PATH}/users/{user2.id}", headers={"X-APIKEY": "123"})
    assert response.status_code == 200
    assert response.json() == {"id": user2.id, "email": "bar.d@fox.at"}


def test_read_user_detail_one_no_auth(session, client):
    user = User(email="homer.j@fox.at")
    session.add(user)
    session.commit()
    response = client.get(f"{ROOT_PATH}/users/{user.id}")
    assert response.status_code == 401


def test_read_user_detail_none_no_auth(client):
    response = client.get(f"{ROOT_PATH}/users/-123")
    assert response.status_code == 401


def test_read_user_detail_none_auth(session, client):
    user = User(email="homer.j@fox.at", apikey="123")
    session.add(user)
    session.commit()
    response = client.get(f"{ROOT_PATH}/users/-123", headers={"X-APIKEY": "123"})
    assert response.status_code == 403


def test_read_user_detail_none_auth_superuser(session, client):
    user = User(email="homer.j@fox.at", apikey="123", superuser=True)
    session.add(user)
    session.commit()
    response = client.get(f"{ROOT_PATH}/users/-123", headers={"X-APIKEY": "123"})
    assert response.status_code == 404
