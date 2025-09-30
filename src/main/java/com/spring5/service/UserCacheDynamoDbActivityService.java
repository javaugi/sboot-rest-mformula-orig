/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service;

import com.spring5.dto.UserActivityDTO;
import com.spring5.dto.UserDTO;
import com.spring5.entity.User;
import com.spring5.repository.UserActivityRepository;
import com.spring5.repository.UserCacheRepository;
import com.spring5.repository.UserRepository;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserCacheDynamoDbActivityService {

    private final UserRepository userRepository; // PostgreSQL (JPA)
    private final UserCacheRepository userCacheRepository; // Redis
    private final UserActivityRepository userActivityRepository; // DynamoDB

    @Transactional
    public UserDTO createUser(UserDTO userDTO) throws Exception {
        // Validate input
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // PostgreSQL write
        User user = userRepository.save(convertToEntity(userDTO));

        // Redis cache warm-up
        userCacheRepository.cacheUser(user);

        // Return DTO
        return convertToDTO(user);
    }

    @Cacheable(value = "users", key = "#id")
    public UserDTO getUserById(Long id) {
        // Check Redis first
        User user = userCacheRepository.getUser(id);
        if (user != null) {
            return convertToDTO(user);
        }

        // Fallback to PostgreSQL
        user
                = userRepository
                        .findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Cache the result
        UserDTO userDTO = convertToDTO(user);
        userCacheRepository.cacheUser(userDTO);

        return userDTO;
    }

    public void recordUserActivity(UserActivityDTO activityDTO) {
        // Async write to DynamoDB for activity tracking
        CompletableFuture.runAsync(
                () -> {
                    userActivityRepository.save(convertToEntity(activityDTO));
                });
    }

    private UserDTO convertToDTO(User entity) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private User convertToEntity(UserDTO dto) {
        User entity = new User();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    private User convertToEntity(UserActivityDTO dto) {
        User entity = new User();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
