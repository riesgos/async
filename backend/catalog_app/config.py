from typing import Optional
from pydantic import BaseSettings, Field


class Config(BaseSettings):
    root_path: str = Field(env="ROOT_PATH", default="/")
    sqlalchemy_database_url: str = Field(
        env="SQLALCHEMY_DATABASE_URL", default="sqlite:///./sql_app.db"
    )
    initial_user_email: Optional[str] = Field(env="BACKEND_USER_EMAIL")
    initial_user_password: Optional[str] = Field(env="BACKEND_USER_PASSWORD")
