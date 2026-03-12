-- categories Table
CREATE TABLE categories
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    parent_id  BIGINT                DEFAULT NULL, -- FK: 부모 카테고리의 id를 참조
    created_at TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE categories
    ADD CONSTRAINT fk_self_category
        FOREIGN KEY (parent_id) REFERENCES categories (id);

-- product Table
CREATE TABLE products
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_id BIGINT,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2) NOT NULL,
    stock       INT            NOT NULL DEFAULT 0,
    created_at  TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE products
    ADD CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories (id);

CREATE TABLE order_products
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id   BIGINT         NOT NULL, -- FK: 어떤 주문에 속하는지
    product_id BIGINT         NOT NULL, -- FK: 어떤 상품인지
    quantity   INT            NOT NULL, -- 주문 수량
    price      DECIMAL(10, 2) NOT NULL  -- 주문 시점의 상품 가격
);
ALTER TABLE order_products
    ADD CONSTRAINT fk_order_product_order_id
        FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE order_products
    ADD CONSTRAINT fk_order_product_product_id
        FOREIGN KEY (product_id) REFERENCES products (id);