CREATE OR REPLACE PROCEDURE update_book_prices(
    category_param IN VARCHAR2,
    percentage IN NUMBER,
    updated_count OUT NUMBER
) AS
BEGIN
    UPDATE books
    SET price = price * (1 + percentage/100)
    WHERE category = category_param;
    
    updated_count := SQL%ROWCOUNT;
    COMMIT;
END;
