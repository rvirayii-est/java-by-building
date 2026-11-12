-- Version 3: stop selling a product without deleting it (its sales history must stay). An inactive product is hidden
-- from the menu in a later version. NOT NULL with a DEFAULT: every product that exists today becomes active (TRUE).
ALTER TABLE product ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
