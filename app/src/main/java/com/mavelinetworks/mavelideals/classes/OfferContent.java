package com.mavelinetworks.mavelideals.classes;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Droideve on 11/8/2017.
 */

public class OfferContent extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;
    private String description;
    private Float price;
    private Float percent;
    private String currency;
    private Float actualPrice;
    private Float offerPercent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getPercent() {
        return percent;
    }

    public void setPercent(Float percent) {
        this.percent = percent;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Float getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(Float actualPrice) {
        this.actualPrice = actualPrice;
    }

    public Float getOfferPercent() {
        return offerPercent;
    }

    public void setOfferPercent(Float offerPercent) {
        this.offerPercent = offerPercent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.description);
        dest.writeValue(this.price);
        dest.writeValue(this.percent);
        dest.writeString(this.currency);
        dest.writeValue(this.actualPrice);
        dest.writeValue(this.offerPercent);
    }

    public OfferContent() {
    }

    protected OfferContent(Parcel in) {
        this.id = in.readInt();
        this.description = in.readString();
        this.price = (Float) in.readValue(Float.class.getClassLoader());
        this.percent = (Float) in.readValue(Float.class.getClassLoader());
        this.currency = in.readString();
        this.actualPrice = (Float) in.readValue(Float.class.getClassLoader());
        this.offerPercent = (Float) in.readValue(Float.class.getClassLoader());
    }

    public static final Creator<OfferContent> CREATOR = new Creator<OfferContent>() {
        @Override
        public OfferContent createFromParcel(Parcel source) {
            return new OfferContent(source);
        }

        @Override
        public OfferContent[] newArray(int size) {
            return new OfferContent[size];
        }
    };
}
