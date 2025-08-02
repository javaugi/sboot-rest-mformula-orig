In PostgreSQL, Data Definition Language (DDL) commands are used to define, modify, and delete the structure of database objects like tables, schemas, indexes, etc. 
Here are the key DDL commands in PostgreSQL with examples:
1. CREATE
Purpose: To create database objects.
Examples:
Create a table:
sql
CREATE TABLE CUSTOMERS(
   InsuranceID INT,
   Name VARCHAR(50),
   DOB DATE,
   NIN INT,
   Location VARCHAR(255)
);
This statement creates a table named CUSTOMERS with the specified columns and their data types.
Create a schema:
sql
CREATE SCHEMA my_schema;
Create an index:
sql
CREATE INDEX customer_name_idx ON CUSTOMERS (Name);
 
2. ALTER
Purpose: To modify the structure of existing database objects.
Examples:
Add a column to a table:
sql
ALTER TABLE CUSTOMERS ADD COLUMN email_id VARCHAR(50);
Modify a column's data type:
sql
ALTER TABLE CUSTOMERS ALTER COLUMN name TYPE VARCHAR(100);
Rename a column:
sql
ALTER TABLE CUSTOMERS RENAME COLUMN email_id TO email;
Add a constraint:
sql
ALTER TABLE CUSTOMERS ADD CONSTRAINT unique_email UNIQUE (email);
Rename a table:
sql
ALTER TABLE CUSTOMERS RENAME TO CUSTOMERINFO;
 
3. DROP
Purpose: To delete database objects.
Examples:
Drop a table:
sql
DROP TABLE CUSTOMERS;
This command deletes the table and all its data.
Drop a schema:
sql
DROP SCHEMA my_schema CASCADE;
The CASCADE option removes dependent objects as well.
Drop an index:
sql
DROP INDEX customer_name_idx;
 
4. TRUNCATE
Purpose: To quickly remove all data from a table, but not the table structure itself.
Example:
sql
TRUNCATE TABLE CUSTOMERS;
This removes all rows from the CUSTOMERS table efficiently. 
5. COMMENT
Purpose: To add comments to the data dictionary, explaining the purpose of database objects.
Example:
sql
COMMENT ON TABLE CUSTOMERS IS 'Stores information about customers';
 
Important Notes:
DDL commands are irreversible: Be cautious when using DROP or TRUNCATE commands as they permanently delete data.
Permissions: You typically need specific permissions (like table ownership or superuser privileges) to execute DDL commands.
Dependencies: Be aware of dependencies when dropping tables or other objects. Use CASCADE if needed, but understand the implications. 
