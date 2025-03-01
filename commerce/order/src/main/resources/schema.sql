CREATE TABLE IF NOT EXISTS addresses (
    address_id UUID PRIMARY KEY,
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house VARCHAR(255) NOT NULL,
    flat VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY,
    shopping_cart_id UUID,
    payment_id UUID,
    delivery_id UUID,
    order_state VARCHAR(50) NOT NULL,
    delivery_weight DOUBLE PRECISION NOT NULL,
    delivery_volume DOUBLE PRECISION NOT NULL,
    fragile BOOLEAN NOT NULL,
    total_price NUMERIC(15, 2) NOT NULL,
    delivery_price NUMERIC(15, 2) NOT NULL,
    product_price NUMERIC(15, 2) NOT NULL,
    from_address_id UUID NOT NULL,
    to_address_id UUID NOT NULL,
    FOREIGN KEY (from_address_id) REFERENCES addresses(address_id),
    FOREIGN KEY (to_address_id) REFERENCES addresses(address_id)
);

CREATE TABLE IF NOT EXISTS order_products (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);