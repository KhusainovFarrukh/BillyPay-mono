CREATE SEQUENCE IF NOT EXISTS bill_id_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE bill
(
    id             BIGINT NOT NULL,
    address        VARCHAR(255),
    account_number INTEGER,
    type           VARCHAR(255),
    price          DOUBLE PRECISION,
    owner_id       BIGINT NOT NULL,
    CONSTRAINT pk_bill PRIMARY KEY (id)
);

ALTER TABLE bill
    ADD CONSTRAINT FK_BILL_ON_OWNER FOREIGN KEY (owner_id) REFERENCES app_user (id);
