package com.premium.patternbox.app;

/**
 * Created by b on 1/13/2017.
 */

public class FabricCategoryInfo {
    public String  id;
    public String name;
    public  boolean isDefault;

    public FabricCategoryInfo(String id, String name, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
    }
    public FabricCategoryInfo() {
    }
}
