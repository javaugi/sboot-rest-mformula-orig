/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//Logging Aspect with Masking:
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Autowired
    private PHIMaskingUtil phiMaskingUtil;

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.GetMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // Mask input parameters
        Object[] args = joinPoint.getArgs();
        String maskedArgs = phiMaskingUtil.maskString(Arrays.toString(args));

        logger.info("Entering {}.{} with arguments: {}", className, methodName, maskedArgs);

        try {
            Object result = joinPoint.proceed();

            // Mask response if it contains PHI
            String maskedResult = phiMaskingUtil.maskObject(result);
            logger.info("Exiting {}.{} with result: {}", className, methodName, maskedResult);

            return result;
        } catch (Exception e) {
            logger.error("Exception in {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
