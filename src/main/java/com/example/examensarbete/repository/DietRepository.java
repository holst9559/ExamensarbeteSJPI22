package com.example.examensarbete.repository;

import com.example.examensarbete.entities.Diet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DietRepository extends JpaRepository<Diet, Integer> {
    Optional<Diet> findByName(String name);
}
