package com.mavelinetworks.mavelideals.classes;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by User on 03-Apr-18.
 */

public class Filter extends RealmObject implements Serializable {

    private String offerTypeId;
    private String nameFilt;
    private String offerParentId;
    private String offerImage;
    private int status;
    private RealmList<FilterChild> filterChild;
    Boolean isChecked = false;


    public Filter() {

    }

    public Filter(String offerTypeId, String nameFilt, String offerParentId, String offerImage, int status) {
        this.offerTypeId = offerTypeId;
        this.nameFilt = nameFilt;
        this.offerParentId = offerParentId;
        this.offerImage = offerImage;
        this.status = status;

    }

    public String getOfferTypeId() {
        return offerTypeId;
    }

    public void setOfferTypeId(String offerTypeId) {
        this.offerTypeId = offerTypeId;
    }

    public String getNameFilt() {
        return nameFilt;
    }

    public void setNameFilt(String nameFilt) {
        this.nameFilt = nameFilt;
    }

    public String getOfferParentId() {
        return offerParentId;
    }

    public void setOfferParentId(String offerParentId) {
        this.offerParentId = offerParentId;
    }

    public String getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(String offerImage) {
        this.offerImage = offerImage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public RealmList<FilterChild> getFilterChild() {
        return filterChild;
    }

    public void setFilterChild(RealmList<FilterChild> filterChild) {
        this.filterChild = filterChild;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }
}