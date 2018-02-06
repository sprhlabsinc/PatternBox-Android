package com.premium.patternbox.app;

import java.util.ArrayList;

/**
 * Created by b on 1/13/2017.
 */

public class PatternCategoryInfo {
    public String  id;
    public String name;
    public int icon;
    public  boolean isDefault;

    public PatternCategoryInfo(String id, String name, int icon, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.isDefault = isDefault;
    }
    public PatternCategoryInfo() {
    }
}
