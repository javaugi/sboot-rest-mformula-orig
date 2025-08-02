CREATE PROCEDURE sp_archive_old_books
    @cutoff_date DATE,
    @archive_year INT,
    @rows_affected INT OUTPUT
AS
BEGIN
    INSERT INTO archived_books
    SELECT *, @archive_year 
    FROM books 
    WHERE published_date < @cutoff_date;
    
    DELETE FROM books
    WHERE published_date < @cutoff_date;
    
    SET @rows_affected = @@ROWCOUNT;
END
