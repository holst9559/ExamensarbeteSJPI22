package com.example.examensarbete.repository;

import com.example.examensarbete.entities.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {
    Optional<Unit> findByName(String name);
}