package com.example.examensarbete.repository;

import com.example.examensarbete.entities.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructionRepository extends JpaRepository<Instruction, Long> {
}