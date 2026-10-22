


-- Insert categories
INSERT INTO public.category (id, description, name)
VALUES
    (nextval('category_seq'), 'Computer keyboards and accessories', 'Keyboard'),
    (nextval('category_seq'), 'Computer monitors and displays', 'Monitors'),
    (nextval('category_seq'), 'Computer mice and accessories', 'Mouse'),
    (nextval('category_seq'), 'Laptop computers and accessories', 'Laptops'),
    (nextval('category_seq'), 'Computer accessories and peripherals', 'Accessories');


-- Insert products for the 'Keyboard' category
INSERT INTO public.product
(id, description, name, available_quantity, price, category_id)
VALUES
    (nextval('product_seq'), 'Mechanical gaming keyboard with RGB lighting',
     'Mechanical Keyboard', 50, 2499.00,
     (SELECT id FROM public.category WHERE name = 'Keyboard')),

    (nextval('product_seq'), 'Wireless keyboard with ergonomic design',
     'Wireless Keyboard', 30, 1499.00,
     (SELECT id FROM public.category WHERE name = 'Keyboard'));




-- Insert products for the 'Monitors' category
INSERT INTO public.product
(id, description, name, available_quantity, price, category_id)
VALUES
    (nextval('product_seq'), '24-inch Full HD LED monitor',
     'Dell 24 Inch Monitor', 20, 12999.00,
     (SELECT id FROM public.category WHERE name = 'Monitors')),

    (nextval('product_seq'), '27-inch QHD IPS monitor',
     'LG 27 Inch Monitor', 15, 18999.00,
     (SELECT id FROM public.category WHERE name = 'Monitors'));


-- Mouse products
INSERT INTO public.product
(id, description, name, available_quantity, price, category_id)
VALUES
    (nextval('product_seq'), 'Wireless optical mouse',
     'Logitech Wireless Mouse', 40, 799.00,
     (SELECT id FROM public.category WHERE name = 'Mouse'));


-- Laptop products
INSERT INTO public.product
(id, description, name, available_quantity, price, category_id)
VALUES
    (nextval('product_seq'), 'Business laptop with Intel processor',
     'Dell Laptop', 10, 54999.00,
     (SELECT id FROM public.category WHERE name = 'Laptops'));


-- Accessories products
INSERT INTO public.product
(id, description, name, available_quantity, price, category_id)
VALUES
    (nextval('product_seq'), 'USB-C multiport adapter',
     'USB-C Hub', 25, 1999.00,
     (SELECT id FROM public.category WHERE name = 'Accessories'));