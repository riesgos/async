create table processes (
  id serial,
  wps_url varchar(256),
  wps_identifier varchar(256),
  primary key (id)
);

create table users (
  id serial,
  email varchar(256),
  primary key (id)
);

create table orders (
  id serial,
  user_id bigint,
  order_constraints jsonb,
  primary key (id)
);

create table jobs (
  id serial,
  process_id bigint,
  status varchar(16),
  primary key (id),
  foreign key (process_id) references processes(id)
);

create table order_job_refs (
  id serial,
  job_id bigint,
  order_id bigint,
  primary key (id),
  foreign key (order_id) references orders(id),
  foreign key (job_id) references jobs(id)
);

create table complex_outputs (
  id serial,
  job_id bigint,
  wps_identifier varchar(256),
  link varchar(1024),
  mime_type varchar(64),
  xmlschema varchar(256),
  encoding varchar(16),
  primary key (id),
  foreign key (job_id) references jobs(id)
);

create table complex_outputs_as_inputs(
  id serial,
  wps_identifier varchar(256),
  job_id bigint,
  -- To refer to an already existing complex output
  -- that we are going to reuse.
  complex_output_id bigint,
  primary key (id),
  foreign key (job_id) references jobs(id),
  foreign key (complex_output_id) references complex_outputs(id)
);

create table complex_inputs (
  id serial,
  job_id bigint,
  wps_identifier varchar(256),
  link varchar(1024),
  mime_type varchar(64),
  xmlschema varchar(256),
  encoding varchar(16),
  primary key (id),
  foreign key (job_id) references jobs(id)
);

create table literal_inputs (
  id serial,
  job_id bigint,
  wps_identifier varchar(256),
  input_value text,
  primary key (id),
  foreign key (job_id) references jobs(id)
);

create table bbox_inputs (
  id serial,
  job_id bigint,
  wps_identifier varchar(256),
  lower_corner_x real,
  lower_corner_y real,
  upper_corner_x real,
  upper_corner_y real,
  crs varchar(32),
  primary key (id),
  foreign key (job_id) references jobs(id)
);
