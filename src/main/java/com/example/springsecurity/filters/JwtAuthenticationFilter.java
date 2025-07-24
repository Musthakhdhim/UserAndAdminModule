package com.example.springsecurity.filters;

import com.example.springsecurity.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader=request.getHeader("Authorization");

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        var token=authHeader.replace("Bearer ","");
        if(!jwtService.validateToken(token)){
            filterChain.doFilter(request,response);
            return;
        }
//userid--> subject
        var role=jwtService.getRoleFromToken(token);

        var authentication=new UsernamePasswordAuthenticationToken(
                jwtService.getIdFromToken(token),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_"+role))
        );
//        var authentication=new UsernamePasswordAuthenticationToken(
//                jwtService.getEmailFromToken(token),
//                null,
//                null
//        );
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request,response);
    }
}
