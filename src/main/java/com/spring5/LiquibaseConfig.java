/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5;

public class LiquibaseConfig {
    /*
  Best Practice Recommendation - The cleanest solution is typically to:
      Let Liquibase run first (before Hibernate initializes)
      Configure Hibernate to only validate the schema (not create/modify it)
          properties
          spring.jpa.hibernate.ddl-auto=validate
          spring.liquibase.enabled=true
     */

 /*
  @Primary
  @Bean
  public SpringLiquibase liquibase(DataSource dataSource) {
      SpringLiquibase liquibase = new SpringLiquibase();
      liquibase.setDataSource(dataSource);
      liquibase.setChangeLog("classpath:db_changelog/changelog-master.yaml");
      liquibase.setShouldRun(true);
      return liquibase;
  }
  // */
}

/*
Key Points:
    Never mix hibernate.hbm2ddl.auto=create/update with Liquibase
    Use validate or none for Hibernate's DDL auto
    Ensure Liquibase runs first with @DependsOn or proper ordering
    The DataSource should be created before both Liquibase and JPA
 */
