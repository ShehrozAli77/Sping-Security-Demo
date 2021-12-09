package com.example.springsecuritydemo.filters;

import com.example.springsecuritydemo.TokenUtils.TokenUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
            String uname = request.getParameter("username");
            String password = request.getParameter("password");
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(uname,password);
            Authentication auth = authenticationManager.authenticate(token);
            return auth;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String accessToken = TokenUtils.generateJWTToken(authResult,new Date(System.currentTimeMillis()+1*60*1000));
        String refreshToken = TokenUtils.generateJWTToken(authResult,new Date(System.currentTimeMillis()+30*60*1000));
        TokenUtils.writeJwtTokensToResponseBody(accessToken,refreshToken,response);
    }


}
