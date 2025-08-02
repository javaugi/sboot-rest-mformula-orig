CREATE OR REPLACE FUNCTION get_books_by_author(
    author_name VARCHAR,
    page_number INTEGER,
    page_size INTEGER
)
RETURNS TABLE (
    id BIGINT,
    title VARCHAR,
    author VARCHAR,
    isbn VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT b.id, b.title, b.author, b.isbn
    FROM books b
    WHERE b.author = author_name
    ORDER BY b.title
    LIMIT page_size
    OFFSET (page_number - 1) * page_size;
END;
$$ LANGUAGE plpgsql;
