package com.mavelinetworks.mavelideals.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 30-Jul-18.
 */

public class UserRow implements Parcelable {

    /**
     * id : 24
     * name : megha
     * password : 14ed796766c8f9f98a7d119fca721f838137e41c
     * email : megha@itvoyager.com
     * mobile : 1234567891
     * images : null
     * status : 1
     * confirmed : 1
     * dateLogin : null
     * typeAuth : customer
     * is_online : 0
     * lat : null
     * lng : null
     * country : null
     * date_created : 2018-07-30 05:01:50
     */

    private String id;
    private String name;
    private String password;
    private String email;
    private String mobile;
    private String images;
    private String status;
    private String confirmed;
    private String dateLogin;
    private String typeAuth;
    private String is_online;
    private String lat;
    private String lng;
    private String country;
    private String date_created;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getDateLogin() {
        return dateLogin;
    }

    public void setDateLogin(String dateLogin) {
        this.dateLogin = dateLogin;
    }

    public String getTypeAuth() {
        return typeAuth;
    }

    public void setTypeAuth(String typeAuth) {
        this.typeAuth = typeAuth;
    }

    public String getIs_online() {
        return is_online;
    }

    public void setIs_online(String is_online) {
        this.is_online = is_online;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.password);
        dest.writeString(this.email);
        dest.writeString(this.mobile);
        dest.writeString(this.images);
        dest.writeString(this.status);
        dest.writeString(this.confirmed);
        dest.writeString(this.dateLogin);
        dest.writeString(this.typeAuth);
        dest.writeString(this.is_online);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.country);
        dest.writeString(this.date_created);
    }

    public UserRow() {
    }

    protected UserRow(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.password = in.readString();
        this.email = in.readString();
        this.mobile = in.readString();
        this.images = in.readString();
        this.status = in.readString();
        this.confirmed = in.readString();
        this.dateLogin = in.readString();
        this.typeAuth = in.readString();
        this.is_online = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.country = in.readString();
        this.date_created = in.readString();
    }

    public static final Creator<UserRow> CREATOR = new Creator<UserRow>() {
        @Override
        public UserRow createFromParcel(Parcel source) {
            return new UserRow(source);
        }

        @Override
        public UserRow[] newArray(int size) {
            return new UserRow[size];
        }
    };
}
