/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.spring5.repository.auth;

import com.spring5.entity.auth.ApiClient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author javau
 */
public interface ApiClientRepository extends JpaRepository<ApiClient, Long> {

	Optional<ApiClient> findByApiKey(String apiKey);

}
