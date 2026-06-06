--create sequence user_id_seq start with 1 increment by 50;
--
--create table users
--(
--    id         bigint       not null default nextval('user_id_seq'),
--    email      varchar(255) not null,
--    password   varchar(255) not null,
--    name       varchar(255) not null,
--    role       varchar(20)  not null,
--    created_at timestamp    not null,
--    updated_at timestamp,
--    primary key (id),
--    constraint user_email_unique unique (email)
--);
--
--create sequence post_id_seq start with 1 increment by 50;
--
--create table posts
--(
--    id         bigint       not null default nextval('post_id_seq'),
--    title      varchar(250) not null,
--    slug       varchar(300) not null,
--    content    text         not null,
--    created_by bigint       not null references users (id),
--    created_at timestamp    not null,
--    updated_at timestamp,
--    primary key (id),
--    constraint posts_slug_unique unique (slug)
--);
--
--create sequence comment_id_seq start with 1 increment by 50;
--
--create table comments
--(
--    id         bigint       not null default nextval('comment_id_seq'),
--    post_id    bigint       not null references posts (id),
--    name       varchar(150) not null,
--    email      varchar(150),
--    content    text         not null,
--    created_at timestamp    not null,
--    updated_at timestamp,
--    primary key (id)
--);
--
CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    name       VARCHAR(100) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'ROLE_USER',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE short_urls
(
    id           BIGSERIAL PRIMARY KEY,
    short_key    VARCHAR(10) NOT NULL UNIQUE,
    original_url TEXT        NOT NULL,
    is_private   BOOLEAN     NOT NULL DEFAULT FALSE,
    expires_at   TIMESTAMP,
    created_by   BIGINT,
    click_count  BIGINT      NOT NULL DEFAULT 0,
    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_short_urls_users FOREIGN KEY (created_by) REFERENCES users (id)
);
