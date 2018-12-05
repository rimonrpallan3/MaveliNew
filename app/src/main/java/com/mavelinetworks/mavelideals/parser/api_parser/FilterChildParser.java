package com.mavelinetworks.mavelideals.parser.api_parser;


import com.mavelinetworks.mavelideals.classes.FilterChild;
import com.mavelinetworks.mavelideals.parser.Parser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;


public class FilterChildParser extends Parser {

    public FilterChildParser(JSONObject json) {
        super(json);
    }

    public RealmList<FilterChild> getOffers(){

        RealmList<FilterChild> list = new RealmList<>();

        try{
            System.out.println("json.getJSONObject(Tags.CHILD) : "+ json.toString());
            JSONObject json_array = json;

            for (int i=0;i<json_array.length();i++){

                try {
                    JSONObject json_user = json_array.getJSONObject(i + "");
                    FilterChild filterChild = new FilterChild();

                    filterChild.setOfferTypeId(json_user.getString("id_offertype"));
                    filterChild.setNameFilt(json_user.getString("name"));
                    filterChild.setOfferParentId(json_user.getString("parent_id"));
                    filterChild.setStatus(json_user.getInt("status"));
                    filterChild.setOfferImage(json_user.getString("image"));


                    /*FilterSubChildParser filterSubChildParser = new FilterSubChildParser(json_user);

                    filterChild.setFilterSubChild(filterSubChildParser.getFilterChild());*/


                    list.add(filterChild);
                }catch (JSONException e){
                    e.printStackTrace();
                    System.out.println("json.getJSONObject(Tags.CHILD) E : "+e.getMessage());
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }


        return list;
    }

    public FilterChild getFilterChild(){

        FilterChild filterChild = null;
        try {

            if (json.length()>0){

                filterChild = new FilterChild();

                filterChild.setOfferTypeId(json.getString("id_offertype"));
                filterChild.setNameFilt(json.getString("name"));
                filterChild.setOfferParentId(json.getString("parent_id"));
                filterChild.setStatus(json.getInt("status"));
                filterChild.setOfferImage(json.getString("image"));

                //filterChild.setJson(json);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        return filterChild;
    }




}
