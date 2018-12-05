package com.mavelinetworks.mavelideals.parser.api_parser;


import com.mavelinetworks.mavelideals.classes.FilterChild;
import com.mavelinetworks.mavelideals.classes.FilterSubChild;
import com.mavelinetworks.mavelideals.parser.Parser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;


public class FilterSubChildParser extends Parser {

    public FilterSubChildParser(JSONObject json) {
        super(json);
    }

    public RealmList<FilterSubChild> getOffers(){

        RealmList<FilterSubChild> list = new RealmList<FilterSubChild>();

        try{

            JSONObject json_array = json.getJSONObject(Tags.RESULT);

            for (int i=0;i<json_array.length();i++){

                try {
                    JSONObject json_user = json_array.getJSONObject(i + "");
                    FilterSubChild filterSubChild = new FilterSubChild();

                    filterSubChild.setOfferTypeId(json_user.getString("id_offertype"));
                    filterSubChild.setNameFilt(json_user.getString("name"));
                    filterSubChild.setOfferParentId(json_user.getString("parent_id"));
                    filterSubChild.setStatus(json_user.getInt("status"));
                    filterSubChild.setOfferImage(json_user.getString("image"));


                    /*FilterSubChildParser filterChildParser = new FilterSubChildParser(new JSONObject(
                            json_user.getString("child")
                    ));

                    filterSubChild.setFilterChild(filterChildParser.getFilterChild());*/


                    list.add(filterSubChild);
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

        }catch (JSONException e){
            e.printStackTrace();
        }


        return list;
    }

    public FilterSubChild getFilterChild(){

        FilterSubChild filterSubChild = null;
        try {

            if (json.length()>0){

                filterSubChild = new FilterSubChild();

                filterSubChild.setOfferTypeId(json.getString("id_offertype"));
                filterSubChild.setNameFilt(json.getString("name"));
                filterSubChild.setOfferParentId(json.getString("parent_id"));
                filterSubChild.setStatus(json.getInt("status"));
                filterSubChild.setOfferImage(json.getString("image"));

                //filterSubChild.setJson(json);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        return filterSubChild;
    }



}
