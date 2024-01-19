package com.example.examensarbete.repository;

import com.example.examensarbete.entities.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Integer> {
    Optional<Unit> findByName(String name);
}