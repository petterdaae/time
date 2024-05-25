create table session
(
    id    serial primary key,
    start timestamp with time zone not null,
    _end   timestamp with time zone
);