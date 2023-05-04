from .config import Config
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base

config = Config()
engine = create_engine(config.sqlalchemy_database_url)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()
