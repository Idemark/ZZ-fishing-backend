package com.example.ZZfishing.api.user.repository;

import com.example.ZZfishing.api.user.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramUserRepository extends JpaRepository<User, Long> {
}
