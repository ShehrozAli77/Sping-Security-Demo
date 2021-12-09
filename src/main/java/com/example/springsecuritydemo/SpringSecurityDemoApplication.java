package com.example.springsecuritydemo;

import com.example.springsecuritydemo.Services.UserService;
import com.example.springsecuritydemo.domain.Role;
import com.example.springsecuritydemo.domain.User;
import net.bytebuddy.asm.Advice;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@SpringBootApplication
public class SpringSecurityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(UserService userService){
        return args -> {
            userService.saveUser(User.builder().email("Shehroz").password("123456").roles(Arrays.asList(Role.builder().name("ROLE_USER").build())).build());
            userService.saveUser(User.builder().email("Omer").password("1234567").roles(Arrays.asList(Role.builder().name("ROLE_ADMIN").build())).build());
        };
    }


    @Bean
    PasswordEncoder PasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
