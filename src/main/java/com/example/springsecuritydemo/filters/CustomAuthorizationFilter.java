package com.example.springsecuritydemo.filters;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springsecuritydemo.TokenUtils.AlgorithmUtls;
import com.example.springsecuritydemo.TokenUtils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static org.springframework.http.HttpStatus.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            if (request.getServletPath().equals("/api/login")){
                filterChain.doFilter(request,response);
            }
            else
            {
                String authorizationHeader = request.getHeader(AUTHORIZATION);
                if (nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")){
                    try {
                        String token = authorizationHeader.substring("Bearer ".length());
                        DecodedJWT decodedJWT = TokenUtils.verifyToken(token, AlgorithmUtls.getAlgorithm());
                        UsernamePasswordAuthenticationToken authenticationToken = TokenUtils.getUserAuthenticationToken(decodedJWT);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        filterChain.doFilter(request,response);
                    }
                    catch (Exception e){
                        log.error("Error in {}",e.getMessage());
                        response.setStatus(FORBIDDEN.value());
                        HashMap<String,String> errors = new HashMap<>();
                        errors.put("error", e.getMessage());
                        response.setContentType(APPLICATION_JSON_VALUE);
                        new ObjectMapper().writeValue(response.getOutputStream(),errors);
                    }

                }
                else {
                    filterChain.doFilter(request,response);
                }
            }
    }
}
