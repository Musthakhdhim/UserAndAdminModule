package com.example.springsecurity.Service;

import com.example.springsecurity.Config.JwtConfig;
import com.example.springsecurity.Model.Role;
import com.example.springsecurity.Model.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@AllArgsConstructor

public class JwtService {
    private final JwtConfig jwtConfig;

    //if we want ot make userid as the subject, and also add email and name to the token
    public String generateToken(User user){
//        final long expiration=300;//5 minutes
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("name",user.getName())
                .claim("email",user.getEmail())
                .claim("role",user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*jwtConfig.getAccessTokenExpiration()))
                .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .compact();
    }

    public String generateRefreshToken(User user){
//        final long expiration=604800; //7days
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email",user.getEmail())
                .claim("name",user.getName())
                .claim("role",user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*jwtConfig.getRefreshTokenExpiration()))
                .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .compact();
    }


//    public String generateToken(String email){
//        final long expiration=84000;
//        return Jwts.builder()
//                .subject(email)
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis()+1000*expiration))
//                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
//                .compact();
//    }

    public boolean validateToken(String token){
        try{
            var claims=Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().after(new Date());
        }
        catch(JwtException jwt){
            return false;
        }
    }
//userid-->subject
    public Long getIdFromToken(String token){
        var claims=Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

    public Role getRoleFromToken(String token){
        var claims=Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Role.valueOf(claims.get("role", String.class));
    }

//    public String getEmailFromToken(String token){
//        var claims=Jwts.parser()
//                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//        return claims.getSubject();
//    }
}
