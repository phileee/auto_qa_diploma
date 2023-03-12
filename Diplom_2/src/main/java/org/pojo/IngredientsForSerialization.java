package org.pojo;

import java.util.List;

public class IngredientsForSerialization {
    private List<String> ingredients;

    public IngredientsForSerialization(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public IngredientsForSerialization() {}

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredients(String ingredient) {
        ingredients.add(ingredient);
    }
}
