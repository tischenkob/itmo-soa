CREATE TABLE IF NOT EXISTS soa_people
(
    passport    varchar(32) primary key check (trim(both from passport) <> ''),
    eye_color   varchar(8)  not null,
    hair_color  varchar(8)  not null,
    nationality varchar(16) not null
);

CREATE TABLE IF NOT EXISTS soa_workers
(
    id       serial primary key check (id > 0),
    name     varchar(50) not null check (trim(both from name) <> ''),
    x        real check (x <= 48),
    y        int check (y <= 676),
    created  timestamp   not null default now(),
    salary   bigint check (salary > 0),
    hired    timestamp   not null,
    quit     timestamp            default null,
    status   varchar(32) not null,
    passport varchar(32) not null references soa_people on delete cascade,
    org_id   int references soa_orgs on delete cascade
);

CREATE TABLE IF NOT EXISTS soa_orgs
(
    id   serial primary key check (id > 0),
    name varchar(32) not null check (trim(both from name) <> '')
);

INSERT INTO soa_orgs(name)
VALUES ('Facebook'),
       ('Amazon'),
       ('Netflix'),
       ('Google');