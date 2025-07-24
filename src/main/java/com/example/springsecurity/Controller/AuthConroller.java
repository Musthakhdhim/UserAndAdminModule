package com.example.springsecurity.Controller;

import com.example.springsecurity.Config.JwtConfig;
import com.example.springsecurity.Exception.UserNotFound;
import com.example.springsecurity.Mapper.UserMapper;
import com.example.springsecurity.Model.User;
import com.example.springsecurity.Repository.UserRepository;
import com.example.springsecurity.Service.JwtService;
import com.example.springsecurity.dtos.JwtResponse;
import com.example.springsecurity.dtos.LoginRequestDao;
import com.example.springsecurity.dtos.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthConroller {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequestDao loginUser,
                            HttpServletResponse response
                                             ){
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getEmail(),
                        loginUser.getPassword()
                )
        );


        User user=userRepository.findByEmail(loginUser.getEmail()).orElseThrow();
//        String token=jwtService.generateToken(loginUser.getEmail());
        String accessToken=jwtService.generateToken(user);
        String refreshToken= jwtService.generateRefreshToken(user);

        var cookie=new Cookie("refreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/auth");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); // 7 days
        cookie.setSecure(true);

        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear the refresh token cookie
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth");
        cookie.setMaxAge(0); // Delete cookie immediately

        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }


    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value="refreshToken") String refreshToken
    ){
        if(!jwtService.validateToken(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var userId=jwtService.getIdFromToken(refreshToken);
        var user=userRepository.findById(userId).orElseThrow();
        var accessToken=jwtService.generateToken(user);
        System.out.println(accessToken);

        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

//    @PostMapping("/validate")
//    public boolean validateToken(@RequestHeader("authorization") String authHeader){
//        var token=authHeader.replace("Bearer ","");
//        return jwtService.validateToken(token);
//    }

//userid-->subject
    @GetMapping("/currentuser")
    public ResponseEntity<UserDto> getCurrentUser(){
        var authentication= SecurityContextHolder.getContext().getAuthentication();
        Long userId=(long)authentication.getPrincipal();

        User user=userRepository.findById(userId).orElseThrow();

        return ResponseEntity.ok(userMapper.toDto(user));
    }

//    @GetMapping("/currentuser")
//    public ResponseEntity<UserDto> getCurrentUser(){
//        var authentication= SecurityContextHolder.getContext().getAuthentication();
//        String email=(String)authentication.getPrincipal();
//
//        User user=userRepository.findByEmail(email).orElseThrow(()->{
//            throw new UserNotFound("user with the email is not found");
//        });
//
//        return ResponseEntity.ok(userMapper.toDto(user));
//    }

}




//User user=userRepository.findByEmail(loginUser.getEmail());
//        if(user==null){
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//                if(!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())){
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }