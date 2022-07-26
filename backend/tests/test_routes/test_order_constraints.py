from catalog_app.main import ROOT_PATH
from catalog_app.models import Order, OrderConstraint, User

from ..base import cleanup_db, client, session


def test_read_order_constaint_list_empty(client):
    response = client.get(f"{ROOT_PATH}/order-constraints")
    assert response.status_code == 200
    assert response.json() == []


def test_read_order_contraint_list_one(session, client):
    user = User(email="homer.j@fox.at")
    order = Order(user=user)
    order_constraint = OrderConstraint(
        order=order, key="gmpe", constraint_value="abrahamson"
    )
    session.add_all([user, order, order_constraint])
    session.commit()
    response = client.get(f"{ROOT_PATH}/order-constraints")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": order_constraint.id,
            "order_id": order.id,
            "key": "gmpe",
            "constraint_value": "abrahamson",
        }
    ]


def test_read_order_contraint_list_filter_order_id(session, client):
    user = User(email="homer.j@fox.at")
    order1 = Order(user=user)
    order2 = Order(user=user)
    order_constraint1 = OrderConstraint(
        order=order1, key="gmpe", constraint_value="abrahamson"
    )
    order_constraint2 = OrderConstraint(
        order=order2, key="gmpe", constraint_value="abrahamson"
    )
    session.add_all([user, order1, order2, order_constraint1, order_constraint2])
    session.commit()
    response = client.get(f"{ROOT_PATH}/order-constraints?order_id={order1.id}")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": order_constraint1.id,
            "order_id": order1.id,
            "key": "gmpe",
            "constraint_value": "abrahamson",
        }
    ]


def test_read_order_contraint_list_filter_key(session, client):
    user = User(email="homer.j@fox.at")
    order1 = Order(user=user)
    order_constraint1 = OrderConstraint(
        order=order1, key="gmpe", constraint_value="abrahamson"
    )
    order_constraint2 = OrderConstraint(
        order=order1, key="grid", constraint_value="usgs"
    )
    session.add_all([user, order1, order_constraint1, order_constraint2])
    session.commit()
    response = client.get(f"{ROOT_PATH}/order-constraints?key=gmpe")
    assert response.status_code == 200
    assert response.json() == [
        {
            "id": order_constraint1.id,
            "order_id": order1.id,
            "key": "gmpe",
            "constraint_value": "abrahamson",
        }
    ]


def test_read_order_constraint_list_101(session, client):
    user = User(email="homer.j@fox.at")
    session.add(user)
    order = Order(user=user)
    session.add(order)
    for i in range(101):
        order_constraint = OrderConstraint(
            order=order, key="gmpe", constraint_value="abrahamson"
        )
        session.add(order_constraint)
        session.commit()
    # In the first page we have 100 orders.
    response = client.get(f"{ROOT_PATH}/order-constraints")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{ROOT_PATH}/order-constraints?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1


def test_read_order_constraint_detail_one(session, client):
    user = User(email="homer.j@fox.at")
    order = Order(user=user)
    order_constraint = OrderConstraint(
        order=order, key="gmpe", constraint_value="abrahamson"
    )
    session.add_all([user, order, order_constraint])
    session.commit()
    response = client.get(f"{ROOT_PATH}/order-constraints/{order_constraint.id}")
    assert response.status_code == 200
    assert response.json() == {
        "id": order_constraint.id,
        "order_id": order.id,
        "key": "gmpe",
        "constraint_value": "abrahamson",
    }


def test_read_order_constraints_detail_none(client):
    response = client.get(f"{ROOT_PATH}/order-constraints/-123")
    assert response.status_code == 404
