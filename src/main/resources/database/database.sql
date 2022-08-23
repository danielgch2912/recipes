DROP TABLE IF EXISTS recipe_ingredient;
DROP TABLE IF EXISTS ingredient;
DROP TABLE IF EXISTS recipe;

CREATE TABLE recipe (
   recipe_id BIGINT AUTO_INCREMENT PRIMARY KEY,
   title VARCHAR(255) NOT NULL UNIQUE,
   instructions TEXT NOT NULL,
   serves INT(2) NOT NULL,
   vegetarian INT(1) NOT NULL
);

CREATE TABLE ingredient(
	ingredient_id BIGINT PRIMARY KEY AUTO_INCREMENT,
	title VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE recipe_ingredient(
	recipe_id BIGINT NOT NULL REFERENCES recipe(recipe_id),
	ingredient_id BIGINT NOT NULL REFERENCES ingredient(ingredient_id),

	PRIMARY KEY (recipe_id, ingredient_id)
);
