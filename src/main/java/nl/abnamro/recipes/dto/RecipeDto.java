package nl.abnamro.recipes.dto;

import lombok.Builder;
import lombok.Data;
import nl.abnamro.recipes.domain.Recipe;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class RecipeDto {

    private Integer recipeId;

    @NotBlank
    private String title;

    @NotBlank
    private String instructions;

    @NotNull
    private Integer serves;

    @NotNull
    private Boolean vegetarian;

    @NotNull
    public List<String> ingredients;

    public static Recipe toEntity(RecipeDto dto){
        return  Recipe.builder()
                .recipeId(dto.getRecipeId())
                .title(dto.getTitle())
                .instructions(dto.getInstructions())
                .serves(dto.getServes())
                .vegetarian(dto.getVegetarian())
                .build();
    }

    public static List<RecipeDto> toDTO(List<Recipe> recipe) {
        var ls = new ArrayList<RecipeDto>();

        for(var r : recipe) {
            ls.add(toDTO(r));
        }

        return ls;
    }

    public static RecipeDto toDTO(Recipe recipe){
        var dto = RecipeDto.builder()
                .recipeId(recipe.getRecipeId())
                .title(recipe.getTitle())
                .instructions(recipe.getInstructions())
                .serves(recipe.getServes())
                .vegetarian(recipe.getVegetarian())
                .build();

        if (recipe.getIngredients() != null) {
            var ingredientsDto = recipe.getIngredients().stream()
                    .map(e -> e.getTitle())
                    .collect(Collectors.toList());

            dto.setIngredients(ingredientsDto);
        }

        return dto;
    }
}
