from catalog_app.main import ROOT_PATH
from catalog_app.models import Order, User

from ..base import cleanup_db, client, session


def test_read_order_list_empty(client):
    response = client.get(f"{ROOT_PATH}/orders")
    assert response.status_code == 200
    assert response.json() == []


def test_read_order_list_one(session, client):
    user = User(email="homer.j@fox.at")
    order = Order(user=user, order_constraints={})
    session.add_all([user, order])
    session.commit()
    response = client.get(f"{ROOT_PATH}/orders")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": order.id,
            "user_id": user.id,
            "order_constraints": {},
        }
    ]


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
    response = client.get(f"{ROOT_PATH}/orders?user_id={user1.id}")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": order1.id,
            "user_id": user1.id,
            "order_constraints": {"gmpe": ["Abrahamson"]},
        }
    ]


def test_read_order_list_101(session, client):
    user = User(email="homer.j@fox.at")
    session.add(user)
    for i in range(101):
        order = Order(user=user, order_constraints={})
        session.add(order)
        session.commit()
    # In the first page we have 100 orders.
    response = client.get(f"{ROOT_PATH}/orders")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{ROOT_PATH}/orders?skip=100")
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
    response = client.get(f"{ROOT_PATH}/orders/{order.id}")
    assert response.status_code == 200
    assert response.json() == {
        "id": order.id,
        "user_id": user.id,
        "order_constraints": {"gmpe": ["Abrahamson"]},
    }


def test_read_order_detail_none(client):
    response = client.get(f"{ROOT_PATH}/orders/-123")
    assert response.status_code == 404
