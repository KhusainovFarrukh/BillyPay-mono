ALTER TABLE stats
    ALTER COLUMN amount SET NOT NULL;

ALTER TABLE stats
    ALTER COLUMN start_date SET NOT NULL;

ALTER TABLE stats
    ALTER COLUMN total_price SET NOT NULL;

ALTER TABLE app_user
    DROP CONSTRAINT uk_app_user_email;
