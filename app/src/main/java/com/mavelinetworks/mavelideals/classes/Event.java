package com.mavelinetworks.mavelideals.classes;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by idriss on 02/10/2016.
 */

public class Event extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String address;
    private Images Images;
    private String imageJson;
    private String dateB,dateE;
    private String description;
    private Double distance;
    private Double lat;
    private Double lng;
    private String tel;
    private String webSite;
    private int type;
    public int  status;
    private boolean liked=false;
    private int store_id;
    private String store_name;

    private int featured;

    public int getFeatured() {
        return featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }


    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    private RealmList<Images> listImages;

    public static class Tags{

        public static final String LISTIMAGES = "ListImages" ;
        public static String ID = "id_event";
        public static String NAME = "name";
        public static String ADDRESS = "description";
        public static String LAT = "lat";
        public static String LONG = "lng";
        public static String DISTANCE = "distance";
        public static String STATUS = "status";
        public static String DESCRIPTION = "detail";
        public static String USER = "user";
        public static String IMAGES = "images";
        public static String PHONE = "tel";
        public static String OTHER = "autres";
        public static String TEL = "tel";
        public static String WEBSITE = "website";
        public static String DATE_B = "date_b";
        public static String DATE_E = "date_f";
        public static String TYPE = "type";


    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getDateB() {
        return dateB;
    }

    public void setDateB(String dateB) {
        this.dateB = dateB;
    }

    public String getDateE() {
        return dateE;
    }

    public void setDateE(String dateE) {
        this.dateE = dateE;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTel() {
        return tel;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Double getLat() {
        return lat;
    }



    public List<Images> getListImages() {
        return listImages;
    }

    public void setListImages(RealmList<Images> listImages) {
        this.listImages = listImages;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public com.mavelinetworks.mavelideals.classes.Images getImages() {
        return Images;
    }

    public void setImages(com.mavelinetworks.mavelideals.classes.Images images) {
        Images = images;
    }

    public String getImageJson() {
        return imageJson;
    }

    public void setImageJson(String imageJson) {
        this.imageJson = imageJson;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
