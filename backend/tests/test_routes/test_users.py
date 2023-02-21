import pytest

from catalog_app.main import config
from catalog_app.models import User
from catalog_app.routers.user_router import allow_external_user_registration
from catalog_app import crud

from ..base import cleanup_db, client, session


def test_read_user_list_empty(client):
    # Here it also makes no difference if we have auth or not.
    response = client.get(f"{config.root_path}/users")
    assert response.status_code == 200
    assert response.json() == []


def test_read_user_list_two_self_auth(session, client):
    user1 = User(email="homer.j@fox.at", apikey="123")
    session.add(user1)
    user2 = User(email="bar.d@fox.at", apikey="124")
    session.add(user2)
    session.commit()
    response = client.get(f"{config.root_path}/users", headers={"X-APIKEY": "123"})
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
    response = client.get(f"{config.root_path}/users", headers={"X-APIKEY": "123"})
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
    response = client.get(f"{config.root_path}/users")
    assert response.status_code == 200
    assert response.json() == []


def test_read_user_list_one_wrong_auth(session, client):
    user = User(email="homer.j@fox.at", apikey="123")
    session.add(user)
    session.commit()
    response = client.get(f"{config.root_path}/users", headers={"X-APIKEY": "456"})
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
    response = client.get(f"{config.root_path}/users", headers={"X-APIKEY": "123"})
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(
        f"{config.root_path}/users?skip=100", headers={"X-APIKEY": "123"}
    )
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_user_detail_one_self_auth(session, client):
    user = User(email="homer.j@fox.at", apikey=123)
    session.add(user)
    session.commit()
    response = client.get(
        f"{config.root_path}/users/{user.id}", headers={"X-APIKEY": "123"}
    )
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
    response = client.get(
        f"{config.root_path}/users/{user2.id}", headers={"X-APIKEY": "123"}
    )
    assert response.status_code == 403


def test_read_user_detail_one_different_auth_superuser(session, client):
    user1 = User(email="homer.j@fox.at", apikey=123, superuser=True)
    user2 = User(email="bar.d@fox.at")
    session.add_all([user1, user2])
    session.commit()
    response = client.get(
        f"{config.root_path}/users/{user2.id}", headers={"X-APIKEY": "123"}
    )
    assert response.status_code == 200
    assert response.json() == {"id": user2.id, "email": "bar.d@fox.at"}


def test_read_user_detail_one_no_auth(session, client):
    user = User(email="homer.j@fox.at")
    session.add(user)
    session.commit()
    response = client.get(f"{config.root_path}/users/{user.id}")
    assert response.status_code == 401


def test_read_user_detail_none_no_auth(client):
    response = client.get(f"{config.root_path}/users/-123")
    assert response.status_code == 401


def test_read_user_detail_none_wrong_auth(client):
    response = client.get(f"{config.root_path}/users/-123", headers={"X-APIKEY": "123"})
    assert response.status_code == 401


def test_read_user_detail_none_auth(session, client):
    user = User(email="homer.j@fox.at", apikey="123")
    session.add(user)
    session.commit()
    response = client.get(f"{config.root_path}/users/-123", headers={"X-APIKEY": "123"})
    assert response.status_code == 403


def test_read_user_detail_none_auth_superuser(session, client):
    user = User(email="homer.j@fox.at", apikey="123", superuser=True)
    session.add(user)
    session.commit()
    response = client.get(f"{config.root_path}/users/-123", headers={"X-APIKEY": "123"})
    assert response.status_code == 404


@pytest.mark.skipif(
    not allow_external_user_registration, reason="No registration possible via api"
)
def test_register_user(client):
    response = client.post(
        f"{config.root_path}/users/register",
        json={"email": "test@user.net", "password": "test123"},
    )
    assert response.status_code == 200
    response_data = response.json()
    assert "email" in response_data.keys()
    assert response_data["email"] == "test@user.net"
    assert "id" in response_data.keys()
    assert "apikey" in response_data.keys()


@pytest.mark.skipif(
    not allow_external_user_registration, reason="No registration possible via api"
)
def test_registration_existing_user(session, client):
    user = User(email="test@user.net")
    session.add(user)
    session.commit()
    response = client.post(
        f"{config.root_path}/users/register",
        json={"email": "test@user.net", "password": "test123"},
    )
    assert response.status_code == 409


@pytest.mark.skipif(
    not allow_external_user_registration, reason="No registration possible via api"
)
def test_register_user_no_password(client):
    response = client.post(
        f"{config.root_path}/users/register",
        json={"email": "test@user.net", "password": ""},
    )
    assert response.status_code == 400


def test_login_user(client, session):
    user = User(
        email="test@user.net",
        password_hash=User.generate_new_password_hash("test123"),
        apikey=User.generate_new_apikey(),
    )
    crud.create_user(session, user)

    response = client.post(
        f"{config.root_path}/users/login",
        json={"email": "test@user.net", "password": "test123"},
    )
    assert response.status_code == 200
    login_data = response.json()
    assert user.apikey == login_data["apikey"]


def test_login_user_multiple(client, session):
    user1 = User(
        email="test@user.net",
        password_hash=User.generate_new_password_hash("test123"),
        apikey=User.generate_new_apikey(),
    )
    crud.create_user(session, user1)
    user2 = User(
        email="test2@user.net",
        password_hash=User.generate_new_password_hash("test1234"),
        apikey=User.generate_new_apikey(),
    )
    crud.create_user(session, user2)
    response = client.post(
        f"{config.root_path}/users/login",
        json={"email": "test@user.net", "password": "test123"},
    )
    assert response.status_code == 200
    response = client.post(
        f"{config.root_path}/users/login",
        json={"email": "test2@user.net", "password": "test1234"},
    )
    assert response.status_code == 200


def test_login_wrong_email(client, session):
    user = User(
        email="test@user.net",
        password_hash=User.generate_new_password_hash("test123"),
        apikey=User.generate_new_apikey(),
    )
    crud.create_user(session, user)
    response = client.post(
        f"{config.root_path}/users/login",
        json={"email": "test2@user.net", "password": "test123"},
    )
    assert response.status_code == 400


def test_login_wrong_password(client, session):
    user = User(
        email="test@user.net",
        password_hash=User.generate_new_password_hash("test123"),
        apikey=User.generate_new_apikey(),
    )
    crud.create_user(session, user)
    response = client.post(
        f"{config.root_path}/users/login",
        json={"email": "test@user.net", "password": "test1234"},
    )
    assert response.status_code == 400
