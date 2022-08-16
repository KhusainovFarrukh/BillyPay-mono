CREATE SEQUENCE IF NOT EXISTS stats_id_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE stats
(
    id          BIGINT NOT NULL,
    start_date  date,
    end_date    date,
    amount      DOUBLE PRECISION,
    total_price DOUBLE PRECISION,
    bill_id     BIGINT NOT NULL,
    CONSTRAINT pk_stats PRIMARY KEY (id)
);

ALTER TABLE stats
    ADD CONSTRAINT FK_STATS_ON_BILL FOREIGN KEY (bill_id) REFERENCES bill (id);
