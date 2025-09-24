/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.sql;

/**
 *
 * @author javau
 */
public class OraclePivot {

}
/*
I'll help you create Oracle PL/SQL code to pivot the Occupation data. Here's a comprehensive solution:

1. First, let's create a sample table and data:
sql
-- Create the OCCUPATIONS table
CREATE TABLE OCCUPATIONS (
    Name VARCHAR2(50),
    Occupation VARCHAR2(50)
);

-- Insert sample data
INSERT INTO OCCUPATIONS VALUES ('Samantha', 'Doctor');
INSERT INTO OCCUPATIONS VALUES ('Julia', 'Actor');
INSERT INTO OCCUPATIONS VALUES ('Maria', 'Actor');
INSERT INTO OCCUPATIONS VALUES ('Meera', 'Singer');
INSERT INTO OCCUPATIONS VALUES ('Ashley', 'Professor');
INSERT INTO OCCUPATIONS VALUES ('Ketty', 'Professor');
INSERT INTO OCCUPATIONS VALUES ('Christeen', 'Professor');
INSERT INTO OCCUPATIONS VALUES ('Jane', 'Actor');
INSERT INTO OCCUPATIONS VALUES ('Jenny', 'Doctor');
INSERT INTO OCCUPATIONS VALUES ('Priya', 'Singer');
COMMIT;
2. PL/SQL Solution using PIVOT and Dynamic SQL:
sql
DECLARE
    v_sql VARCHAR2(4000);
    v_result SYS_REFCURSOR;
    v_doctor VARCHAR2(50);
    v_professor VARCHAR2(50);
    v_singer VARCHAR2(50);
    v_actor VARCHAR2(50);
    v_row_num NUMBER := 1;
BEGIN
    -- Dynamic SQL to pivot the data
    v_sql := '
        SELECT Doctor, Professor, Singer, Actor
        FROM (
            SELECT Name, Occupation,
                   ROW_NUMBER() OVER (PARTITION BY Occupation ORDER BY Name) as rn
            FROM OCCUPATIONS
        )
        PIVOT (
            MAX(Name)
            FOR Occupation IN (
                ''Doctor'' as Doctor,
                ''Professor'' as Professor,
                ''Singer'' as Singer,
                ''Actor'' as Actor
            )
        )
        ORDER BY rn';

    -- Open the cursor
    OPEN v_result FOR v_sql;

    -- Display header
    DBMS_OUTPUT.PUT_LINE(RPAD('Doctor', 15) || RPAD('Professor', 15) ||
                         RPAD('Singer', 15) || RPAD('Actor', 15));
    DBMS_OUTPUT.PUT_LINE('------------------------------------------------------------');

    -- Fetch and display results
    LOOP
        FETCH v_result INTO v_doctor, v_professor, v_singer, v_actor;
        EXIT WHEN v_result%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(
            RPAD(NVL(v_doctor, 'NULL'), 15) ||
            RPAD(NVL(v_professor, 'NULL'), 15) ||
            RPAD(NVL(v_singer, 'NULL'), 15) ||
            RPAD(NVL(v_actor, 'NULL'), 15)
        );
    END LOOP;

    CLOSE v_result;
END;
/
3. Alternative Solution using CASE statements:
sql
DECLARE
    CURSOR c_occupations IS
        SELECT
            MAX(CASE WHEN Occupation = 'Doctor' THEN Name END) as Doctor,
            MAX(CASE WHEN Occupation = 'Professor' THEN Name END) as Professor,
            MAX(CASE WHEN Occupation = 'Singer' THEN Name END) as Singer,
            MAX(CASE WHEN Occupation = 'Actor' THEN Name END) as Actor
        FROM (
            SELECT Name, Occupation,
                   ROW_NUMBER() OVER (PARTITION BY Occupation ORDER BY Name) as rn
            FROM OCCUPATIONS
        )
        GROUP BY rn
        ORDER BY rn;

    v_doctor VARCHAR2(50);
    v_professor VARCHAR2(50);
    v_singer VARCHAR2(50);
    v_actor VARCHAR2(50);
BEGIN
    -- Display header
    DBMS_OUTPUT.PUT_LINE(RPAD('Doctor', 15) || RPAD('Professor', 15) ||
                         RPAD('Singer', 15) || RPAD('Actor', 15));
    DBMS_OUTPUT.PUT_LINE('------------------------------------------------------------');

    -- Process each row
    OPEN c_occupations;
    LOOP
        FETCH c_occupations INTO v_doctor, v_professor, v_singer, v_actor;
        EXIT WHEN c_occupations%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(
            RPAD(NVL(v_doctor, 'NULL'), 15) ||
            RPAD(NVL(v_professor, 'NULL'), 15) ||
            RPAD(NVL(v_singer, 'NULL'), 15) ||
            RPAD(NVL(v_actor, 'NULL'), 15)
        );
    END LOOP;
    CLOSE c_occupations;
END;
/
4. Simple SQL Solution (if you just need the query):
sql
SELECT
    MAX(CASE WHEN Occupation = 'Doctor' THEN Name END) as Doctor,
    MAX(CASE WHEN Occupation = 'Professor' THEN Name END) as Professor,
    MAX(CASE WHEN Occupation = 'Singer' THEN Name END) as Singer,
    MAX(CASE WHEN Occupation = 'Actor' THEN Name END) as Actor
FROM (
    SELECT Name, Occupation,
           ROW_NUMBER() OVER (PARTITION BY Occupation ORDER BY Name) as rn
    FROM OCCUPATIONS
)
GROUP BY rn
ORDER BY rn;
Expected Output:
text
Doctor         Professor      Singer         Actor
------------------------------------------------------------
Jenny          Ashley         Meera          Jane
Samantha       Christeen      Priya          Julia
NULL           Ketty          NULL           Maria

The solution:
    Uses ROW_NUMBER() to assign row numbers within each occupation group
    Pivots the data using either PIVOT operator or CASE statements
    Orders names alphabetically within each occupation
    Displays the results in the required format with four columns
    Handles NULL values for occupations that have fewer entries

Choose the solution that best fits your needs. The PL/SQL solutions provide formatted output, while the SQL solution gives you the raw result set.
*/
