package com.example.examensarbete.dto;

import jakarta.validation.constraints.NotEmpty;

public record InstructionDto(
        String step,
        String description) {
}
