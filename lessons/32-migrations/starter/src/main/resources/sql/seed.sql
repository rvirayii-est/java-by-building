-- Sample rows: the shop after three busy days (1-3 March 2026). Rows are numbered 1, 2, 3 ... in the order written.
-- Stock on the shelf = RECEIVED minus SOLD in stock_movement (the same rule Inventory.audit checks in Java).

INSERT INTO product (name, price, reorder_level, quantity) VALUES
    ('Rice 25kg',      1250.50, 10, 17),
    ('Cooking Oil 1L',   98.00, 10, 12),
    ('Sugar 1kg',        75.00, 10,  4),
    ('Sardines 155g',    24.00, 20, 30),
    ('Soy Sauce 500ml',  38.00,  5,  0),
    ('Instant Noodles',  14.00, 30,  5);

INSERT INTO customer (name, phone) VALUES
    ('Ana Reyes',   '09171234567'),
    ('Ben Santos',  '09189876543'),
    ('Carla Dizon', '09175550123');

-- Sale 2 and sale 5 have no customer (walk-ins).
INSERT INTO sale (customer_id, sold_at, discount, payment) VALUES
    (1,    TIMESTAMP '2026-03-01 09:30:00', 'none',       'CASH'),
    (NULL, TIMESTAMP '2026-03-01 10:15:00', 'none',       'GCASH'),
    (2,    TIMESTAMP '2026-03-02 14:05:00', 'percent:10', 'CASH'),
    (1,    TIMESTAMP '2026-03-02 16:40:00', 'senior',     'CARD'),
    (NULL, TIMESTAMP '2026-03-03 09:10:00', 'none',       'CASH');

INSERT INTO sale_line (sale_id, product_id, unit_price, quantity) VALUES
    (1, 1, 1250.50,  2),
    (2, 4,   24.00,  6),
    (2, 6,   14.00, 10),
    (3, 2,   98.00,  3),
    (3, 3,   75.00,  4),
    (4, 1, 1250.50,  1),
    (4, 4,   24.00,  4),
    (5, 6,   14.00, 25);

INSERT INTO stock_movement (product_id, type, quantity, moved_at) VALUES
    (1, 'RECEIVED', 20, TIMESTAMP '2026-03-01 08:00:00'),
    (2, 'RECEIVED', 15, TIMESTAMP '2026-03-01 08:00:00'),
    (3, 'RECEIVED',  8, TIMESTAMP '2026-03-01 08:00:00'),
    (4, 'RECEIVED', 40, TIMESTAMP '2026-03-01 08:00:00'),
    (6, 'RECEIVED', 40, TIMESTAMP '2026-03-01 08:00:00'),
    (1, 'SOLD',      2, TIMESTAMP '2026-03-01 09:30:00'),
    (4, 'SOLD',      6, TIMESTAMP '2026-03-01 10:15:00'),
    (6, 'SOLD',     10, TIMESTAMP '2026-03-01 10:15:00'),
    (2, 'SOLD',      3, TIMESTAMP '2026-03-02 14:05:00'),
    (3, 'SOLD',      4, TIMESTAMP '2026-03-02 14:05:00'),
    (1, 'SOLD',      1, TIMESTAMP '2026-03-02 16:40:00'),
    (4, 'SOLD',      4, TIMESTAMP '2026-03-02 16:40:00'),
    (6, 'SOLD',     25, TIMESTAMP '2026-03-03 09:10:00');
