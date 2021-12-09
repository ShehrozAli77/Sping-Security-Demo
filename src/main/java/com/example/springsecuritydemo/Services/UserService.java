package com.example.springsecuritydemo.Services;

import com.example.springsecuritydemo.Repo.UserRepository;
import com.example.springsecuritydemo.domain.Role;
import com.example.springsecuritydemo.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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


}
