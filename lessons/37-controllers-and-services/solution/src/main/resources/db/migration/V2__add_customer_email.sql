-- Version 2: the shop wants to send receipts by e-mail, so customers get an optional e-mail address.
-- The column is optional (no NOT NULL) because the customers that exist today have none. Existing rows keep working.
ALTER TABLE customer ADD COLUMN email VARCHAR(100);
