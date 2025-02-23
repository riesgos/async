from catalog_app.main import config
from catalog_app.models import Order, User

from ..base import cleanup_db, client, session


def test_read_order_list_empty(client):
    response = client.get(f"{config.root_path}/orders")
    assert response.status_code == 200
    assert response.json() == []


def test_read_order_list_one(session, client):
    user = User(email="homer.j@fox.at")
    order = Order(user=user, order_constraints={})
    session.add_all([user, order])
    session.commit()
    response = client.get(f"{config.root_path}/orders")
    assert response.status_code == 200
    expected = {
        "id": order.id,
        "user_id": user.id,
        "order_constraints": {},
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_order_list_filter_user_id(session, client):
    user1 = User(email="homer.j@fox.at")
    user2 = User(email="m.simp@fox.at")
    order1 = Order(
        user=user1,
        order_constraints={
            "gmpe": ["Abrahamson"],
        },
    )
    order2 = Order(user=user2, order_constraints={})
    session.add_all([user1, user2, order1, order2])
    session.commit()
    response = client.get(f"{config.root_path}/orders?user_id={user1.id}")
    assert response.status_code == 200
    expected = {
        "id": order1.id,
        "user_id": user1.id,
        "order_constraints": {"gmpe": ["Abrahamson"]},
    }

    assert len(response.json()) == 1
    for key, value in expected.items():
        assert response.json()[0][key] == value
    assert "created_at" in response.json()[0].keys()


def test_read_order_list_101(session, client):
    user = User(email="homer.j@fox.at")
    session.add(user)
    for i in range(101):
        order = Order(user=user, order_constraints={})
        session.add(order)
        session.commit()
    # In the first page we have 100 orders.
    response = client.get(f"{config.root_path}/orders")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{config.root_path}/orders?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_order_detail_one(session, client):
    user = User(email="homer.j@fox.at")
    order = Order(
        user=user,
        order_constraints={
            "gmpe": ["Abrahamson"],
        },
    )
    session.add_all([user, order])
    session.commit()
    response = client.get(f"{config.root_path}/orders/{order.id}")
    assert response.status_code == 200
    expected = {
        "id": order.id,
        "user_id": user.id,
        "order_constraints": {"gmpe": ["Abrahamson"]},
    }
    for key, value in expected.items():
        assert response.json()[key] == value
    assert "created_at" in response.json().keys()


def test_read_order_detail_none(client):
    response = client.get(f"{config.root_path}/orders/-123")
    assert response.status_code == 404


def test_create_order_no_auth(client):
    response = client.post(
        f"{config.root_path}/orders/",
        json={"order_constraints": {"gmpe": ["Abrahamson"]}},
    )
    assert response.status_code == 401


def test_create_order_wrong_auth(client):
    response = client.post(
        f"{config.root_path}/orders/",
        json={
            "order_constraints": {"gmpe": ["Abrahamson"]},
        },
        headers={"X-APIKEY": "123"},
    )
    assert response.status_code == 401


def test_create_order_auth(client, session):
    user = User(email="abc@def", apikey="123")
    session.add(user)
    session.commit()
    response = client.post(
        f"{config.root_path}/orders/",
        json={
            "order_constraints": {"gmpe": ["Abrahamson"]},
        },
        headers={"X-APIKEY": "123"},
    )
    assert response.status_code == 200
    response_data = response.json()
    assert "id" in response_data.keys()
    assert "user_id" in response_data.keys()
    assert "order_constraints" in response_data.keys()
