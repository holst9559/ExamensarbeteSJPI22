package com.example.examensarbete.repository;

import com.example.examensarbete.entities.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DishRepository extends JpaRepository<Dish, Integer> {
    Optional<String> findByName(String name);
}
