/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DxcgClientConfig {

    /*
  @Bean
  @ConditionalOnProperty(name = "app.dxcg.client-type", havingValue = "REAL")
  public DxcgClient realDxcgClient() {
      return new VeriskDxcgClient(restTemplateBuilder, objectMapper);
  }

  @Bean
  @ConditionalOnProperty(name = "app.dxcg.client-type", havingValue = "STUB", matchIfMissing = true)
  public DxcgClient stubDxcgClient() {
      return new DxcgStubClient();
  }
  // */
}
