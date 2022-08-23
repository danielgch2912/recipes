package nl.abnamro.recipes.utils;

import nl.abnamro.recipes.dto.RecipeDto;

import java.util.ArrayList;
import java.util.Arrays;

public class SampleData {

    public static ArrayList<RecipeDto> create()
    {
        var okRecipeList = new ArrayList<RecipeDto>();

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

        return okRecipeList;
    }
}
