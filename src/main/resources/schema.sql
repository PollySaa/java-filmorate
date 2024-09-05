DROP TABLE IF EXISTS users, mpa, film, genre, film_genre, film_like, friends;

CREATE TABLE IF NOT EXISTS users
(
    id       integer generated by default as identity not null primary key,
    email    varchar(255) NOT NULL,
    login    varchar(255) NOT NULL,
    name     varchar(255),
    birthday date
);

CREATE TABLE IF NOT EXISTS mpa
(
    id          integer generated by default as identity not null primary key,
    name        varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film
(
    id           integer generated by default as identity not null primary key,
    name         varchar(255) NOT NULL,
    description  varchar(200),
    release_date date         NOT NULL,
    duration     int          NOT NULL,
    rating_id    integer      NOT NULL REFERENCES mpa(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS genre
(
    id   integer generated by default as identity not null primary key,
    name varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  integer REFERENCES film(id) ON DELETE CASCADE,
    genre_id integer REFERENCES genre(id) ON DELETE RESTRICT,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS film_like
(
    film_id  integer REFERENCES film(id) ON DELETE CASCADE,
    user_id integer REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);


CREATE TABLE IF NOT EXISTS friends
(
    user_id integer REFERENCES users(id) ON DELETE CASCADE,
    friend_id integer REFERENCES users(id) ON DELETE CASCADE,
    status boolean
);