package nl.abnamro.recipes.repository;

import nl.abnamro.recipes.domain.Recipe;
import java.util.List;

public interface CustomRecipeRepository {
    List<Recipe> find(String text, String include, String exclude, Integer servings, Boolean vegetarian);
}
