CREATE TABLE IF NOT EXISTS bookings
(
    booking_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shopping_cart_id UUID NOT NULL,
    delivery_weight DOUBLE PRECISION NOT NULL,
    delivery_volume DOUBLE PRECISION NOT NULL,
    fragileBOOLEANNOT NULL
    );

CREATE UNIQUE INDEX bookings_shopping_cart_id_index
    ON bookings (shopping_cart_id);

CREATE TABLE IF NOT EXISTS warehouse_products
(
    product_id UUID PRIMARY KEY,
    fragile BOOLEANNOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    quantity_available INTEGERNOT NULL,
    width DOUBLE PRECISION NOT NULL,
    height DOUBLE PRECISION NOT NULL,
    depth DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS booking_products
(
    booking_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (booking_id, product_id),
    FOREIGN KEY (booking_id) REFERENCES bookings (booking_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES warehouse_products (product_id) ON DELETE CASCADE
    );