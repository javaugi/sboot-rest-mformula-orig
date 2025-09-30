/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.mapper;

import com.spring5.dto.UserDTO;
import com.spring5.entity.User;
import org.modelmapper.ModelMapper;

/**
 * @author javau
 */
public class UserModelMapper {

    ModelMapper modelMapper = new ModelMapper();

    User user = new User();
    UserDTO dto = modelMapper.map(user, UserDTO.class);
    User entity = modelMapper.map(dto, User.class);
}
