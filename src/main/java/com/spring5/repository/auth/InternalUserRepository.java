/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.spring5.repository.auth;

import com.spring5.entity.auth.InternalUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author javau
 */
public interface InternalUserRepository extends JpaRepository<InternalUser, Long> {

    Optional<InternalUser> findByUsername(String username);
}
