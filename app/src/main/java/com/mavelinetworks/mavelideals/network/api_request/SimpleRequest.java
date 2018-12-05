package com.mavelinetworks.mavelideals.network.api_request;


import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.mavelinetworks.mavelideals.AppController;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.AppContext;
import com.mavelinetworks.mavelideals.utils.Translator;

import java.util.HashMap;
import java.util.Map;


public class SimpleRequest extends StringRequest {

    public static int TIME_OUT = 40000;

    public SimpleRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> customHeader = new HashMap<>();

        try {

            customHeader = AppController.getTokens();
            customHeader.put("Language", Translator.DefaultLang);
            customHeader.put("Debug", String.valueOf(AppContext.DEBUG));
            customHeader.put("Api-key-android", AppConfig.ANDROID_API_KEY);
            /*System.out.println("Translator.DefaultLang : "+Translator.DefaultLang);
            System.out.println("Debug : "+String.valueOf(AppContext.DEBUG));
            System.out.println("Api-key-android : "+AppConfig.ANDROID_API_KEY);
            System.out.println("getHeaders : "+customHeader.toString());*/

            if(AppConfig.APP_DEBUG)
                Log.e("getHeaders",customHeader.toString());

        }catch (Exception e){
            e.printStackTrace();
        }


        return customHeader;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {


        return super.getParams();
    }
}