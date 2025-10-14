--liquibase formatted sql
--changeset AVoronov:v1.0.0/couriers

create table if not exists couriers
(
    id uuid not null primary key,
    is_deleted boolean not null default false,
    deleted_at timestamp without time zone,
    name varchar(255) not null,
    gender varchar(32),
    phone_number varchar(32) unique not null,
    status varchar(64) not null
);