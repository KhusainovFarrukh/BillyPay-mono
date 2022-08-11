CREATE SEQUENCE IF NOT EXISTS image_id_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS app_user_id_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE image
(
    id      BIGINT NOT NULL,
    content BYTEA,
    CONSTRAINT pk_image PRIMARY KEY (id)
);

CREATE TABLE app_user
(
    id           BIGINT                NOT NULL,
    name         VARCHAR(255),
    email        VARCHAR(255)          NOT NULL,
    phone_number VARCHAR(255)          NOT NULL,
    password     VARCHAR(255)          NOT NULL,
    is_enabled   BOOLEAN DEFAULT TRUE  NOT NULL,
    is_locked    BOOLEAN DEFAULT FALSE NOT NULL,
    role         VARCHAR(255),
    image_id     BIGINT,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE app_user
    ADD CONSTRAINT uk_app_user_email UNIQUE (email);

ALTER TABLE app_user
    ADD CONSTRAINT uk_app_user_phone_number UNIQUE (phone_number);

ALTER TABLE app_user
    ADD CONSTRAINT fk_image_id_of_app_user FOREIGN KEY (image_id) REFERENCES image (id);
