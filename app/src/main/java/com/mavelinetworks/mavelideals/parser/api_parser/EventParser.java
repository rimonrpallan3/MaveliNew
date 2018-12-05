package com.mavelinetworks.mavelideals.parser.api_parser;


import android.util.Log;

import com.mavelinetworks.mavelideals.classes.Event;
import com.mavelinetworks.mavelideals.classes.Images;
import com.mavelinetworks.mavelideals.parser.Parser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;


public class EventParser extends Parser {

    public EventParser(JSONObject json) {
        super(json);
    }

    public RealmList<Event> getEvents(){

        RealmList<Event> list = new RealmList<Event>();

        try{

            JSONObject json_array = json.getJSONObject(Tags.RESULT);
            if(APP_DEBUG) {  Log.e("JSONEventArray",json.toString()); }

            for (int i=0;i<json_array.length();i++){


                try {

                    JSONObject json_user = json_array.getJSONObject(i + "");

                    if(APP_DEBUG) { Log.e("EventUD",json_user+"");}
                    Event event  = new Event();
                    event.setId(json_user.getInt("id_event"));
                    event.setName(json_user.getString("name"));
                    event.setAddress(json_user.getString("address"));
                    event.setLat(json_user.getDouble("lat"));
                    event.setLng(json_user.getDouble("lng"));
                   // store.setType(json_user.getInt("type"));
                    event.setStatus(json_user.getInt("status"));


                    try {
                        event.setFeatured(json_user.getInt("featured"));
                    }catch (Exception e){}

                    try {
                        event.setDistance(json_user.getDouble("distance"));
                    }catch (Exception e){
                        event.setDistance(0.0);
                    }

                    try {
                        event.setStore_name(json_user.getString("store_name"));
                        event.setStore_id(json_user.getInt("store_id"));
                    }catch (Exception e){
                        e.printStackTrace();
                        event.setStore_name("");
                        event.setStore_id(0);
                    }

                    event.setTel(json_user.getString("tel"));
                    event.setDateB(json_user.getString("date_b"));
                    event.setDateE(json_user.getString("date_e"));
                    event.setDescription(json_user.getString("description"));
                    event.setWebSite(json_user.getString("website"));

                /*if(!json_user.isNull("detail") && json_user.has("detail"))
                    store.setDetail(json_user.getJSONObject("detail"));
                else
                    store.setDetail(new JSONObject(""));
                    */


                    String jsonValues= "";
                    try {

                        if (!json_user.isNull("images")) {
                            jsonValues = json_user.getJSONObject("images").toString();
                            JSONObject jsonObject = new JSONObject(jsonValues);
                            ImagesParser imgp = new ImagesParser(jsonObject);
                            event.setListImages(imgp.getImagesList());
                            event.setImageJson(json_user.toString());
                        }

                    }catch (JSONException jex){
                        event.setListImages(new RealmList<Images>());
                    }


                    if(APP_DEBUG) { Log.e("ParserEvent",event.getId()+"  "+event.getAddress()+"   "+event.getWebSite()); }


                    list.add(event);
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

        }catch (JSONException e){
            e.printStackTrace();
        }


        return list;
    }



}
