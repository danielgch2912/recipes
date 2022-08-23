package nl.abnamro.recipes.controller;

import lombok.RequiredArgsConstructor;
import nl.abnamro.recipes.dto.RecipeDto;
import nl.abnamro.recipes.service.RecipeService;
import nl.abnamro.recipes.service.ServiceErrorException;
import nl.abnamro.recipes.utils.SampleData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/recipe")
public class RecipeController {

    @Autowired
    private RecipeService service;

    @GetMapping(path = "find")
    public List<RecipeDto> find(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String include,
            @RequestParam(required = false) String exclude,
            @RequestParam(required = false) Integer servings,
            @RequestParam(required = false) Boolean vegetarian){

        var ls = service.find(text, include, exclude, servings, vegetarian);

        return RecipeDto.toDTO(ls);
    }

    @PostMapping
    public ResponseEntity save(@RequestBody @Valid RecipeDto recipeDto) {

        try {
            recipeDto = service.save(recipeDto);
            return ResponseEntity.ok(recipeDto);
        } catch(ServiceErrorException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        }
    }

    @PutMapping
    public ResponseEntity saveOrUpdate(@RequestBody @Valid RecipeDto recipeDto) {

        try {
            recipeDto = service.saveOrUpdate(recipeDto);
            return ResponseEntity.ok(recipeDto);
        } catch(ServiceErrorException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        }
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity delete(@PathVariable Integer recipeId) {

        try {
            service.delete(recipeId);
            return ResponseEntity.ok().build();
        } catch(ServiceErrorException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        }
    }

    /**
     * This method is here just for test purposes. It will generate sample data
     * that can be inserted without running the testcase. The purpose is to
     * look for a recipe directly from browser or Postman after using
     * docker compose to run the application.
     */
    @GetMapping("/sampledata")
    public ResponseEntity GenerateSampleData()
    {
        try {
            var data = SampleData.create();

            for (var d: data) {
                service.save(d);
            }

            return ResponseEntity.ok().build();
        } catch(ServiceErrorException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        }
    }

}
