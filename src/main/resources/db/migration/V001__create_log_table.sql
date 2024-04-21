create type kind as enum ('START', 'STOP');

create table log
(
    id        serial primary key,
    kind      kind,
    timestamp timestamp with time zone
);