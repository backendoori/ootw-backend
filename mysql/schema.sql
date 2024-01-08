CREATE SCHEMA IF NOT EXISTS ootw;

USE ootw;

CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    nickname   VARCHAR(255) NOT NULL,
    image      VARCHAR(500) NULL,
    created_at DATETIME(6)  NULL,
    updated_at DATETIME(6)  NULL,
    CONSTRAINT users_pk
        PRIMARY KEY (id),
    CONSTRAINT users_email_index
        UNIQUE (email)
);

CREATE TABLE avatar_items
(
    id    BIGINT AUTO_INCREMENT,
    image VARCHAR(500) NOT NULL,
    type  VARCHAR(30)  NOT NULL,
    sex   TINYINT      NOT NULL,
    CONSTRAINT avatar_items_pk
        PRIMARY KEY (id)
);

CREATE TABLE posts
(
    id         BIGINT AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(30)  NOT NULL,
    content    VARCHAR(255) NOT NULL,
    image      VARCHAR(500) NULL,
    created_at DATETIME(6)  NULL,
    updated_at DATETIME(6)  NULL,
    current_temperature DOUBLE                                                  NOT NULL,
    day_min_temperature DOUBLE                                                  NOT NULL,
    day_max_temperature DOUBLE                                                  NOT NULL,
    pty_type            ENUM ('NONE', 'RAIN', 'RAIN_OR_SNOW', 'SNOW', 'SHOWER') NOT NULL,
    sky_type            ENUM ('SUNNY', 'CLOUDY', 'OVERCAST')                    NOT NULL,
    CONSTRAINT posts_pk
        PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES users (id)
);

CREATE TABLE likes
(
    id         BIGINT AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    post_id    BIGINT       NOT NULL,
    status     TINYINT      NOT NULL,
    created_at DATETIME(6)  NULL,
    updated_at DATETIME(6)  NULL,
    CONSTRAINT posts_pk
        PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES users (id),
    FOREIGN KEY (post_id)
        REFERENCES posts (id),
);
