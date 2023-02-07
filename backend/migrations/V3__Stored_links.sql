create table stored_links (
  id serial,
  original_link varchar(1024),
  checksum varchar(64),
  stored_link varchar(1024),
  primary key (id)
);
