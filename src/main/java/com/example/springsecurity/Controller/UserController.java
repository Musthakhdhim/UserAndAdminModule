package com.example.springsecurity.Controller;

import com.example.springsecurity.Mapper.UserMapper;
import com.example.springsecurity.Model.Role;
import com.example.springsecurity.Model.User;
import com.example.springsecurity.Repository.UserRepository;
import com.example.springsecurity.dtos.RegisterRequestDto;
import com.example.springsecurity.dtos.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto registerUser){
        if(userRepository.existsByEmail(registerUser.getEmail())){
            return ResponseEntity.badRequest().body(
                    Map.of("email","email already present")
            );
        }

        User user=userMapper.toUser(registerUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

}
