package com.example.springsecuritydemo.Repo;

import com.example.springsecuritydemo.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
