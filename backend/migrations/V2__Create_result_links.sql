create table result_links (
    id serial,
    product_id bigint,
    identifier varchar(256),
    link varchar(1024),
    mimetype varchar(64),
    xmlschema varchar(256),
    primary key (id),
    foreign key (product_id) references products(id)
);


