package nl.abnamro.recipes.repository;

import nl.abnamro.recipes.domain.Recipe;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface CustomRecipeRepository {
    List<Recipe> find(String text, String include, String exclude, Integer servings, Boolean vegetarian);
}
