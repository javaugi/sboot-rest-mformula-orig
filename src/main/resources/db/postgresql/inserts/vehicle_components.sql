INSERT INTO vehicle_components (component_name, component_type, manufacturer, specifications)
SELECT
    'Component ' || gs::text, -- Generate unique component names
    CASE floor(random() * 3)
        WHEN 0 THEN 'Engine'
        WHEN 1 THEN 'Transmission'
        ELSE 'Brakes'
    END, -- Generate random component types
    'Manufacturer ' || floor(random() * 5)::text, -- Generate random manufacturers
    jsonb_build_object('weight', random() * 1000, 'material', 'Steel') -- Generate random JSONB specifications
FROM generate_series(1, 1000) gs; 
/* -- Generate 1 million rows */
