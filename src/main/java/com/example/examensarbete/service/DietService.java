package com.example.examensarbete.service;

import com.example.examensarbete.entities.Diet;
import com.example.examensarbete.repository.DietRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DietService {
    private static final Logger logger = LoggerFactory.getLogger(DietService.class);
    private final DietRepository dietRepository;

    public DietService(DietRepository dietRepository) {
        this.dietRepository = dietRepository;
    }

    public List<Diet> getAllDiets() {
        logger.info("Fetching all diets from the database");
        List<Diet> diets = dietRepository.findAll();
        logger.info("Fetched {} diets", diets.size());
        return diets;
    }
}
