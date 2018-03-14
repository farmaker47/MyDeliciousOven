package com.george.mydeliciousoven;

/**
 * Created by farmaker1 on 12/03/2018.
 */

public class Ingredients {

    String quantity, measure, ingredient;

    public Ingredients(String string, String string2, String string3) {
        quantity = string;
        measure = string2;
        ingredient = string3;
    }


    public String getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }
}
