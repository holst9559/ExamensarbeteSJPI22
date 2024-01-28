package com.example.examensarbete.controller;

import com.example.examensarbete.entities.Diet;
import com.example.examensarbete.service.DietService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/diets")
public class DietController {
    private final DietService dietService;

    public DietController(DietService dietService) {
        this.dietService = dietService;
    }

    @GetMapping
    public List<Diet> getAllDiets(){
        return dietService.getAllDiets();
    }
}
