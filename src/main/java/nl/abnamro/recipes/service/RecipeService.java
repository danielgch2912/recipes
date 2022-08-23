package nl.abnamro.recipes.service;

import nl.abnamro.recipes.domain.Recipe;
import nl.abnamro.recipes.utils.Errors;
import nl.abnamro.recipes.domain.Ingredient;
import nl.abnamro.recipes.dto.ErrorDto;
import nl.abnamro.recipes.dto.RecipeDto;
import nl.abnamro.recipes.repository.IngredientRepository;
import nl.abnamro.recipes.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    public RecipeDto save(RecipeDto recipeDto) throws ServiceErrorException {
        if (recipeDto.getRecipeId() != null) {
            throw new ServiceErrorException(new ErrorDto("recipeId", Errors.RECIPE_ID_SHOULD_BE_EMPTY));
        }

        var duplicateRecipe = recipeRepository.findByTitle(recipeDto.getTitle());
        if (duplicateRecipe.isPresent()) {
            throw new ServiceErrorException(new ErrorDto("title", Errors.RECIPE_SAME_TITLE));
        }

        return saveRegister(recipeDto);
    }

    public RecipeDto saveOrUpdate(RecipeDto recipeDto) throws ServiceErrorException {
        // If id is empty, just save a new register
        if (recipeDto.getRecipeId() == null) {
            return save(recipeDto);
        }

        var recipeFromDB = recipeRepository.findById(recipeDto.getRecipeId());
        if (recipeFromDB.isEmpty()) {
            throw new ServiceErrorException(new ErrorDto("recipeId", Errors.RECIPE_NOT_FOUND));
        }

        return saveRegister(recipeDto);
    }

    public void delete(Integer id) throws ServiceErrorException {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isEmpty()) {
            throw new ServiceErrorException(new ErrorDto("recipeId", Errors.RECIPE_NOT_FOUND));
        }

        recipeRepository.delete(recipe.get());
    }

    private RecipeDto saveRegister(RecipeDto recipeDto) {
        var ingredients = new ArrayList<Ingredient>();
        for (var ingredientStr : recipeDto.getIngredients()) {
            var ingredient = ingredientRepository.findByTitle(ingredientStr);

            if (!ingredient.isEmpty()) {
                ingredients.add(ingredient.get());
            } else {
                var newIngredient = Ingredient.builder()
                        .title(ingredientStr)
                        .build();

                ingredientRepository.save(newIngredient);

                ingredients.add(newIngredient);
            }
        }

        var recipe = RecipeDto.toEntity(recipeDto);
        recipe.setIngredients(ingredients);

        recipe = recipeRepository.save(recipe);
        return RecipeDto.toDTO(recipe);
    }

}
