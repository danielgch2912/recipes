package nl.abnamro.recipes.repository;

import nl.abnamro.recipes.domain.Recipe;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecipeRepository extends CrudRepository<Recipe, Integer> , CustomRecipeRepository{
    Optional<Recipe> findByTitle(String title);
}
