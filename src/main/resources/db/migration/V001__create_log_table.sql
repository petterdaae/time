create table log
(
    id          serial primary key,
    description text,
    timestamp   timestamp with time zone
);