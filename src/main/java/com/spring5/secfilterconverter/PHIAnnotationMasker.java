/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

//import java.text.MessageFormat.Field;
//import org.hibernate.validator.internal.properties.Field;
import java.lang.reflect.Field;
import org.springframework.stereotype.Component;
//Reflection-based Masking:
@Component
public class PHIAnnotationMasker {

    public Object maskPHIFields(Object object) {
        if (object == null) {
            return null;
        }

        try {
            Object maskedObject = object.getClass().newInstance();
            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(object);

                if (field.isAnnotationPresent(PHI.class)) {
                    PHI phiAnnotation = field.getAnnotation(PHI.class);
                    field.set(maskedObject, phiAnnotation.maskWith());
                } else {
                    field.set(maskedObject, value);
                }
            }
            return maskedObject;
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException e) {
            return object; // Return original if masking fails
        }
    }
}
