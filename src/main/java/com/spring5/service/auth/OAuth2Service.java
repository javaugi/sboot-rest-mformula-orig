/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service.auth;

import com.spring5.dto.auth.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class OAuth2Service {

	public OAuth2UserInfo getUserInfo(String provider, String code) {
		return OAuth2UserInfo.builder().provider(provider).code(code).build();
	}

	public String getAuthorizationUrl(String provider) {
		return "https://" + provider;
	}

}
