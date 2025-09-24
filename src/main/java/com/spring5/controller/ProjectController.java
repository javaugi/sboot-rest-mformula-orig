/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

import com.spring5.dto.ProjectDTO;
import com.spring5.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
// @Tag gives a name to this group of endpoints in the UI
@Tag(name = "Project Management", description = "APIs for managing development projects within the Order Management System")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation( // @Operation describes a single API operation
        summary = "Retrieve all projects",
        description = "Fetches a list of all project entities from the database."
    )
    @ApiResponse( // @ApiResponse describes a possible response
        responseCode = "200",
        description = "Successfully retrieved list of projects",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDTO[].class))
    )
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a project by its ID")
    @ApiResponses(value = { // Use @ApiResponses for multiple possible responses

        @ApiResponse(responseCode = "200", description = "Found the project", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDTO.class))}),
        @ApiResponse(responseCode = "404", description = "Project not found", content = @Content) // No content schema for 404
    })
    public ResponseEntity<ProjectDTO> getProjectById(
        @Parameter( // @Parameter describes a method parameter
            description = "ID of the project to be retrieved",
            required = true,
            example = "123"
        )
        @PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    @ApiResponse(
        responseCode = "201",
        description = "Project created successfully"
    )
    public ResponseEntity<ProjectDTO> createProject(
        @Parameter(
            description = "Project object to be created. The ID and dateCreated will be generated automatically and can be ignored.",
            required = true
        )
        @Valid @RequestBody ProjectDTO projectDto) {
        ProjectDTO savedProject = projectService.save(projectDto);
        return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Project updated successfully"),
        @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ProjectDTO> updateProject(
        @Parameter(description = "ID of the project to be updated", required = true, example = "123") @PathVariable Long id,
        @Parameter(description = "Updated project object", required = true) @Valid @RequestBody ProjectDTO projectDto) {
        return ResponseEntity.ok(projectService.update(id, projectDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project by its ID")
    @ApiResponse(responseCode = "204", description = "Project deleted successfully, no content to return")
    public ResponseEntity<Void> deleteProject(
        @Parameter(description = "ID of the project to be deleted", required = true, example = "123") @PathVariable Long id) {
        projectService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
