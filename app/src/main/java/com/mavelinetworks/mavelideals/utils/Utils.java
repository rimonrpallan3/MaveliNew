package com.mavelinetworks.mavelideals.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.unbescape.html.HtmlEscape;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Droideve on 09/06/2015.
 */
public class Utils {

    public static boolean isValidURL(String url) {

        URL u = null;

        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }


    public static Drawable changeDrawableIconMap(Context context,int resId){

        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), resId,null);
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
        drawable.setColorFilter(ResourcesCompat.getColor(context.getResources(),R.color.colorPrimary,null),mode);

        return drawable;
    }

    public static void enableEvent(){

//        for(int i=0;i<Constances.initConfig.ListCats.size();i++){
//
//            Category tab = Constances.initConfig.ListCats.get(i);
//            if(AppConfig.ENABLE_EVENTS==false){
//                if(tab.getType() == Constances.initConfig.Tabs.EVENTS){
//                    Constances.initConfig.ListCats.remove(i);
//                    Constances.initConfig.Numboftabs = Constances.initConfig.ListCats.size();
//                }
//
//            }
//        }
    }


    private static String SP_NAME = "q2sUn5aZDmL56";
    private static String SP_NAME_KEY = "q2sUn5aZDmL56tOoKeN";

    public static void setToken(Context context,String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(SP_NAME_KEY, token);
        edit.apply();
    }

    public static String getToken(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(SP_NAME_KEY,"");
    }

    public static void setFontBold(Context context, View rootView, String s) {

    }


    public static class Params{

        private Bundle params;

        public Params(){
            params = new Bundle();
        }

        public void setParam(String key,Double value){
            try{
                params.putString(key,value+"");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void setParam(String key,String value){
            try{
                params.putString(key,value);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void setParam(String key,int value){
            try{
                params.putString(key,value+"");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public String getParam(String key){

            try{
                if(!params.isEmpty()){
                    return params.getString(key);
                }

            }catch (Exception e) {}
            return "";
        }


        public double getDouble(String key){

            try{
                if(!params.isEmpty()){
                    return params.getDouble(key);
                }

            }catch (Exception e) {}
            return 0.0;
        }


        public int getInt(String key){

            try{
                if(!params.isEmpty()){
                    return params.getInt(key);
                }

            }catch (Exception e) {}
            return 0;
        }



        @Override
        public String toString() {

            return params.toString();
        }
    }



    public static final String DEFAULT_VALUE = "N/A";
    public static String Escape(String str){
        return str;
    }
    public static String Unescape(String str){
        return  HtmlEscape.unescapeHtml(HtmlEscape.unescapeHtml(str));
    }


    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-mm-dd";
        String outputPattern = "dd/mm/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getDistanceBy(double meters){

        String FINAL_VALUE = "M";
        if(meters>0){

            if(meters>1000){

                FINAL_VALUE = "Km";

            }
        }else{
            FINAL_VALUE = "";
        }

        return FINAL_VALUE;

    }

    public static String preparDistance(double meters){

        String FINAL_VALUE = DEFAULT_VALUE+" ";

            if(meters>0){

                if(meters>1000){
                    double kilometers = 0.0;
                    kilometers = meters * 0.001;

                    DecimalFormat decim = new DecimalFormat("#.##");
                    FINAL_VALUE = decim.format(kilometers)+"";

                }else if(meters<1000){
                    FINAL_VALUE = ((int)meters)+"";
                }

            }


        return FINAL_VALUE;
    }


    public static void setFontBold(Context context,TextView view){

        Typeface tf = Typeface.createFromAsset(context.getAssets(), Constances.Fonts.BOLD);
        view.setTypeface(tf);

    }



    public static void setFont(Context context,TextView view,String name){

        Typeface tf = Typeface.createFromAsset(context.getAssets(), Constances.Fonts.REGULAR);
       view.setTypeface(tf);

    }

    public static boolean isValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
