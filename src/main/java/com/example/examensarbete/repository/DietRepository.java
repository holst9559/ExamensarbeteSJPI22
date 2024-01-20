package com.example.examensarbete.repository;

import com.example.examensarbete.entities.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DietRepository extends JpaRepository<Diet, Integer> {
    Optional<Diet> findByName(String name);
}
