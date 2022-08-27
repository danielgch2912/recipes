package nl.abnamro.recipes.service;

import nl.abnamro.recipes.dto.ErrorDto;
import nl.abnamro.recipes.dto.RecipeDto;
import nl.abnamro.recipes.repository.IngredientRepository;
import nl.abnamro.recipes.repository.RecipeRepository;
import nl.abnamro.recipes.service.exception.AlreadyExistException;
import nl.abnamro.recipes.service.exception.NotFoundException;
import nl.abnamro.recipes.utils.Errors;
import nl.abnamro.recipes.utils.SampleData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RecipeServiceTest {

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private IngredientRepository ingredientRepository;

	@Autowired
	private RecipeService recipeService;

	private List<RecipeDto> okRecipeList;


	@BeforeEach
	public void cleanAndPrepareTests() {
		recipeRepository.deleteAll();
		ingredientRepository.deleteAll();

		okRecipeList = SampleData.create();
	}

	@Test
	void whenSaveRecipe_dataShouldBeOk() {
		var recipe = okRecipeList.get(0);
		recipeService.save(recipe);

		var savedRecipe = recipeRepository.findByTitle(recipe.getTitle());
		assertThat(savedRecipe.isPresent()).isTrue();

		var fromDB = savedRecipe.get();
		assertThat(fromDB.getServes()).isEqualTo(recipe.getServes());
		assertThat(fromDB.getVegetarian()).isEqualTo(recipe.getVegetarian());
		assertThat(fromDB.getServes()).isEqualTo(recipe.getServes());

		var ingredientsFromDB = fromDB.getIngredients();

		for(var ingredientFromDB : ingredientsFromDB) {
			assertThat(ingredientFromDB).isNotNull();

			assertThat(recipe.ingredients.contains(ingredientFromDB.getTitle())).isTrue();
		}

	}

	@Test
	void whenSaveDuplicateRecipe_throwAlreadyExistException() {
		var recipe = okRecipeList.get(0);
		recipeService.save(recipe);

		var exception = assertThrows(AlreadyExistException.class, () -> recipeService.save(recipe));

		List<ErrorDto> errors = exception.getErrors();

		assertThat(errors.size()).isEqualTo(1);
		assertThat(errors.get(0).getField()).isEqualTo("title");
		assertThat(errors.get(0).getMessage()).isEqualTo(Errors.RECIPE_SAME_TITLE);
	}

	@Test
	void whenUpdateRecipeTitle_dataShouldBeOk() {
		// Save a recipe
		var recipe = okRecipeList.get(0);
		recipeService.save(recipe);

		var savedRecipe = recipeRepository.findByTitle(recipe.getTitle());
		assertThat(savedRecipe.isPresent()).isTrue();

		// Update recipe
		var title = "This is a new title";
		recipe.setRecipeId(savedRecipe.get().getRecipeId());
		recipe.setTitle(title);
		recipeService.saveOrUpdate(recipe);

		var updatedRecipe = recipeRepository.findByTitle(recipe.getTitle());
		assertThat(updatedRecipe.isPresent()).isTrue();
		assertThat(updatedRecipe.get().getRecipeId()).isNotNull();
		assertThat(updatedRecipe.get().getTitle()).isEqualTo(title);
	}

	@Test
	void whenUpdateRecipe_throwNotFoundException() {
		var recipe = okRecipeList.get(0);

		// Set an invalid ID
		recipe.setRecipeId(9999);

		var exception = assertThrows(NotFoundException.class, () -> recipeService.saveOrUpdate(recipe));
		var errors = exception.getErrors();

		assertThat(errors.size()).isEqualTo(1);
		assertThat(errors.get(0).getField()).isEqualTo("recipeId");
		assertThat(errors.get(0).getMessage()).isEqualTo(Errors.RECIPE_NOT_FOUND);
	}

	@Test
	void whenDeleteRecipes_dataShouldNotBePresent() {
		var recipe = okRecipeList.get(0);
		recipeService.save(recipe);

		var savedRecipe = recipeRepository.findByTitle(recipe.getTitle());
		assertThat(savedRecipe.isPresent()).isTrue();

		var recipeId = savedRecipe.get().getRecipeId();
		recipeService.delete(recipeId);

		var recipeFromDB = recipeRepository.findById(recipeId);
		assertThat(recipeFromDB).isNotPresent();
	}

	@Test
	void whenDeleteNonExistentRecipe_shouldThrowNotFoundException() {
		// recipeId that does not exist
		int recipeId = 9999;

		var exception = assertThrows(NotFoundException.class, () -> recipeService.delete(recipeId));
		var errors = exception.getErrors();

		assertThat(errors.size()).isEqualTo(1);
		assertThat(errors.get(0).getField()).isEqualTo("recipeId");
		assertThat(errors.get(0).getMessage()).isEqualTo(Errors.RECIPE_NOT_FOUND);
	}

	@Test
	void whenFindByInstructions_shoulContainText() {
		insertRecipesForFindTests();

		var recipes = recipeService.find("vegetables", "", "", null, null);

		assertThat(recipes.size()).isEqualTo(2);
		assertThat(recipes.get(0).getInstructions()).contains("vegetables");
		assertThat(recipes.get(0).getInstructions()).contains("vegetables");
	}

	@Test
	void whenFindByServings_shouldBeEqual() {
		insertRecipesForFindTests();

		var recipes = recipeService.find("", "", "", 2, null);
		assertThat(recipes.size()).isEqualTo(2);
	}

	@Test
	void whenFindByVegetarianRecipe_shouldReturnExpectedNumber() {
		insertRecipesForFindTests();

		var recipes = recipeService.find("", "", "", null, true);
		assertThat(recipes.size()).isEqualTo(4);
	}

	@Test
	void whenFindByInclude_shouldReturnExpectedRecipes() {
		insertRecipesForFindTests();

		var recipes = recipeService.find("", "carrot", "", null, null);
		assertThat(recipes.size()).isEqualTo(2);

		var ingredientCount1 = recipes.get(0).getIngredients()
				.stream()
				.filter(x -> x.getTitle().equals("Carrot"))
				.count();

		var ingredientCount2 = recipes.get(1).getIngredients()
				.stream()
				.filter(x -> x.getTitle().equals("Carrot"))
				.count();

		assertThat(ingredientCount1).isEqualTo(1);
		assertThat(ingredientCount2).isEqualTo(1);
	}

	@Test
	void whenFindByExclude_shouldReturnExpectedRecipes() {
		insertRecipesForFindTests();

		var recipes = recipeService.find("", "", "carrot", null, null);
		assertThat(recipes.size()).isEqualTo(3);

		var ingredientCount1 = recipes.get(0).getIngredients()
				.stream()
				.filter(x -> x.getTitle().equals("Carrot"))
				.count();

		var ingredientCount2 = recipes.get(1).getIngredients()
				.stream()
				.filter(x -> x.getTitle().equals("Carrot"))
				.count();

		var ingredientCount3= recipes.get(2).getIngredients()
				.stream()
				.filter(x -> x.getTitle().equals("Carrot"))
				.count();

		assertThat(ingredientCount1).isEqualTo(0);
		assertThat(ingredientCount2).isEqualTo(0);
		assertThat(ingredientCount3).isEqualTo(0);

	}

	@Test
	void whenFindByIncludeAndExclude_shouldReturnExpectedRecipes() {
		insertRecipesForFindTests();

		var recipes = recipeService.find("", "basil", "carrot", null, null);
		assertThat(recipes.size()).isEqualTo(1);

		var ingredientCountBasil= recipes.get(0).getIngredients()
				.stream()
				.filter(x -> x.getTitle().equals("Basil"))
				.count();

		var ingredientCountCarrot= recipes.get(0).getIngredients()
				.stream()
				.filter(x -> x.getTitle().equals("Carrot"))
				.count();

		assertThat(ingredientCountBasil).isEqualTo(1);
		assertThat(ingredientCountCarrot).isEqualTo(0);
	}

	private void insertRecipesForFindTests() {
		for (var recipe : okRecipeList) {
			recipeService.save(recipe);

			var savedRecipe = recipeRepository.findByTitle(recipe.getTitle());
			assertThat(savedRecipe.isPresent()).isTrue();
		}
	}
}
