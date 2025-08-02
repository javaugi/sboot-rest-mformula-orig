INSERT INTO test_results (component_id, test_type, passed, test_parameters, test_metrics)
SELECT
    component_id,
    CASE WHEN random() < 0.8 THEN 'Functional Test' ELSE 'Performance Test' END, -- Example: Randomly assign test type
    random() < 0.9, -- Example: 90% chance of passing
    jsonb_build_object('temperature', random() * 100, 'pressure', random() * 1000), -- Example: Generate random parameters
    jsonb_build_object('duration_ms', random() * 500, 'error_rate', random() * 0.1) -- Example: Generate random metrics
FROM vehicle_components;
