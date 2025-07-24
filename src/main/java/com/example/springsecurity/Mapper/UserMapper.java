package com.example.springsecurity.Mapper;

import com.example.springsecurity.Model.User;
import com.example.springsecurity.dtos.RegisterRequestDto;
import com.example.springsecurity.dtos.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user){
        UserDto dto=new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        return dto;
    }

    public User toUser(RegisterRequestDto dto){
        User user=new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }




}
