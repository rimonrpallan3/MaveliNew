package com.mavelinetworks.mavelideals.classes;

import java.io.Serializable;

/**
 * Created by Droideve on 7/14/2016.
 */

public class TabItem implements Serializable {

    private int type;
    private  int numCat;
    private String nameCat;
    private int  parentCategory;
    private int icon;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public TabItem(int numCat, String nameCat, int parentCategory, int icon) {
        this.numCat = numCat;
        this.nameCat = nameCat;
        this.parentCategory = parentCategory;
        this.icon = icon;
        this.type = numCat;
    }

    public TabItem() {

    }

    public int getNumCat() {
        return numCat;
    }

    public void setNumCat(int numCat) {
        this.numCat = numCat;
    }

    public String getNameCat() {
        return nameCat;
    }

    public void setNameCat(String nameCat) {
        this.nameCat = nameCat;
    }

    public int getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(int parentCategory) {
        this.parentCategory = parentCategory;
    }


}
