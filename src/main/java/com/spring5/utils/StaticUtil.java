/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

@Slf4j
public class StaticUtil {

    public static ProducerFactory getKafkaProducerFactory() {
        log.info("StaticUtil trying to perform an action...");
        try {
            // Ensure ApplicationContextHolder is initialized
            if (ApplicationContextHolder.isInitialized()) {
                ProducerFactory producerFactory = ApplicationContextHolder.getBean(ProducerFactory.class);
                log.info("producerFactory {}", producerFactory);
                return producerFactory;
            } else {
                System.err.println(
                        "ApplicationContextHolder not initialized. MyService cannot be accessed.");
                // Handle this case appropriately - perhaps by throwing an exception
                // or having a fallback mechanism if running outside Spring.
            }
        } catch (BeansException e) {
            System.err.println("Error retrieving MyService: " + e.getMessage());
            // Handle bean retrieval errors
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Component // Make this a Spring-managed bean
    public class ApplicationContextHolder implements ApplicationContextAware {

        private static ApplicationContext context;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            // Store the ApplicationContext statically.
            // This method is called by Spring during initialization.
            ApplicationContextHolder.context = applicationContext;
        }

        /**
         * Retrieves a bean from the Spring application context.
         *
         * @param beanName the name of the bean to retrieve
         * @return the bean instance
         * @throws BeansException if the bean cannot be found or created
         */
        public static Object getBean(String beanName) throws BeansException {
            if (context == null) {
                throw new IllegalStateException(
                        "ApplicationContext not initialized. Ensure ApplicationContextHolder is scanned by Spring.");
            }
            return context.getBean(beanName);
        }

        /**
         * Retrieves a bean from the Spring application context by its type.
         *
         * @param beanClass the class of the bean to retrieve
         * @param <T> the type of the bean
         * @return the bean instance
         * @throws BeansException if the bean cannot be found, created, or if
         * multiple beans of the type exist
         */
        public static <T> T getBean(Class<T> beanClass) throws BeansException {
            if (context == null) {
                throw new IllegalStateException(
                        "ApplicationContext not initialized. Ensure ApplicationContextHolder is scanned by Spring.");
            }
            return context.getBean(beanClass);
        }

        /**
         * Checks if the ApplicationContext has been initialized.
         *
         * @return true if initialized, false otherwise.
         */
        public static boolean isInitialized() {
            return context != null;
        }
    }
}
