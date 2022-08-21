package nl.abnamro.recipes.repository;

import nl.abnamro.recipes.domain.Ingredient;
import nl.abnamro.recipes.domain.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomRecipeRepositoryImpl implements CustomRecipeRepository {

    @Autowired
    private EntityManager em;

    @Override
    public List<Recipe> find(String text, String include, String exclude, Integer servings, Boolean vegetarian) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Recipe> cq = cb.createQuery(Recipe.class);
        Root<Recipe> recipe = cq.from(Recipe.class);

        List<Predicate> predicatesAnd = new ArrayList<Predicate>();

        if (text != null && text != "") {
            Predicate titlePredicate = cb.like(recipe.get("title"), "%" + text + "%");
            Predicate instructionsPredicate = cb.like(recipe.get("instructions"), "%" + text + "%");

            predicatesAnd.add(cb.or(titlePredicate, instructionsPredicate));
        }

        if (include != null && include != "") {
            Subquery<Ingredient> subQuery = cq.subquery(Ingredient.class);
            Root<Recipe> rec = subQuery.from(Recipe.class);

            Join<Recipe, Ingredient> setJoin = rec.join("ingredients");
            subQuery.where(cb.like(setJoin.get("title"), include));
            subQuery.select(rec.get("recipeId"));

            predicatesAnd.add(cb.in(recipe.get("recipeId")).value(subQuery));
        }

        if (exclude != null && exclude != "") {
            Subquery<Ingredient> subQuery = cq.subquery(Ingredient.class);
            Root<Recipe> rec = subQuery.from(Recipe.class);

            Join<Recipe, Ingredient> setJoin = rec.join("ingredients");
            subQuery.where(cb.like(setJoin.get("title"), exclude));
            subQuery.select(rec.get("recipeId"));

            predicatesAnd.add(cb.in(recipe.get("recipeId")).value(subQuery).not());
        }

        if (servings != null) {
            Predicate servingsPredicate = cb.equal(recipe.get("serves"), servings);

            predicatesAnd.add(servingsPredicate);
        }

        if (vegetarian != null) {
            Predicate vegetarianPredicate = cb.equal(recipe.get("vegetarian"), vegetarian);

            predicatesAnd.add(vegetarianPredicate);
        }

        cq.where(predicatesAnd.toArray(Predicate[]::new));

        TypedQuery<Recipe> query = em.createQuery(cq);
        return query.getResultList();
    }
}


