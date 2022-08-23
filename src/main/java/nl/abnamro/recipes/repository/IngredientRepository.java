package nl.abnamro.recipes.repository;

import nl.abnamro.recipes.domain.Ingredient;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface IngredientRepository extends CrudRepository<Ingredient, Integer> {

    Optional<Ingredient> findByTitle(String title);
}
