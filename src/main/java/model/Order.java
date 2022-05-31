package model;

import java.util.ArrayList;


public class Order {

    private ArrayList <String> ingredients;

    public Order(ArrayList<String> id) {
        this.ingredients = id;
    }

    public Order() {}

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }
}
