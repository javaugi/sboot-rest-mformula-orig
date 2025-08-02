/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 *
 * @author javaugi
 */
@Service
@org.springframework.core.annotation.Order(10)
public class BSTNativeQueryService implements CommandLineRunner{
    private static final Logger log = LoggerFactory.getLogger(BSTNativeQueryService.class);

    @Autowired
    private EntityManagerFactory emf;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SessionFactory sessionFactory;

    @PostConstruct
    public void checkEntities() {
        try {
            System.out.println("EntityManagerFactory    Managed types: " + emf.getMetamodel().getEntities());
            System.out.println("EntityManager           Managed types: " + entityManager.getMetamodel().getEntities());
            System.out.println("SessionFactory          Managed types: " + sessionFactory.getMetamodel().getEntities());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> doQuery(String qString) {
        List<String> returnValue = new ArrayList<>();
        Query query = entityManager.createNativeQuery(qString);
        List<Object[]> list = (List<Object[]>)query.getResultList();

        StringBuilder sb;
        for (Object[] obj: list) {
            sb = new StringBuilder();
            for (Object o: obj) {
                sb.append(String.valueOf(o));
                sb.append("     ");
            }
            
            returnValue.add(sb.toString());
        }            
        
        return returnValue;
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("BSTNativeQueryService with args {}", Arrays.toString(args)); 
    }

}
