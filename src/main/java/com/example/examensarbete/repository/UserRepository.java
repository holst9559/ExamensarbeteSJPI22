package com.example.examensarbete.repository;

import com.example.examensarbete.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
