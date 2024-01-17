package com.example.examensarbete.repository;

import com.example.examensarbete.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);
    Optional<User> findByEmail(String email);
}
