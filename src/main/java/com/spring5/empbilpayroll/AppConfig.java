/*
 * Copyright (C) 2019 Strategic Information Systems, LLC.
 *
 */
package com.spring5.empbilpayroll;

public class AppConfig {

    /*
  public static void main(String[] args) {
      SpringApplication.run(AppConfig.class, args);
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
      LocalContainerEntityManagerFactoryBean factory
              = new LocalContainerEntityManagerFactoryBean();
      factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
      return factory;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
      JpaTransactionManager txManager = new JpaTransactionManager();
      txManager.setEntityManagerFactory(entityManagerFactory);
      return txManager;
  }
  // */
}
