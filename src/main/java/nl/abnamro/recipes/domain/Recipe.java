package nl.abnamro.recipes.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer recipeId;

    private String title;

    private String instructions;

    private Integer serves;

    private Boolean vegetarian;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "recipe_ingredient",
            joinColumns = @JoinColumn(name = "recipe_id", referencedColumnName = "recipeId"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id", referencedColumnName = "ingredientID")

    )
    private List<Ingredient> ingredients;
}
