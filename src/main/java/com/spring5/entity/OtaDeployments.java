/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity;

/**
 *
 * @author javau
 */
public class OtaDeployments {
    
}
/*
CREATE TABLE ota_deployments (
    deployment_id UUID PRIMARY KEY,
    package_id UUID REFERENCES software_packages(package_id),
    deployment_name VARCHAR(100) NOT NULL,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ,
    rollout_strategy JSONB NOT NULL, -- Phased rollout parameters
    status VARCHAR(20) NOT NULL CHECK (status IN ('scheduled', 'in_progress', 'completed', 'paused'))
);
*/