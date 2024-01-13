package com.example.examensarbete.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "RECIPE")
public class Recipe implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id")
    private Dish dish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotEmpty
    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "prep_time")
    private Integer prepTime;

    @NotNull
    @Column(name = "cook_time")
    private Integer cookTime;

    @NotNull
    @Column(name = "servings")
    private Integer servings;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "recipe")
    private Set<Instruction> instructions = new HashSet();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "recipe")
    private Set<RecipeIngredient> recipeIngredients = new HashSet();

    @Column(name = "img_url")
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id")
    private Diet diet;

    public Recipe(User user, Dish dish, Category category, String description, Integer prepTime, Integer cookTime, Integer servings, Set<Instruction> instructions, Set<RecipeIngredient> recipeIngredients, String imgUrl, Diet diet) {
        this.user = user;
        this.dish = dish;
        this.category = category;
        this.description = description;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.servings = servings;
        this.instructions = instructions;
        this.recipeIngredients = recipeIngredients;
        this.imgUrl = imgUrl;
        this.diet = diet;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Recipe recipe = (Recipe) o;
        return getId() != null && Objects.equals(getId(), recipe.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    private int calculateTotalTime(int prepTime, int cookTime){
        return prepTime + cookTime;
    }

    private void addIngredient(RecipeIngredient recipeIngredient){
        recipeIngredients.add(recipeIngredient);
    }

    private void addInstruction(Instruction instruction){
        instructions.add(instruction);
    }
}
