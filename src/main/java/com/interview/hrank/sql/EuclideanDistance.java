/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.sql;

/**
 * @author javau
 */
public class EuclideanDistance {
}

/*
SELECT ROUND(
    SQRT(
        POWER(MAX(LAT_N) - MIN(LAT_N), 2) +
        POWER(MAX(LONG_W) - MIN(LONG_W), 2)
    )::numeric, 4
) AS euclidean_distance
FROM STATION;

SELECT ROUND(
    SQRT(
        POWER(MAX(LAT_N) - MIN(LAT_N), 2) +
        POWER(MAX(LONG_W) - MIN(LONG_W), 2)
    )::numeric, 4
) AS euclidean_distance
FROM STATION;
Alternative solution (more explicit casting):
sql
SELECT ROUND(
    SQRT(
        POWER((MAX(LAT_N) - MIN(LAT_N))::numeric, 2) +
        POWER((MAX(LONG_W) - MIN(LONG_W))::numeric, 2)
    ), 4
) AS euclidean_distance
FROM STATION;
Explanation:
Point 1: (min(LAT_N), min(LONG_W)) - the minimum values

Point 2: (max(LAT_N), max(LONG_W)) - the maximum values

Euclidean Distance Formula: √[(x₂ - x₁)² + (y₂ - y₁)²]

Where x₁ = min(LAT_N), x₂ = max(LAT_N)

y₁ = min(LONG_W), y₂ = max(LONG_W)

Components:

MAX(LAT_N) - MIN(LAT_N) = difference in x-coordinates

MAX(LONG_W) - MIN(LONG_W) = difference in y-coordinates

POWER(difference, 2) = squares the differences

SQRT(sum_of_squares) = square root of the sum

ROUND(result, 4) = rounds to 4 decimal places

PostgreSQL-specific: The ::numeric cast ensures proper type handling for the mathematical operations.

Example:
If the data has:

min(LAT_N) = 20, max(LAT_N) = 200

min(LONG_W) = 30, max(LONG_W) = 180

The calculation would be:

√[(200 - 20)² + (180 - 30)²] = √[180² + 150²] = √[32400 + 22500] = √54900 ≈ 234.3075

This solution will work correctly in PostgreSQL and display the result with exactly 4 decimal places.
 */
