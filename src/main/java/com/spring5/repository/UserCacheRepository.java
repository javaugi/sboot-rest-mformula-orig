/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.repository;

import com.spring5.dto.UserDTO;
import com.spring5.entity.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@CacheConfig(cacheNames = "users")
@Repository
public interface UserCacheRepository extends JpaRepository<User, Long> {

    void cacheUser(@Param("user") User user);

    void cacheUser(@Param("userDTO") UserDTO userDTO);

    User getUser(@Param("id") Long id);
}
