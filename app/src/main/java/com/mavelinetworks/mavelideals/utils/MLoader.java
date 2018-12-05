package com.mavelinetworks.mavelideals.utils;

import android.content.Context;

import com.mavelinetworks.mavelideals.classes.Category;
import com.mavelinetworks.mavelideals.classes.Event;
import com.mavelinetworks.mavelideals.classes.Store;
import com.mavelinetworks.mavelideals.parser.api_parser.CategoryParser;
import com.mavelinetworks.mavelideals.parser.api_parser.EventParser;
import com.mavelinetworks.mavelideals.parser.api_parser.StoreParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Droideve on 3/15/2017.
 */

public class MLoader {


    public static List<Category> parseCategoriesFromAssets(Context context){

        JSONObject json = getDataFromAssets(context);
        try {
            JSONObject jsonByType = json.getJSONObject("categories");
            CategoryParser mCategoryParser = new CategoryParser(jsonByType);
            return mCategoryParser.getCategories();
        }catch (Exception e){

        }

        return  new ArrayList<>();
    }

    public static List<Event> parseEventsFromAssets(Context context){
        JSONObject json = getDataFromAssets(context);

        try {
            JSONObject jsonByType = json.getJSONObject("events");
            EventParser mEventParser = new EventParser(jsonByType);
            return mEventParser.getEvents();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  new ArrayList<>();
    }

    public static List<Store> parseStoresFromAssets(Context context,int type){
        JSONObject json = getDataFromAssets(context);

        try {
            JSONObject jsonByType = json.getJSONObject("_"+type);
            StoreParser mStoreParser = new StoreParser(jsonByType);
            return mStoreParser.getStore();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  new ArrayList<>();
    }

    private static JSONObject getDataFromAssets(Context context){
        String json = null;
        try {
            InputStream is = context.getAssets().open("data/backup.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            return new JSONObject(json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
