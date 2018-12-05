package com.mavelinetworks.mavelideals.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 31-Jul-18.
 */

public class Errors implements Parcelable {

    String connect;
    String otp;
    String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Errors(String connect) {
        this.connect = connect;
    }

    public String getConnect() {
        return connect;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.connect);
    }

    protected Errors(Parcel in) {
        this.connect = in.readString();
    }

    public static final Parcelable.Creator<Errors> CREATOR = new Parcelable.Creator<Errors>() {
        @Override
        public Errors createFromParcel(Parcel source) {
            return new Errors(source);
        }

        @Override
        public Errors[] newArray(int size) {
            return new Errors[size];
        }
    };
}
