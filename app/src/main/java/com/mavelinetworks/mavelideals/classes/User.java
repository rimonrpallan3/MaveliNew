package com.mavelinetworks.mavelideals.classes;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Droideve on 2/12/2016.
 */
public class User extends RealmObject {



    public static String MANGER = "manager";
    public static String ADMIN = "admin";
    public static String CUSTOMER = "customer";

    public static String TYPE_SENDER = "SENDER";
    public static String TYPE_LOGGED = "LOGGED";
    public static String TYPE_FOLLOWER = "FOLLOWER";
    public static String TYPE_POTER = "POTER";
    public static String TYPE_WITH_TEXT_HEADER= "HEADR";

    public static class DefaultValues{
        public static int nbrFollowing=0;
        public static int nbrFollowers=0;
    }

    public static class Tags{
        public static String ID = "id_user";
        public static String USERNAME = "username";
        public static String PASSWORD = "password";
        public static String EMAIL = "email";
        public static String PHONE = "phone";
        public static String FIRSTNAME = "name";
        public static String LASTNAME = "lastname";
        public static String AUTH = "auth_type";
        public static String STATUS = "status";
        public static String IMAGES = "images";
        public static String IMAGE_100 = "image100";
        public static String IMAGE_200 = "images200";
        public static String IMAGE_500 = "images500";
        public static String FOLLOWED = "followed";
        public static String GCMTOKEN = "gcmtoken";
        public static String TYPE = "type";
        public static String SENDERID ="senderid";

    }


    private String senderid;

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

    @Ignore
    private boolean withHeader=false;

    public boolean isWithHeader() {
        return withHeader;
    }

    public void setWithHeader(boolean withHeader) {
        this.withHeader = withHeader;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    private String aboutJson;
    private boolean blocked;

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

    public String getAboutJson() {
        return aboutJson;
    }

    public void setAboutJson(String aboutJson) {
        this.aboutJson = aboutJson;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTokenGCM() {
        return tokenGCM;
    }

    public void setTokenGCM(String tokenGCM) {
        this.tokenGCM = tokenGCM;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String telephone) {
        this.phone = telephone;
    }





}
