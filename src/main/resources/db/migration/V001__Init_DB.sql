CREATE TABLE User
(
    id         varchar NOT NULL,
    first_name varchar,
    last_name  varchar,
    active     integer NOT NULL DEFAULT 1,
    role_id    bigint,
    primary key (id)
);

CREATE TABLE Workout
(
    id             INTEGER,
    coach_id       varchar,
    day_of_week    varchar,
    time           varchar,
    max_count_user integer DEFAULT 8,
    is_active      integer,
    PRIMARY KEY (id AUTOINCREMENT)
);

CREATE TABLE Role
(
    id   INTEGER,
    name varchar,
    PRIMARY KEY (id AUTOINCREMENT)
);

CREATE TABLE New_workout
(
    id              INTEGER,
    chat_id         varchar,
    first_name      varchar,
    last_name       varchar,
    reserve         integer NOT NULL,
    sentMessages_id bigint,
    workout_id      bigint,
    PRIMARY KEY (id AUTOINCREMENT)
);

CREATE TABLE Sent_messages
(
    id         INTEGER,
    message_id integer NOT NULL,
    user_id    varchar,
    workout_id bigint,
    PRIMARY KEY (id AUTOINCREMENT)
);
CREATE TABLE Statistic
(
    id        INTEGER,
    chat_id   varchar,
    user_name varchar,
    action    varchar,
    workout   varchar,
    date      varchar,
    PRIMARY KEY (id AUTOINCREMENT)
);
insert  INTO Role(name) values('ADMIN');
insert  INTO Role(name) values('COACH');
insert  INTO Role(name) values('USER');
