package com.example.springsecuritydemo.Controller;

import com.example.springsecuritydemo.Services.UserService;
import com.example.springsecuritydemo.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public ResponseEntity saveUser(@RequestBody User user){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        userService.saveUser(user);
        return ResponseEntity.created(uri).body(null);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUserList(){
        return ResponseEntity.ok(userService.getUserList());
    }

    @GetMapping("refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response){
        userService.refreshToken(request,response);
        return ResponseEntity.ok(null);
    }

}
