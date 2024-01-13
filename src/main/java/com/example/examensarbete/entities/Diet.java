package com.example.examensarbete.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "DIET")
public class Diet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id")
    private Integer id;

    @NotEmpty
    @Column(name = "diet_name")
    private String name;
}
