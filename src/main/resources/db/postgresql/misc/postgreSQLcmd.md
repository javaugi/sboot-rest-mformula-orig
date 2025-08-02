AI Overview
PostgreSQL's interactive command-line tool, psql, offers a variety of commands to manage and query databases. Here's a breakdown of some key psql commands: 
Basic Commands:

REFRESH MATERIALIZED VIEW your_materialized_view_name;

\\l: Lists all databases accessible by the user.
\\c <database_name>: Switches to a different database.
\\dt: Lists all tables in the current database.
\\d <table_name>: Describes the schema of a specific table, showing column names, data types, and constraints.
\\du: Lists all users and roles in the database.
\\?: Displays a help message with a list of available psql commands.
\\q: Quits the psql session. 
Other Useful Commands:
\\e: Edits the query buffer in your system's default editor.
\\i <file_name>: Executes SQL commands from a file.
\\x: Toggles between expanded and aligned output formats.
\\timing: Toggles the display of query execution time.
\\s: Shows the command history.
\\copy: Copies data between a table and a file.
\\o <file_name>: Redirects the output of psql to a file. 
SQL Commands (Beyond psql): 
PostgreSQL also supports a wide range of SQL commands for database management, including: 
CREATE DATABASE: Creates a new database.
CREATE TABLE: Creates a new table with defined columns and data types.
INSERT INTO: Adds new rows of data into a table.
SELECT: Retrieves data from one or more tables.
UPDATE: Modifies existing data in a table.
DELETE: Removes rows of data from a table.
ALTER TABLE: Modifies the structure of an existing table.
DROP TABLE: Removes a table from the database.
GRANT: Assigns permissions to users or roles.
REVOKE: Removes permissions from users or roles. 
Example:
To connect to a PostgreSQL database named "my_database" and list its tables:
Open a terminal or command prompt.
Run the command: psql -d my_database.
Enter your password if prompted.
Once connected, use \\dt to list the tables.
Use \\d <table_name> to describe a specific table. 



Chapter 9. Operators
Postgres provides a large number of built-in operators on system types. These operators are declared in the system catalog pg_operator. Every entry in pg_operator includes the name of the procedure that implements the operator and the class OIDs of the input and output types.

To view all variations of the “||” string concatenation operator, try

    SELECT oprleft, oprright, oprresult, oprcode
    FROM pg_operator WHERE oprname = '||';

oprleft|oprright|oprresult|oprcode
-------+--------+---------+-------
     25|      25|       25|textcat
   1042|    1042|     1042|textcat
   1043|    1043|     1043|textcat
(3 rows)
Table 9-1. Postgres Operators

Operator	Description	Usage
<	Less than?	1 < 2
<=	Less than or equal to?	1 <= 2
<>	Not equal?	1 <> 2
=	Equal?	1 = 1
>	Greater than?	2 > 1
>=	Greater than or equal to?	2 >= 1
||	Concatenate strings	'Postgre' || 'SQL'
!!=	NOT IN	3 !!= i
~~	LIKE	'scrappy,marc,hermit' ~~ '%scrappy%'
!~~	NOT LIKE	'bruce' !~~ '%al%'
~	Match (regex), case sensitive	'thomas' ~ '*.thomas*.'
~*	Match (regex), case insensitive	'thomas' ~* '*.Thomas*.'
!~	Does not match (regex), case sensitive	'thomas' !~ '*.Thomas*.'
!~*	Does not match (regex), case insensitive	'thomas' !~ '*.vadim*.'
Table 9-2. Postgres Numerical Operators

Operator	Description	Usage
!	Factorial	3 !
!!	Factorial (left operator)	!! 3
%	Modulo	5 % 4
%	Truncate	% 4.5
*	Multiplication	2 * 3
+	Addition	2 + 3
-	Subtraction	2 - 3
/	Division	4 / 2
:	Natural Exponentiation	: 3.0
;	Natural Logarithm	(; 5.0)
@	Absolute value	@ -5.0
^	Exponentiation	2.0 ^ 3.0
|/	Square root	|/ 25.0
||/	Cube root	||/ 27.0
Table 9-3. Postgres Geometric Operators

Operator	Description	Usage
+	Translation	'((0,0),(1,1))'::box + '(2.0,0)'::point
-	Translation	'((0,0),(1,1))'::box - '(2.0,0)'::point
*	Scaling/rotation	'((0,0),(1,1))'::box * '(2.0,0)'::point
/	Scaling/rotation	'((0,0),(2,2))'::box / '(2.0,0)'::point
#	Intersection	'((1,-1),(-1,1))' # '((1,1),(-1,-1))'
#	Number of points in polygon	# '((1,0),(0,1),(-1,0))'
##	Point of closest proximity	'(0,0)'::point ## '((2,0),(0,2))'::lseg
&&	Overlaps?	'((0,0),(1,1))'::box && '((0,0),(2,2))'::box
&<	Overlaps to left?	'((0,0),(1,1))'::box &< '((0,0),(2,2))'::box
&>	Overlaps to right?	'((0,0),(3,3))'::box &> '((0,0),(2,2))'::box
<->	Distance between	'((0,0),1)'::circle <-> '((5,0),1)'::circle
<<	Left of?	'((0,0),1)'::circle << '((5,0),1)'::circle
<^	Is below?	'((0,0),1)'::circle <^ '((0,5),1)'::circle
>>	Is right of?	'((5,0),1)'::circle >> '((0,0),1)'::circle
>^	Is above?	'((0,5),1)'::circle >^ '((0,0),1)'::circle
?#	Intersects or overlaps	'((-1,0),(1,0))'::lseg ?# '((-2,-2),(2,2))'::box;
?-	Is horizontal?	'(1,0)'::point ?- '(0,0)'::point
?-|	Is perpendicular?	'((0,0),(0,1))'::lseg ?-| '((0,0),(1,0))'::lseg
@-@	Length or circumference	@-@ '((0,0),(1,0))'::path
?|	Is vertical?	'(0,1)'::point ?| '(0,0)'::point
?||	Is parallel?	'((-1,0),(1,0))'::lseg ?|| '((-1,2),(1,2))'::lseg
@	Contained or on	'(1,1)'::point @ '((0,0),2)'::circle
@@	Center of	@@ '((0,0),10)'::circle
~=	Same as	'((0,0),(1,1))'::polygon ~= '((1,1),(0,0))'::polygon
The time interval data type tinterval is a legacy from the original date/time types and is not as well supported as the more modern types. There are several operators for this type.

Table 9-4. Postgres Time Interval Operators

Operator	Description	Usage
#<	Interval less than?	
#<=	Interval less than or equal to?	
#<>	Interval not equal?	
#=	Interval equal?	
#>	Interval greater than?	
#>=	Interval greater than or equal to?	
<#>	Convert to time interval	
<<	Interval less than?	
|	Start of interval	
~=	Same as	
<?>	Time inside interval?	

/*
-> returns json (or jsonb) and ->> returns text:
*/
with t (jo, ja) as (values
    ('{"a":"b"}'::jsonb,('[1,2]')::jsonb)
)
select
    pg_typeof(jo -> 'a'), pg_typeof(jo ->> 'a'),
    pg_typeof(ja -> 1), pg_typeof(ja ->> 1)
from t
;

Users may invoke operators using the operator name, as in: