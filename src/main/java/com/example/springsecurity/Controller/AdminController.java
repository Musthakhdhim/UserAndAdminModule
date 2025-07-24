package com.example.springsecurity.Controller;

import com.example.springsecurity.Exception.UserNotFound;
import com.example.springsecurity.Mapper.UserMapper;
import com.example.springsecurity.Model.Role;
import com.example.springsecurity.Model.User;
import com.example.springsecurity.Repository.UserRepository;
import com.example.springsecurity.dtos.UpdateUserDto;
import com.example.springsecurity.dtos.UserDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mapstruct.control.MappingControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/hello")
    public String hello(){
        return "hello world";
    }

    @GetMapping("/users")
    public List<User> getALlUsers(){
        List<User> users=userRepository.findAllByRole(Role.USER);
        return users;
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserFromAdmin(@PathVariable long id, @Valid @RequestBody UpdateUserDto updateUserDto){
        User user=userRepository.findById(id).orElseThrow(
                ()->{
                    throw new UserNotFound("user with the given id not found");
                }
        );
//        User user1=userRepository.findByEmail(updateUserDto.getEmail()).orElseThrow(()->{
//            throw new UserNotFound("user not found");
//        });
//
//        if(user1.getId()!=id){
//            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).build();
//        }

        Optional<User> user1 = userRepository.findByEmail(updateUserDto.getEmail());
        if (user1.isPresent() && user1.get().getId() != id) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).build();
        }


//        if(userRepository.existsByEmail(updateUserDto.getEmail())){
//            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).build();
//        }
        user.setName(updateUserDto.getName());
        user.setEmail(updateUserDto.getEmail());
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUserFromAdmin(@PathVariable long id){
        User user=userRepository.findById(id).orElseThrow(()->{
            throw new UserNotFound("user is not found");
        });

        userRepository.delete(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> filterUserFromAdmin(@RequestParam String keyword) {
        List<User> users = userRepository.searchUsers(keyword);

        if (users == null || users.isEmpty()) {
            return new ResponseEntity<>("No users found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }




}
