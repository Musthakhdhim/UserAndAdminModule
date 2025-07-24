package com.example.springsecurity.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private long id;

    @NotBlank(message = "can't be blank")
    private String name;
    @Email(message = "should be in email format")
    @NotBlank(message="can't be blank")
    private String email;
}
