-- Version 5: every user gets a role. The people who already exist (from version 4) must not lose their login, so the new column
-- has a DEFAULT: everybody becomes a CASHIER, the least powerful role. Then the user called admin is promoted to ADMIN.
ALTER TABLE app_user ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'CASHIER';
UPDATE app_user SET role = 'ADMIN' WHERE username = 'admin';
