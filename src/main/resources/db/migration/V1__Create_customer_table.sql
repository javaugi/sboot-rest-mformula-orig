/*
 src/main/resources/db/migration/V1__Create_customer_table.sql
*/
CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

/* V2__Add_phone_column.sql */
ALTER TABLE customer ADD COLUMN phone VARCHAR(20);
