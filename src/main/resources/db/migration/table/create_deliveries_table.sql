--liquibase formatted sql
--changeset AVoronov:v1.0.0/deliveries

create table if not exists deliveries
(
    id uuid not null primary key,
    is_deleted boolean not null default false,
    deleted_at timestamp without time zone,
    order_id uuid not null,
    restaurant_id uuid not null,
    address text not null,
    comment text,
    status varchar(64) not null
);