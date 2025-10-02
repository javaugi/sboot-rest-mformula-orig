/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.sql;

/**
 * @author javau
 */
public class PostgreSqlPivot {

}

/*
 * Here's the PostgreSQL equivalent query using CROSSTAB (tablefunc extension), which is
 * PostgreSQL's equivalent of PIVOT:
 * 
 * 1. First, enable the tablefunc extension: sql CREATE EXTENSION IF NOT EXISTS tablefunc;
 * 2. PostgreSQL equivalent using CROSSTAB: sql SELECT * FROM crosstab( 'SELECT
 * ROW_NUMBER() OVER (PARTITION BY Occupation ORDER BY Name) as rn, Occupation, Name FROM
 * OCCUPATIONS ORDER BY 1, 2', 'VALUES (''Doctor''), (''Professor''), (''Singer''),
 * (''Actor'')' ) AS ct(rn int, Doctor text, Professor text, Singer text, Actor text)
 * ORDER BY rn; 3. Alternative using CASE statements (more portable): sql SELECT MAX(CASE
 * WHEN Occupation = 'Doctor' THEN Name END) as Doctor, MAX(CASE WHEN Occupation =
 * 'Professor' THEN Name END) as Professor, MAX(CASE WHEN Occupation = 'Singer' THEN Name
 * END) as Singer, MAX(CASE WHEN Occupation = 'Actor' THEN Name END) as Actor FROM (
 * SELECT Name, Occupation, ROW_NUMBER() OVER (PARTITION BY Occupation ORDER BY Name) as
 * rn FROM OCCUPATIONS ) sub GROUP BY rn ORDER BY rn; 4. If you want to remove the row
 * number from the output: sql SELECT Doctor, Professor, Singer, Actor FROM ( SELECT
 * MAX(CASE WHEN Occupation = 'Doctor' THEN Name END) as Doctor, MAX(CASE WHEN Occupation
 * = 'Professor' THEN Name END) as Professor, MAX(CASE WHEN Occupation = 'Singer' THEN
 * Name END) as Singer, MAX(CASE WHEN Occupation = 'Actor' THEN Name END) as Actor FROM (
 * SELECT Name, Occupation, ROW_NUMBER() OVER (PARTITION BY Occupation ORDER BY Name) as
 * rn FROM OCCUPATIONS ) sub GROUP BY rn ) final_result ORDER BY COALESCE(Doctor,
 * Professor, Singer, Actor);
 * 
 * Key differences between Oracle and PostgreSQL: PIVOT vs CROSSTAB: Oracle uses PIVOT,
 * PostgreSQL uses crosstab() from the tablefunc extension
 * 
 * Syntax: The crosstab function takes two SQL strings as parameters
 * 
 * Type specification: PostgreSQL requires explicit type specification in the AS clause
 * 
 * Extension requirement: tablefunc extension must be installed in PostgreSQL
 * 
 * The CASE statement approach (option 3) is generally recommended because:
 * 
 * It doesn't require any extensions
 * 
 * It's more portable across different SQL databases
 * 
 * It's easier to read and maintain
 * 
 * It produces the exact same output format as your Oracle query
 * 
 * All solutions will give you the same result with four columns (Doctor, Professor,
 * Singer, Actor) and names sorted alphabetically under each occupation.
 */
