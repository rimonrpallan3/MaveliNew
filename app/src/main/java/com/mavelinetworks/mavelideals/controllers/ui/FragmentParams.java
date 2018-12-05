package com.mavelinetworks.mavelideals.controllers.ui;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Droideve on 11/5/2016.
 */

public class FragmentParams implements Serializable{

    protected int fragment_type;
    protected boolean list_refresh;
    protected String  list_type;
    protected String  content_type;
    protected boolean appUser=false;
    protected transient JSONObject customeData;
    protected String json;

    public FragmentParams() {

        this.fragment_type = 0;
        this.list_refresh = false;
        this.list_type = "";
        this.content_type = "";
        this.appUser = false;
        this.customeData = new JSONObject();
        this.json = "";
    }



    private void stringToJson(){
        try {

            if(customeData==null)
            customeData = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void jsonToString(){
        json = customeData.toString();
    }

    public FragmentParams addCustomParam(String key,String obj){
        if(customeData==null)
            customeData = new JSONObject();
        try {
            customeData.put(key,obj);
            jsonToString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return this;
    }

    public String getCustomString(String key){
        try {
            stringToJson();
            return customeData.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public Double getCustomDouble(String key){
        try {
            stringToJson();
            return customeData.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public FragmentParams addCustomParam(String key,Double obj){
        if(customeData==null)
            customeData = new JSONObject();
        try {
            customeData.put(key,obj);
            jsonToString();
        } catch (JSONException e) {
            //e.printStackTrace();
        }

        return this;
    }

    public FragmentParams addCustomParam(String key,boolean obj){
        if(customeData==null)
            customeData = new JSONObject();
        try {
            customeData.put(key,obj);
            jsonToString();
        } catch (JSONException e) {
            //e.printStackTrace();
        }

        return this;
    }

    public boolean getCustomBoolean(String key){
        try {
            stringToJson();
            return customeData.getBoolean(key);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return false;
    }

    public FragmentParams addCustomParam(String key,int obj){
        if(customeData==null)
            customeData = new JSONObject();

        try {
            customeData.put(key,obj);
            jsonToString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return this;
    }

    public int getCustomInt(String key){
        try {
            stringToJson();
            return customeData.getInt(key);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return 0;
    }


    public static FragmentParams newInstance(){
        return new FragmentParams();
    }


    public int getFragment_type() {
        return fragment_type;
    }

    public FragmentParams setFragment_type(int fragment_type) {
        this.fragment_type = fragment_type;
        return this;
    }

    public boolean isList_refresh() {
        return list_refresh;
    }

    public FragmentParams setList_refresh(boolean list_refresh) {
        this.list_refresh = list_refresh;
        return  this;
    }

    public String getList_type() {
        return list_type;
    }

    public FragmentParams setList_type(String list_type) {
        this.list_type = list_type;
        return this;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }



    public boolean isAppUser() {
        return appUser;
    }

    public FragmentParams setAppUser(boolean appUser) {
        this.appUser = appUser;
        return this;
    }

    public JSONObject getCustomeData(){
        stringToJson();
        return customeData;
    }

    public FragmentParams setCustomeData(JSONObject customeData) {
        this.customeData = customeData;
        return this;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
