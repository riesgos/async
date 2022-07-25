from catalog_app.main import ROOT_PATH
from catalog_app.models import Product

from ..base import session, client, cleanup_db

def test_read_product_list_empty(client):
    response = client.get(f"{ROOT_PATH}/products")
    assert response.status_code == 200
    assert response.json() == []

def test_read_product_list_one(session, client):
    product = Product(service="shakyground")
    session.add(product)
    session.commit()
    response = client.get(f"{ROOT_PATH}/products")
    assert response.status_code == 200
    assert response.json() == [{
        "id": product.id,
        "service": "shakyground"
    }]

def test_read_product_list_101(session, client):
    for i in range(101):
        product = Product(service="shakyground")
        session.add(product)
        session.commit()
    # In the first page we have 100 products.
    response = client.get(f"{ROOT_PATH}/products")
    assert response.status_code == 200
    assert len(response.json()) == 100
    # On the second one we have only one left.
    response2 = client.get(f"{ROOT_PATH}/products?skip=100")
    assert response2.status_code == 200
    assert len(response2.json()) == 1

def test_read_product_detail_one(session, client):
    product = Product(service="shakyground")
    session.add(product)
    session.commit()
    response = client.get(f"{ROOT_PATH}/products/{product.id}")
    assert response.status_code == 200
    assert response.json() == {
        "id": product.id,
        "service": "shakyground"
    }

def test_read_product_detail_none(client):
    response = client.get(f"{ROOT_PATH}/products/-123")
    assert response.status_code == 404
