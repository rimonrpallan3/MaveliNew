package com.mavelinetworks.mavelideals.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.mavelinetworks.mavelideals.activities.signuppage.model.IUserDetails;

import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

import static com.mavelinetworks.mavelideals.utils.Utils.isValid;

/**
 * Created by User on 23-Jul-18.
 */

public class UserDetails implements Parcelable,IUserDetails {
    int success = 0;
    UserRow userRow;
    Errors errors;
    @PrimaryKey
    private int id;

    private String name;
    private String username;
    private String password;
    private String job;
    private String email;
    private String phone;
    private String country;
    private String city;
    private String token="";
    private String auth;
    private Images images;
    private String status;
    private boolean followed = false;
    private Double latitude;
    private Double longitude;
    private Double distance;
    private String tokenGCM = "";
    private String type = TYPE_POTER;
    private boolean online;
    public static String TYPE_POTER = "POTER";

    @Ignore
    private boolean withHeader=false;

    public UserDetails() {
    }

    public UserDetails(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserDetails(String name, String password, String email, String auth, String phone) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.auth = auth;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getTokenGCM() {
        return tokenGCM;
    }

    public void setTokenGCM(String tokenGCM) {
        this.tokenGCM = tokenGCM;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public static String getTypePoter() {
        return TYPE_POTER;
    }

    public static void setTypePoter(String typePoter) {
        TYPE_POTER = typePoter;
    }

    public boolean isWithHeader() {
        return withHeader;
    }

    public void setWithHeader(boolean withHeader) {
        this.withHeader = withHeader;
    }

    public static Creator<UserDetails> getCREATOR() {
        return CREATOR;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public UserRow getUserRow() {
        return userRow;
    }

    public void setUserRow(UserRow userRow) {
        this.userRow = userRow;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getTel() {
        return phone;
    }

    public void setTel(String phone) {
        this.phone = phone;
    }


    @Override
    public int validateUserDetails(String FullName, String email, String mobNo, String pswd, String confirmPswd, Boolean termsAndCondCheck) {
        if (FullName.trim().length()==0||
                pswd.trim().length()==0||
                confirmPswd.trim().length()==0||
                email.trim().length()==0||
                mobNo.trim().length()==0||
                termsAndCondCheck.toString().length()==0){
            {
                return -1;
            }
        }else {
            /*for (int i = 0; i < FullName.trim().length(); i++) {
                char charAt2 = FullName.trim().charAt(i);
                if (!Character.isLetter(charAt2)) {
                    return -2;
                }
            }*/
            for (int i = 0; i < password.trim().length(); i++) {
                String charAt2 = password.trim().toString();
                if (charAt2==null) {
                    return -3;
                }
            }

            if(!password.equals(confirmPswd)){
                return -4;
            }
            if (!isValid(email)) {
                return -5;
                /*for (int i = 0; i < email.trim().length(); i++) {
                    String charAt2 = email.trim().toString();
                    if (charAt2 == null) {
                        return -5;
                    }
                }*/
            }
            for (int i = 0; i < email.trim().length(); i++) {
                String charAt2 = email.trim().toString();
                if (charAt2==null) {
                    return -5;
                }
            }
            for (int i = 0; i < mobNo.trim().length(); i++) {
                String charAt2 = mobNo.trim().toString();
                if (charAt2==null) {
                    return -6;
                }
            }
                if (!termsAndCondCheck) {
                    return -8;
                }

        }
        return 0;
    }

    @Override
    public int validateRegisterResponseError(int success) {
        if(success!=0){
            //if there is no error message then it means that data response is correct.
            return -9;
        }
        return 0;
    }

    @Override
    public int checkUserValidity(String name, String passwd) {
        if (email==null||passwd==null||!email.equals(getEmail())||!passwd.equals(getPassword())){
            return -1;
        }
        return 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.success);
        dest.writeParcelable(this.userRow, flags);
        dest.writeParcelable(this.errors, flags);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.job);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.country);
        dest.writeString(this.city);
        dest.writeString(this.token);
        dest.writeString(this.auth);
        dest.writeSerializable(this.images);
        dest.writeString(this.status);
        dest.writeByte(this.followed ? (byte) 1 : (byte) 0);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
        dest.writeValue(this.distance);
        dest.writeString(this.tokenGCM);
        dest.writeString(this.type);
        dest.writeByte(this.online ? (byte) 1 : (byte) 0);
        dest.writeByte(this.withHeader ? (byte) 1 : (byte) 0);
    }

    protected UserDetails(Parcel in) {
        this.success = in.readInt();
        this.userRow = in.readParcelable(UserRow.class.getClassLoader());
        this.errors = in.readParcelable(Errors.class.getClassLoader());
        this.id = in.readInt();
        this.name = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.job = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.country = in.readString();
        this.city = in.readString();
        this.token = in.readString();
        this.auth = in.readString();
        this.images = (Images) in.readSerializable();
        this.status = in.readString();
        this.followed = in.readByte() != 0;
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.distance = (Double) in.readValue(Double.class.getClassLoader());
        this.tokenGCM = in.readString();
        this.type = in.readString();
        this.online = in.readByte() != 0;
        this.withHeader = in.readByte() != 0;
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel source) {
            return new UserDetails(source);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };
}
