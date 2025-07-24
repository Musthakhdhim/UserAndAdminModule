package com.example.springsecurity.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDao {

    private String email;
    private String password;
}
