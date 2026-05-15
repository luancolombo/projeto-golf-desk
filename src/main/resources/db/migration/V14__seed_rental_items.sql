INSERT INTO rental_item (name, type, total_stock, available_stock, rental_price, active)
SELECT 'Buggy', 'Material', 60, 60, 55.00, true
WHERE NOT EXISTS (
    SELECT 1 FROM rental_item WHERE name = 'Buggy'
);

INSERT INTO rental_item (name, type, total_stock, available_stock, rental_price, active)
SELECT 'Trolley Manual', 'Material', 40, 40, 10.00, true
WHERE NOT EXISTS (
    SELECT 1 FROM rental_item WHERE name = 'Trolley Manual'
);

INSERT INTO rental_item (name, type, total_stock, available_stock, rental_price, active)
SELECT 'Trolley Eletrico', 'Material', 13, 13, 30.00, true
WHERE NOT EXISTS (
    SELECT 1 FROM rental_item WHERE name = 'Trolley Eletrico'
);

INSERT INTO rental_item (name, type, total_stock, available_stock, rental_price, active)
SELECT 'Set Clubs Callaway R', 'Clubs', 30, 30, 40.00, true
WHERE NOT EXISTS (
    SELECT 1 FROM rental_item WHERE name = 'Set Clubs Callaway R'
);

INSERT INTO rental_item (name, type, total_stock, available_stock, rental_price, active)
SELECT 'Set Clubs Callaway L', 'Clubs', 15, 15, 40.00, true
WHERE NOT EXISTS (
    SELECT 1 FROM rental_item WHERE name = 'Set Clubs Callaway L'
);
