package com.mavelinetworks.mavelideals.activities.firstotppage.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 04-Jun-18.
 */

public class CountryDetails implements Parcelable {


    /**
     * name : Afghanistan
     * dial_code : +93
     * code : AF
     */

    private String name;
    private String dial_code;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDial_code() {
        return dial_code;
    }

    public void setDial_code(String dial_code) {
        this.dial_code = dial_code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.dial_code);
        dest.writeString(this.code);
    }

    public CountryDetails() {
    }

    protected CountryDetails(Parcel in) {
        this.name = in.readString();
        this.dial_code = in.readString();
        this.code = in.readString();
    }

    public static final Parcelable.Creator<CountryDetails> CREATOR = new Parcelable.Creator<CountryDetails>() {
        @Override
        public CountryDetails createFromParcel(Parcel source) {
            return new CountryDetails(source);
        }

        @Override
        public CountryDetails[] newArray(int size) {
            return new CountryDetails[size];
        }
    };
}
