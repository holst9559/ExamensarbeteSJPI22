package com.example.examensarbete.service;

import com.example.examensarbete.entities.Dish;
import com.example.examensarbete.repository.DishRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {
    private static final Logger logger = LoggerFactory.getLogger(DishService.class);
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<Dish> getAllDishes() {
        logger.info("Fetching all dishes from the database");
        List<Dish> dishes = dishRepository.findAll();
        logger.info("Fetched {} dishes", dishes.size());
        return dishes;
    }
}
