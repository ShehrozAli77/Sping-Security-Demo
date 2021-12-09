package com.example.springsecuritydemo.TokenUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springsecuritydemo.domain.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class TokenUtils {
    public static DecodedJWT verifyToken (String token, Algorithm algorithm){
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        return jwtVerifier.verify(token);
    }

    public static UsernamePasswordAuthenticationToken getUserAuthenticationToken(DecodedJWT decodedJWTtoken){
        String username = decodedJWTtoken.getSubject();
        String [] roles = decodedJWTtoken.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(role->{
            authorities.add(new SimpleGrantedAuthority(role));
        });
        return new UsernamePasswordAuthenticationToken(username,null,authorities);
    }

    public static String generateJWTToken(Authentication authResult, Date expiryDate){
        User user = (User)authResult.getPrincipal();
        Algorithm algorithm = AlgorithmUtls.getAlgorithm();
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expiryDate)
                .withClaim("roles",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
              //  .withIssuer(request.getRequestURI())
                .sign(algorithm);
    }

    public static String generateJWTToken(com.example.springsecuritydemo.domain.User user, Date expiryDate){
        Algorithm algorithm = AlgorithmUtls.getAlgorithm();
        return JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(expiryDate)
                .withClaim("roles",user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                //  .withIssuer(request.getRequestURI())
                .sign(algorithm);
    }

    public static void writeJwtTokensToResponseBody(String accessToken, String refreshToken, HttpServletResponse response) throws IOException {

        HashMap<String,String> tokens = new HashMap<>();
        if (nonNull(accessToken))
            tokens.put("access_Token", accessToken);
        if (nonNull(refreshToken))
            tokens.put("refresh_token",refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),tokens);
    }

}