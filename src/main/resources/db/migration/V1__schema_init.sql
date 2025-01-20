create schema if not exists app;

create table if not exists app.country
(
    id           bigserial    not null,
    name         varchar(50)  not null unique check ( length(trim(name)) > 0 ),
    code         varchar(3)   not null unique check ( length(trim(code)) > 0 ),
    date_created timestamp(3) not null,
    date_updated timestamp(3) not null,
    primary key (id)
);