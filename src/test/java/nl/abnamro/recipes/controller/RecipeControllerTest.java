package nl.abnamro.recipes.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import nl.abnamro.recipes.dto.ErrorDto;
import nl.abnamro.recipes.dto.RecipeDto;
import nl.abnamro.recipes.repository.IngredientRepository;
import nl.abnamro.recipes.repository.RecipeRepository;
import nl.abnamro.recipes.utils.Errors;
import nl.abnamro.recipes.utils.SampleData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeControllerTest {

	@LocalServerPort
	int port;

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private IngredientRepository ingredientRepository;

	private List<RecipeDto> okRecipeList;

	public static String URL_RECIPE = "/v1/recipe";

	public static String URL_RECIPE_FIND = "/v1/recipe/find";

	@BeforeEach
	public void before() {
		RestAssured.baseURI = "http://localhost:" + port + "/api";
	}

	@BeforeEach
	public void cleanAndPrepareTests() {
		recipeRepository.deleteAll();
		ingredientRepository.deleteAll();

		okRecipeList = SampleData.create();
	}

	@Test
	void saveRecipe() {
		var recipe = okRecipeList.get(0);

		given()
				.body(recipe)
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.when()
				.post(URL_RECIPE)
				.then()
				.statusCode(HttpStatus.OK.value());

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
	void saveRecipeDuplicateTitle() {
		var recipe = okRecipeList.get(0);

		given()
				.body(recipe)
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.when()
				.post(URL_RECIPE)
				.then()
				.statusCode(HttpStatus.OK.value());

		var savedRecipe = recipeRepository.findByTitle(recipe.getTitle());
		assertThat(savedRecipe.isPresent()).isTrue();

		ErrorDto[] errors = given()
				.body(recipe)
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.when()
				.post(URL_RECIPE)
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract()
				.as(ErrorDto[].class);

		assertThat(errors.length).isEqualTo(1);
		assertThat(errors[0].getField()).isEqualTo("title");
		assertThat(errors[0].getMessage()).isEqualTo(Errors.RECIPE_SAME_TITLE);
	}

	@Test
	void updateRecipe() {
		// Save a recipe
		var recipe = okRecipeList.get(0);

		given()
				.body(recipe)
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.when()
				.post(URL_RECIPE)
				.then()
				.statusCode(HttpStatus.OK.value());

		var savedRecipe = recipeRepository.findByTitle(recipe.getTitle());
		assertThat(savedRecipe.isPresent()).isTrue();

		// Update recipe
		var title = "This is a new title";
		recipe.setTitle(title);
		given()
				.body(recipe)
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.when()
				.put(URL_RECIPE)
				.then()
				.statusCode(HttpStatus.OK.value());

		var updatedRecipe = recipeRepository.findByTitle(recipe.getTitle());
		assertThat(updatedRecipe.isPresent()).isTrue();
		assertThat(updatedRecipe.get().getRecipeId()).isNotNull();
		assertThat(updatedRecipe.get().getTitle()).isEqualTo(title);
	}

	@Test
	void putRecipe() {
		var recipe = okRecipeList.get(0);

		// User PUT method to insert a new recipe
		given()
				.body(recipe)
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.when()
				.put(URL_RECIPE)
				.then()
				.statusCode(HttpStatus.OK.value());

		var putRecipe = recipeRepository.findByTitle(recipe.getTitle());
		assertThat(putRecipe.isPresent()).isTrue();
		assertThat(putRecipe.get().getRecipeId()).isNotNull();
	}

	@Test
	void updateNonExistentRecipe() {
		var recipe = okRecipeList.get(0);

		// Set an invalid ID
		recipe.setRecipeId(9999);

		// User PUT method to insert a new recipe
		ErrorDto[] errors = given()
				.body(recipe)
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.when()
				.put(URL_RECIPE)
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract()
				.as(ErrorDto[].class);

		assertThat(errors.length).isEqualTo(1);
		assertThat(errors[0].getField()).isEqualTo("recipeId");
		assertThat(errors[0].getMessage()).isEqualTo(Errors.RECIPE_NOT_FOUND);
	}

	@Test
	void deleteRecipes() {
		var recipe = okRecipeList.get(0);

		given()
				.body(recipe)
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.when()
				.post(URL_RECIPE)
				.then()
				.statusCode(HttpStatus.OK.value());

		var savedRecipe = recipeRepository.findByTitle(recipe.getTitle());
		assertThat(savedRecipe.isPresent()).isTrue();

		var recipeId = savedRecipe.get().getRecipeId();

		given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.when()
				.delete(URL_RECIPE + "/" + recipeId)
				.then()
				.statusCode(HttpStatus.OK.value());

		var recipeFromDB = recipeRepository.findById(recipeId);
		assertThat(recipeFromDB).isNotPresent();
	}

	@Test
	void deleteNonExistentRecipe() {
		// recipeId that does not exist
		var recipeId = "9999";

		ErrorDto[] errors = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.when()
				.delete(URL_RECIPE + "/" + recipeId)
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.extract()
				.as(ErrorDto[].class);

		assertThat(errors.length).isEqualTo(1);
		assertThat(errors[0].getField()).isEqualTo("recipeId");
		assertThat(errors[0].getMessage()).isEqualTo(Errors.RECIPE_NOT_FOUND);
	}

	@Test
	void findByInstructions() {
		insertRecipesForFindTests();

		var recipes1 = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.formParam("text", "taco")
				.when()
				.get(URL_RECIPE_FIND)
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(RecipeDto[].class);

		assertThat(recipes1.length).isEqualTo(1);
		assertThat(recipes1[0].getTitle().toLowerCase()).contains("taco");

		var recipes2 = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.formParam("text", "vegetables")
				.when()
				.get(URL_RECIPE_FIND)
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(RecipeDto[].class);

		assertThat(recipes2.length).isEqualTo(2);
		assertThat(recipes2[0].getInstructions()).contains("vegetables");
		assertThat(recipes2[1].getInstructions()).contains("vegetables");
	}

	@Test
	void findServes() {
		insertRecipesForFindTests();

		var recipes1 = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.formParam("servings", "2")
				.when()
				.get(URL_RECIPE_FIND)
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(RecipeDto[].class);

		assertThat(recipes1.length).isEqualTo(2);
	}

	@Test
	void findVegetarians() {
		insertRecipesForFindTests();

		var recipes1 = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.formParam("vegetarian", true)
				.when()
				.get(URL_RECIPE_FIND)
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(RecipeDto[].class);

		assertThat(recipes1.length).isEqualTo(4);
	}

	@Test
	void findIncludes() {
		insertRecipesForFindTests();

		var recipes1 = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.formParam("include", "carrot")
				.when()
				.get(URL_RECIPE_FIND)
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(RecipeDto[].class);

		assertThat(recipes1.length).isEqualTo(2);

		var recipes2 = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.formParam("include", "basil")
				.when()
				.get(URL_RECIPE_FIND)
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(RecipeDto[].class);

		assertThat(recipes2.length).isEqualTo(3);
	}

	@Test
	void findExcludes() {
		insertRecipesForFindTests();

		var recipes1 = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.formParam("exclude", "basil")
				.when()
				.get(URL_RECIPE_FIND)
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(RecipeDto[].class);

		assertThat(recipes1.length).isEqualTo(2);

		var recipes2 = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.formParam("exclude", "carrot")
				.when()
				.get(URL_RECIPE_FIND)
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(RecipeDto[].class);

		assertThat(recipes2.length).isEqualTo(3);
	}

	@Test
	void findIncludeExcludes() {
		insertRecipesForFindTests();

		var recipes1 = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.formParam("include", "basil")
				.formParam("exclude", "carrot")
				.when()
				.get(URL_RECIPE_FIND)
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(RecipeDto[].class);

		assertThat(recipes1.length).isEqualTo(1);

		var recipes2 = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.formParam("include", "potato")
				.formParam("exclude", "rice")
				.when()
				.get(URL_RECIPE_FIND)
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(RecipeDto[].class);

		assertThat(recipes2.length).isEqualTo(1);

		var recipes3 = given()
				.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
				.formParam("include", "rice noodle")
				.formParam("exclude", "basil")
				.when()
				.get(URL_RECIPE_FIND)
				.then()
				.statusCode(HttpStatus.OK.value())
				.extract()
				.as(RecipeDto[].class);

		assertThat(recipes3.length).isEqualTo(0);
	}

	private void insertRecipesForFindTests() {
		for (var recipe : okRecipeList) {
			given()
					.body(recipe)
					.header(HttpHeaders.CONTENT_TYPE, ContentType.JSON)
					.when()
					.post(URL_RECIPE)
					.then()
					.statusCode(HttpStatus.OK.value());

			var savedRecipe = recipeRepository.findByTitle(recipe.getTitle());
			assertThat(savedRecipe.isPresent()).isTrue();
		}
	}
}
