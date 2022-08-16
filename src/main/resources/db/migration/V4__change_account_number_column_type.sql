ALTER TABLE bill
    RENAME COLUMN account_number TO account_number_old;

ALTER TABLE bill
    ADD account_number VARCHAR(255);

UPDATE bill
SET account_number=account_number_old;

ALTER TABLE bill
    DROP COLUMN account_number_old;
