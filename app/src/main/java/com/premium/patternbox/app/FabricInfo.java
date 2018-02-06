package com.premium.patternbox.app;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by b on 1/13/2017.
 */

public class FabricInfo implements Serializable{
    public int id;
    public String imageUrl;
    public String categories;
    public int isBookmark;

    public String name;
    public String type;
    public String color;
    public String width;
    public String length;
    public String weight;
    public String stretch;
    public String uses;
    public String careInstructions;
    public String notes;

    public FabricInfo() {

    }

    public String getCategories(){
        String categoryList = "";
        String[] separated = categories.split(",");
        for (int i = 0; i< separated.length ; i++){
            String str = AppConfig.getFabricCategoryName(separated[i]);
            if (str != null){
                categoryList += "," + str;
            }
        }
        if (categoryList.length() > 0){
            categoryList = categoryList.substring(1);
        }
        return categoryList;
    }
}
