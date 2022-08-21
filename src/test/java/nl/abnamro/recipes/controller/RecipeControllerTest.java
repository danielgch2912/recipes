package nl.abnamro.recipes.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import nl.abnamro.recipes.dto.ErrorDto;
import nl.abnamro.recipes.dto.RecipeDto;
import nl.abnamro.recipes.repository.IngredientRepository;
import nl.abnamro.recipes.repository.RecipeRepository;
import nl.abnamro.recipes.utils.Errors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RecipeControllerTest {

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private IngredientRepository ingredientRepository;

	private List<RecipeDto> okRecipeList;

	private List<String> ingredients;

	public static String URL_RECIPE = "/v1/recipe";

	public static String URL_RECIPE_FIND = "/v1/recipe/find";

	@BeforeAll
	public static void before() {
		RestAssured.baseURI = "http://localhost:8080/api";
	}

	@BeforeEach
	public void cleanAndPrepareTests() {
		recipeRepository.deleteAll();
		ingredientRepository.deleteAll();

		okRecipeList = new ArrayList<RecipeDto>();

		var carrot = "Carrot";
		var chickpea = "Chickpea";
		var cumin = "Cumin";
		var rice = "Rice";
		var potato = "Potato";

		var salmon = "Salmon";
		var bean = "Bean";
		var lettuce = "Lettuce";
		var tomato = "Tomato";
		var basil = "Basil";
		var riceNoodle = "Rice Noodle";

		var recipe1 = RecipeDto.builder()
				.title("Red Curry")
				.instructions("Mix all the vegetables, cook for 30 minutes and it's ready")
				.vegetarian(true)
				.serves(2)
				.ingredients(Arrays.asList(carrot, chickpea, cumin, rice, potato, basil))
				.build();

		var recipe2 = RecipeDto.builder()
				.title("Enhanced Salmon")
				.instructions("Put the salmon in the oven with lots of vegetables and wait for an hour and a half")
				.vegetarian(false)
				.serves(3)
				.ingredients(Arrays.asList(salmon, bean, cumin, chickpea, tomato))
				.build();

		var recipe3 = RecipeDto.builder()
				.title("Pad Thai")
				.instructions("Soak the noodle in cold water for 15 minutes and mora 30 minutes in the pan and it is ready")
				.vegetarian(true)
				.serves(4)
				.ingredients(Arrays.asList(riceNoodle, carrot, tomato, basil, lettuce))
				.build();

		var recipe4 = RecipeDto.builder()
				.title("Mexican Taco")
				.instructions("Just fry everything")
				.vegetarian(true)
				.serves(1)
				.ingredients(Arrays.asList(rice, bean, basil))
				.build();

		var recipe5 = RecipeDto.builder()
				.title("French Fries")
				.instructions("Slice the potato and fry it")
				.vegetarian(true)
				.serves(2)
				.ingredients(Arrays.asList(potato))
				.build();


		okRecipeList.add(recipe1);
		okRecipeList.add(recipe2);
		okRecipeList.add(recipe3);
		okRecipeList.add(recipe4);
		okRecipeList.add(recipe5);
	}

	@Test
	void saveRecipe() {
		var recipe = okRecipeList.get(0);

		var response = given()
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

		int i = 0;
		for(var ingredientFromDB : ingredientsFromDB) {
			assertThat(ingredientFromDB).isNotNull();
			assertThat(ingredientFromDB.getTitle()).isEqualTo(recipe.ingredients.get(i));
			i++;
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
				//.body("field", equalTo("title"))
				//.body("message", equalTo(Errors.RECIPE_SAME_TITLE));

		assertThat(errors.length).isEqualTo(1);
		assertThat(errors[0].getField()).isEqualTo("title");
		assertThat(errors[0].getMessage()).isEqualTo(Errors.RECIPE_SAME_TITLE);
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
