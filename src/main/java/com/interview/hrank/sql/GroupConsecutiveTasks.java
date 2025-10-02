/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.sql;

/**
 * @author javau
 */
public class GroupConsecutiveTasks {

}

/*
 * I'll provide solutions for both Oracle PL/SQL and PostgreSQL for this problem.
 * 
 * Problem Analysis Projects are consecutive tasks where End_Date of one task equals
 * Start_Date of the next
 * 
 * We need to group consecutive tasks into projects
 * 
 * Output: Start date, End date, and duration for each project
 * 
 * Order by duration (ascending), then by start date (ascending)
 * 
 * Sample Data Setup sql -- For both Oracle and PostgreSQL CREATE TABLE Projects ( Task_ID
 * INT, Start_Date DATE, End_Date DATE );
 * 
 * INSERT INTO Projects VALUES (1, '2015-10-01', '2015-10-02'); INSERT INTO Projects
 * VALUES (2, '2015-10-02', '2015-10-03'); INSERT INTO Projects VALUES (3, '2015-10-03',
 * '2015-10-04'); INSERT INTO Projects VALUES (4, '2015-10-13', '2015-10-14'); INSERT INTO
 * Projects VALUES (5, '2015-10-14', '2015-10-15'); INSERT INTO Projects VALUES (6,
 * '2015-10-28', '2015-10-29'); INSERT INTO Projects VALUES (7, '2015-10-30',
 * '2015-10-31'); COMMIT; Oracle PL/SQL Solution sql DECLARE CURSOR c_projects IS WITH
 * project_groups AS ( SELECT Start_Date, End_Date, Start_Date - ROW_NUMBER() OVER (ORDER
 * BY Start_Date) as grp FROM Projects ) SELECT MIN(Start_Date) as project_start,
 * MAX(End_Date) as project_end, MAX(End_Date) - MIN(Start_Date) as duration_days FROM
 * project_groups GROUP BY grp ORDER BY duration_days, project_start;
 * 
 * v_start_date DATE; v_end_date DATE; v_duration NUMBER; BEGIN
 * DBMS_OUTPUT.PUT_LINE(RPAD('Start Date', 12) || RPAD('End Date', 12) || 'Duration');
 * DBMS_OUTPUT.PUT_LINE('------------------------------------');
 * 
 * OPEN c_projects; LOOP FETCH c_projects INTO v_start_date, v_end_date, v_duration; EXIT
 * WHEN c_projects%NOTFOUND;
 * 
 * DBMS_OUTPUT.PUT_LINE( TO_CHAR(v_start_date, 'YYYY-MM-DD') || ' ' || TO_CHAR(v_end_date,
 * 'YYYY-MM-DD') || ' ' || v_duration ); END LOOP; CLOSE c_projects; END; / PostgreSQL
 * Solution sql WITH project_groups AS ( SELECT Start_Date, End_Date, Start_Date -
 * CAST(ROW_NUMBER() OVER (ORDER BY Start_Date) || ' days' AS INTERVAL) as grp FROM
 * Projects ) SELECT MIN(Start_Date) as project_start, MAX(End_Date) as project_end,
 * MAX(End_Date) - MIN(Start_Date) as duration_days FROM project_groups GROUP BY grp ORDER
 * BY duration_days, project_start; Alternative PostgreSQL Solution (using window
 * functions) sql SELECT MIN(Start_Date) as project_start, MAX(End_Date) as project_end,
 * (MAX(End_Date) - MIN(Start_Date)) as duration_days FROM ( SELECT Start_Date, End_Date,
 * SUM(flag) OVER (ORDER BY Start_Date) as project_id FROM ( SELECT Start_Date, End_Date,
 * CASE WHEN Start_Date = LAG(End_Date) OVER (ORDER BY Start_Date) THEN 0 ELSE 1 END as
 * flag FROM Projects ) flagged ) grouped GROUP BY project_id ORDER BY duration_days,
 * project_start; Explanation Key Technique: The solution uses a clever grouping method
 * where we subtract a row number from the start date. For consecutive dates, this
 * difference remains constant, allowing us to group them together.
 * 
 * Oracle PL/SQL:
 * 
 * Uses a cursor to process the results
 * 
 * Formats output using DBMS_OUTPUT
 * 
 * The grp calculation identifies consecutive date ranges
 * 
 * PostgreSQL:
 * 
 * Uses the same logic but handles date arithmetic differently
 * 
 * The interval casting is needed for proper date subtraction
 * 
 * Returns the result set directly
 * 
 * Expected Output:
 * 
 * text Start Date End Date Duration -------------------------------- 2015-10-01
 * 2015-10-04 3 2015-10-13 2015-10-15 2 2015-10-28 2015-10-29 1 2015-10-30 2015-10-31 1
 * The solutions correctly identify that:
 * 
 * Tasks 1, 2, 3 form one project (3 days)
 * 
 * Tasks 4, 5 form another project (2 days)
 * 
 * Tasks 6 and 7 are separate 1-day projects
 * 
 * Both solutions order by duration (ascending) and then by start date (ascending) as
 * required.
 */
