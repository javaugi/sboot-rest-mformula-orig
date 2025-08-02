/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity;

import com.spring5.type.VehicleUpdateStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //it includes @Getter @Setter @ToString @EqualsAndHashCode @RequiredArgsConstructor 
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VehicleUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private VehicleUpdateStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String errorLog;
        
}
/*
CREATE TABLE vehicle_updates (
    update_id BIGSERIAL PRIMARY KEY,
    deployment_id UUID REFERENCES ota_deployments(deployment_id),
    vehicle_id VARCHAR(17) REFERENCES vehicles(vehicle_id),
    attempt_time TIMESTAMPTZ NOT NULL,
    completion_time TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL CHECK (status IN ('pending', 'downloading', 'installing', 'success', 'failed')),
    error_code VARCHAR(50),
    network_type VARCHAR(20),
    download_speed_kbps INTEGER,
    battery_level INTEGER,
    CONSTRAINT successful_completion CHECK (
        (status = 'success' AND completion_time IS NOT NULL) OR 
        (status != 'success' AND completion_time IS NULL)
    )
) PARTITION BY RANGE (attempt_time);
*/