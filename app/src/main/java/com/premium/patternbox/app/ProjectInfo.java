package com.premium.patternbox.app;

import java.io.Serializable;

/**
 * Created by b on 1/13/2017.
 */

public class ProjectInfo implements Serializable{
    public int id;
    public String name;
    public String client;
    public String notes;
    public String measurements;
    public String image;
    public int pattern;
    public String fabrics;
    public String notions;

    public ProjectInfo() {

    }
}
