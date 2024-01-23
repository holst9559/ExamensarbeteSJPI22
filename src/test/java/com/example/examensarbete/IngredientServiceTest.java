package com.example.examensarbete;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.examensarbete.dto.IngredientDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.exception.IngredientAlreadyExistException;
import com.example.examensarbete.exception.IngredientNotFoundException;
import com.example.examensarbete.repository.IngredientRepository;
import com.example.examensarbete.service.IngredientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {


    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    void getAllIngredients() {
        // Mocking Repository Behavior
        when(ingredientRepository.findAll()).thenReturn(List.of(
                createIngredient(1L, "Flour"),
                createIngredient(2L, "Sugar")
        ));

        // Method Invocation and Assertion
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        assertEquals(2, ingredients.size());
    }

    @Test
    void getIngredientById_IngredientFound() {
        // Mocking Repository Behavior
        Long ingredientId = 1L;
        Ingredient ingredient = createIngredient(ingredientId, "Flour");
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));

        // Method Invocation and Assertion
        Ingredient result = ingredientService.getIngredientById(ingredientId);
        assertEquals(ingredient, result);
    }

    @Test
    void getIngredientById_IngredientNotFound() {
        // Mocking Repository Behavior
        Long ingredientId = 1L;
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertThrows(RuntimeException.class, () -> ingredientService.getIngredientById(ingredientId));
    }

    @Test
    void getIngredientByName_IngredientFound() {
        // Mocking Repository Behavior
        String ingredientName = "Flour";
        Ingredient ingredient = createIngredient(1L, ingredientName);
        when(ingredientRepository.findByName(ingredientName)).thenReturn(Optional.of(ingredient));

        // Method Invocation and Assertion
        Ingredient result = ingredientService.getIngredientByName(ingredientName);
        assertEquals(ingredient, result);
    }

    @Test
    void getIngredientByName_IngredientNotFound() {
        // Mocking Repository Behavior
        String ingredientName = "Flour";
        when(ingredientRepository.findByName(ingredientName)).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertThrows(RuntimeException.class, () -> ingredientService.getIngredientByName(ingredientName));
    }

    @Test
    void addIngredient_IngredientDoesNotExist() {
        // Mocking Repository Behavior
        IngredientDto ingredientDto = createIngredientDto("Flour");
        when(ingredientRepository.findByName(ingredientDto.name())).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertDoesNotThrow(() -> ingredientService.addIngredient(ingredientDto));
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void addIngredient_IngredientAlreadyExists() {
        // Mocking Repository Behavior
        IngredientDto ingredientDto = createIngredientDto("Flour");
        when(ingredientRepository.findByName(ingredientDto.name())).thenReturn(Optional.of(createIngredient(1L, "Flour")));

        // Method Invocation and Assertion
        assertThrows(IngredientAlreadyExistException.class, () -> ingredientService.addIngredient(ingredientDto));
        verify(ingredientRepository, never()).save(any(Ingredient.class));
    }

    @Test
    void editIngredient_IngredientExists() {
        // Mocking Repository Behavior
        Long ingredientId = 1L;
        IngredientDto ingredientDto = createIngredientDto("Sugar");
        Ingredient existingIngredient = createIngredient(ingredientId, "Flour");
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(existingIngredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the editIngredient method
        Ingredient updatedIngredient = ingredientService.editIngredient(ingredientId, ingredientDto);

        // Verify that the repository save method is called once with the correct argument
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));

        // Assert that the returned ingredient is the expected one
        assertDoesNotThrow(() -> ingredientService.editIngredient(ingredientId, ingredientDto));
        assertEquals(updatedIngredient.getId(), ingredientDto.id());
        assertEquals(updatedIngredient.getName(), ingredientDto.name());
    }

    @Test
    void editIngredient_IngredientDoesNotExist() {
        // Mocking Repository Behavior
        Long ingredientId = 1L;
        IngredientDto ingredientDto = createIngredientDto("Sugar");
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertThrows(RuntimeException.class, () -> ingredientService.editIngredient(ingredientId, ingredientDto));
        verify(ingredientRepository, never()).save(any(Ingredient.class));
    }

    @Test
    void deleteIngredient_IngredientExists() {
        // Mocking Repository Behavior
        Long ingredientId = 1L;
        Ingredient existingIngredient = createIngredient(ingredientId, "Flour");
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(existingIngredient));

        // Method Invocation
        assertDoesNotThrow(() -> ingredientService.deleteIngredient(ingredientId));
        verify(ingredientRepository, times(1)).delete(existingIngredient);
    }

    @Test
    void deleteIngredient_IngredientDoesNotExist() {
        // Mocking Repository Behavior
        Long ingredientId = 1L;
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertThrows(RuntimeException.class, () -> ingredientService.deleteIngredient(ingredientId));
        verify(ingredientRepository, never()).deleteById(anyLong());
    }

    // Helper methods
    private Ingredient createIngredient(Long id, String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        return ingredient;
    }

    private IngredientDto createIngredientDto(String name) {
        return new IngredientDto(
                1L,
                name
        );

    }
}
