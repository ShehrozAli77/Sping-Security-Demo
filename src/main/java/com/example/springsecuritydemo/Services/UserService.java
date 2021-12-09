package com.example.springsecuritydemo.Services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springsecuritydemo.Repo.UserRepository;
import com.example.springsecuritydemo.TokenUtils.AlgorithmUtls;
import com.example.springsecuritydemo.TokenUtils.TokenUtils;
import com.example.springsecuritydemo.domain.Role;
import com.example.springsecuritydemo.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service @RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()){
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (Role role : user.get().getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            }
            return new org.springframework.security.core.userdetails.User(user.get().getEmail(),user.get().getPassword(),authorities);
        }
        else
            throw new UsernameNotFoundException("User with email do not exist");
    }
    @Autowired
    UserRepository userRepository;

    private String generateToken(){
        return "";
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public List<User> getUserList(){
        return userRepository.findAll();
    }


    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        try {
            if (nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                DecodedJWT decodedJWT = TokenUtils.verifyToken(refreshToken, AlgorithmUtls.getAlgorithm());
                User user = userRepository.findByEmail(decodedJWT.getSubject()).get();
                String accessToken = TokenUtils.generateJWTToken(user, new Date(System.currentTimeMillis() + 1 * 60 * 1000));
                TokenUtils.writeJwtTokensToResponseBody(accessToken, refreshToken, response);
            }
            else throw new RuntimeException("User Not Found");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
