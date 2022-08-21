package nl.abnamro.recipes.repository;

import nl.abnamro.recipes.domain.Ingredient;
import nl.abnamro.recipes.domain.Recipe;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends CrudRepository<Ingredient, Integer> {

    Optional<Ingredient> findByTitle(String title);
}
