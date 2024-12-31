create schema if not exists app;

create table if not exists app.country
(
    id   bigserial   not null unique,
    name varchar(50) not null unique check ( length(trim(name)) > 0 ),
    code varchar(3)  not null unique check ( length(trim(code)) > 0 ),
    primary key (id)
);