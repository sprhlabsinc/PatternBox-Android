package com.premium.patternbox.app;

import java.util.ArrayList;

/**
 * Created by b on 1/13/2017.
 */

public class NotionEditInfo {
    public String  id;
    public String name;
    public  boolean isDefault;
    public ArrayList<NotionInfo> notionInfos;

    public NotionEditInfo(String id, String name, boolean isDefault, ArrayList<NotionInfo> notionInfos) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
        this.notionInfos = notionInfos;
    }

    public NotionEditInfo() {
    }
}
