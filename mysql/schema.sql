CREATE SCHEMA IF NOT EXISTS ootw;

USE ootw;

CREATE TABLE users
(
    id                BIGINT AUTO_INCREMENT,
    email             VARCHAR(255) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    nickname          VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(500) NULL,
    certified         TINYINT(1)   NOT NULL,
    created_at        DATETIME(6)  NULL,
    updated_at        DATETIME(6)  NULL,

    CONSTRAINT users_pk PRIMARY KEY (id),
    CONSTRAINT users_email_index UNIQUE (email)
);

CREATE TABLE avatar_items
(
    id        BIGINT AUTO_INCREMENT,
    image_url VARCHAR(500) NOT NULL,
    type      VARCHAR(30)  NOT NULL,
    sex       VARCHAR(10)  NOT NULL,

    CONSTRAINT avatar_items_pk PRIMARY KEY (id)
);

CREATE TABLE posts
(
    id              BIGINT AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    title           VARCHAR(30)  NOT NULL,
    content         VARCHAR(255) NOT NULL,
    image_url       VARCHAR(500) NULL,
    created_at      DATETIME(6)  NULL,
    updated_at      DATETIME(6)  NULL,
    like_cnt        INTEGER      NULL,
    min_temperature DOUBLE       NOT NULL,
    max_temperature DOUBLE       NOT NULL,

    CONSTRAINT posts_pk PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE likes
(
    id         BIGINT AUTO_INCREMENT,
    user_id    BIGINT      NOT NULL,
    post_id    BIGINT      NOT NULL,
    is_like    TINYINT     NOT NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,

    CONSTRAINT posts_pk PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
);
