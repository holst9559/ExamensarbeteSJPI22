package com.example.examensarbete.controller;

import com.example.examensarbete.entities.Dish;
import com.example.examensarbete.service.DishService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("api/v1/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public List<Dish> getAllDishes(){
        return dishService.getAllDishes();
    }
}
