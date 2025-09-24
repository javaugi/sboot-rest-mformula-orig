/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service;

import com.spring5.dto.ProjectDTO;
import com.spring5.repository.ProjectRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectDTO> findAll() {
        return this.projectRepository.findAllProjects();
    }

    public ProjectDTO findById(Long id) {
        return ProjectDTO.builder().id(id).build();
    }

    public ProjectDTO save(ProjectDTO dto) {
        return dto;
    }

    public ProjectDTO update(Long id, ProjectDTO dto) {
        return dto;
    }

    public void deleteById(Long id) {
        this.projectRepository.deleteById(id);
    }
}
