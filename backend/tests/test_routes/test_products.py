from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import pytest


from catalog_app.main import app, ROOT_PATH
from catalog_app.database import Base
from catalog_app.dependencies import get_db
from catalog_app.models import Product

SQLALCHEMY_DATABASE_URL = "sqlite:///./test.db"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False}
)
TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


Base.metadata.create_all(bind=engine)
def overwrite_get_db():
    try:
        db = TestingSessionLocal()
        yield db
    finally:
        db.close()

app.dependency_overrides[get_db] = overwrite_get_db

client = TestClient(app)

@pytest.fixture(autouse=True)
def cleanup_db():
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)
    yield
    Base.metadata.drop_all(bind=engine)

def test_read_product_list_empty():
    response = client.get(f"{ROOT_PATH}/products")
    assert response.status_code == 200
    assert response.json() == []

def test_read_product_list_one():
    for session in overwrite_get_db():
        product = Product(service="shakyground")
        session.add(product)
        session.commit()
        response = client.get(f"{ROOT_PATH}/products")
        assert response.status_code == 200
        assert response.json() == [{
            "id": product.id,
            "service": "shakyground"
        }]

def test_read_product_detail_one():
    for session in overwrite_get_db():
        product = Product(service="shakyground")
        session.add(product)
        session.commit()
        response = client.get(f"{ROOT_PATH}/products/{product.id}")
        assert response.status_code == 200
        assert response.json() == {
            "id": product.id,
            "service": "shakyground"
        }

def test_read_product_detail_none():
    response = client.get(f"{ROOT_PATH}/products/-123")
    assert response.status_code == 404
