package com.george.mydeliciousoven;

/**
 * Created by farmaker1 on 12/03/2018.
 */

public class Recipes {

    String id,name,servings,image;

    public Recipes(String string,String string2,String string3,String string4){
        id = string;
        name = string2;
        servings = string3;
        image = string4;
    }

    public String getImage() {
        return image;
    }

    public String getServings(){
        return servings;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }
}
