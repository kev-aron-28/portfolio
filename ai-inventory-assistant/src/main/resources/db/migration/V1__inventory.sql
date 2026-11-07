CREATE TABLE product (sku varchar(255) PRIMARY KEY, name varchar(255) NOT NULL);
CREATE TABLE warehouse (code varchar(255) PRIMARY KEY, name varchar(255) NOT NULL);
CREATE TABLE stock (
    sku varchar(255) REFERENCES product(sku),
    warehouse varchar(255) REFERENCES warehouse(code),
    quantity bigint NOT NULL CHECK (quantity >= 0),
    PRIMARY KEY (sku, warehouse)
);
CREATE TABLE stock_movement (
    id uuid PRIMARY KEY,
    sku varchar(255) NOT NULL REFERENCES product(sku),
    warehouse varchar(255) NOT NULL REFERENCES warehouse(code),
    quantity bigint NOT NULL CHECK (quantity > 0),
    type varchar(255) NOT NULL CHECK (type IN ('ENTRY', 'EXIT')),
    status varchar(255) NOT NULL CHECK (status IN ('PENDING', 'APPLIED'))
);

INSERT INTO product(sku, name) VALUES
    ('LAP-001', 'Laptop de oficina'),
    ('MON-001', 'Monitor 24 pulgadas'),
    ('KEY-001', 'Teclado inalámbrico');

INSERT INTO warehouse(code, name) VALUES
    ('NORTH', 'North warehouse'),
    ('SOUTH', 'South warehouse');

INSERT INTO stock(sku, warehouse, quantity) VALUES
    ('LAP-001', 'NORTH', 12),
    ('LAP-001', 'SOUTH', 5),
    ('MON-001', 'NORTH', 20),
    ('KEY-001', 'SOUTH', 30);
