/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.mapper;

import com.spring5.dto.UserDTO;
import com.spring5.entity.User;
import java.time.LocalDate;
import java.time.Period;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface UserMapperCustom {

    UserDTO toDto(User user);

    default Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // After mapping customization
    @AfterMapping
    default void calculateAge(User user, @MappingTarget UserDTO userDTO) {
        userDTO.setAge(calculateAge(user.getBirthDate()));
    }
}
