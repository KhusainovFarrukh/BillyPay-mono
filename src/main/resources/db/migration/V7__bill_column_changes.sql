UPDATE bill
SET account_number = id;

ALTER TABLE bill
    ADD CONSTRAINT uk_bill_account_number UNIQUE (account_number);

ALTER TABLE bill
    ALTER COLUMN price SET NOT NULL;

ALTER TABLE bill
    ALTER COLUMN type SET NOT NULL;
