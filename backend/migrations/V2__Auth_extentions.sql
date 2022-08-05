alter table users add column password_hash varchar(256);
alter table users add column apikey varchar(256);
alter table users add column superuser boolean default false;
