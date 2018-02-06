package com.premium.patternbox.app;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by b on 1/13/2017.
 */

public class PatternInfo implements Serializable{
    public int id;
    public String name;
    public String key;
    public String info;
    public String frontImage;
    public String backImage;
    public String urls;
    public String categories;
    public int isPdf;
    public int isBookmark;
    public PatternInfo() {

    }

    public ArrayList<String> getUrls() {
        ArrayList<String> newList = new ArrayList<String>();
        String[] separated = urls.split(",");
        for (int i = 0; i< separated.length ; i++){
            newList.add(separated[i]);
        }
        return newList;
    }

    public String getCategories(){
        String categoryList = "";
        String[] separated = categories.split(",");
        for (int i = 0; i< separated.length ; i++){
            String str = AppConfig.getPatternCategoryName(separated[i]);
            if (str != null){
                categoryList += ", " + str;
            }
        }
        if (categoryList.length() > 0){
            categoryList = categoryList.substring(2);
        }
        return categoryList;
    }
}
