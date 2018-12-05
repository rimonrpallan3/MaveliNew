package com.mavelinetworks.mavelideals.classes;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by idriss on 02/07/2016.
 */

public class Category extends RealmObject implements Serializable {

    @PrimaryKey
    private  int numCat;
    private int type;
    private String nameCat;
    private int  parentCategory;
    private int icon=0;
    private boolean menu =true;
    private int nbr_stores;

    public int getNbr_stores() {
        return nbr_stores;
    }

    public void setNbr_stores(int nbr_stores) {
        this.nbr_stores = nbr_stores;
    }

    public boolean isMenu() {
        return menu;
    }

    public void setMenu(boolean menu) {
        this.menu = menu;
    }

    @Ignore
    private Drawable drawableIcon;
    private Images images;

    public Drawable getDrawableIcon() {
        return drawableIcon;
    }

    public void setDrawableIcon(Drawable drawableIcon) {
        this.drawableIcon = drawableIcon;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Category(int numCat, String nameCat, int parentCategory, int icon) {
        this.numCat = numCat;
        this.nameCat = nameCat;
        this.parentCategory = parentCategory;
        this.icon = icon;
        this.type = numCat;
        this.menu = true;
    }
    public Category(int numCat, String nameCat, int parentCategory, Drawable icon) {
        this.numCat = numCat;
        this.nameCat = nameCat;
        this.parentCategory = parentCategory;
        this.drawableIcon = icon;
        this.type = numCat;
        this.icon = 0;
        this.menu = true;
    }

    public Category() {

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

