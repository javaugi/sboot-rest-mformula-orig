/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

// Using @Schema annotations to document the DTO
@Data
@Builder(toBuilder = true)
public class ProjectDTO {

    @Schema(
            description = "Unique identifier of the project",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(
            description = "Name of the project",
            example = "Enterprise Fulfillment Dashboard",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Schema(
            description = "Detailed description of the project",
            example = "A microservice to handle all fulfillment operations.")
    private String description;

    @Schema(
            description = "Timestamp when the project was created",
            example = "2024-10-27T10:30:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dateCreated;

    // Standard getters and setters
    // ...
}
