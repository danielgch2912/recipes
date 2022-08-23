# ABN Recipes

ABN Recipes is a service that uses REST Webservices to find and maintain recipes. It was built using Java, Spring Boot framework and MySql database.

Requirements:
 - Java 17
 - Docker
 - Docker Compose

The webservice data structure is:
````
{
    "recipeId": Integer,
    "title": String,
    "instructions": String,
    "serves": Integer,
    "vegetarian": Boolean,
    "ingredients": Array[String]
}
````

Example:
````json
{
    "recipeId": 1,
    "title": "Red Curry",
    "instructions": "Mix all the vegetables, cook for 30 minutes and it's ready",
    "serves": 2,
    "vegetarian": true,
    "ingredients": [
        "Carrot",
        "Chickpea",
        "Cumin",
        "Rice",
        "Potato",
        "Basil"
    ]
}
````

The endpoint url is:
````
    http://localhost:8080/api/v1/recipe
````
To insert and update a recipe you can send a call to the endpoint using methods POST and PUT with a JSon as a body based on the webservice data structure.
To delete a recipe use the DELETE method with **/api/v1/recipe/{recipeID}** as url.

To find a recipe, you can do a GET on the url: **/api/v1/recipe/find** using the following parameters:

````
text: Text filter to be applied in title and instructions of recipes.
include: Includes a determined ingredient in the search
exclude: Excludes a determined ingredient in the search
servings: How many people this recipe serves
vegetarian: Whether it is a vegetarian recipe or not
```` 

To run the application using docker, run the following command
````shell
    docker compose up
````

To execute the tests you can use maven command
````shell
mvn clean test
````

### Architectural Decisions

The application is structured in 3 layers. The repository to operate the database, the controller to handle user requests
and service to organize, validate and transfer data between the controller and the repository.

The data structure used by the controller is based on the Data Transfer Object design pattern. The service is responsible
for transforming data into entities to be handled by the repository.

#### Database
I have decided to use a relational database because it's easier to avoid repetition of ingredients between recipes.
The database was modeled in 3 tables: recipe, ingredient and recipe_ingredient. The table recipe_ingredient is a join
table used to map the N:N relationship between recipe and ingredient. Check **database.sql** for details of the database structure.