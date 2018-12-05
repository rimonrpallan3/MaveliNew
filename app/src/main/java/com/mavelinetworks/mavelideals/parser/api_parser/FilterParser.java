package com.mavelinetworks.mavelideals.parser.api_parser;


import com.mavelinetworks.mavelideals.classes.Filter;
import com.mavelinetworks.mavelideals.classes.FilterChild;
import com.mavelinetworks.mavelideals.classes.FilterSubChild;
import com.mavelinetworks.mavelideals.parser.Parser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;


public class FilterParser extends Parser {

    public FilterParser(JSONObject json) {
        super(json);
    }

    public RealmList<Filter> getFilters(){

        RealmList<Filter> list = new RealmList<Filter>();

        try{

            JSONObject json_array = json.getJSONObject(Tags.RESULT);


            for (int i=0;i<json_array.length();i++){

                try {
                    JSONObject json_user = json_array.getJSONObject(i + "");

                    Filter filter = new Filter();

                    filter.setOfferTypeId(json_user.getString("id_offertype"));
                    filter.setNameFilt(json_user.getString("name"));
                    filter.setOfferParentId(json_user.getString("parent_id"));
                    filter.setStatus(json_user.getInt("status"));
                    filter.setOfferImage(json_user.getString("image"));
                    System.out.println("json_user.getJSONObject (child) : "+json_user.getJSONObject("child").toString());

                    JSONObject json_array2 =json_user.getJSONObject("child");
                    RealmList<FilterChild> filterChildren = new RealmList<>();
                    for (int j=0;j<json_array2.length();j++){
                        JSONObject json_user2 = json_array2.getJSONObject(j + "");
                        FilterChild filter2 = new FilterChild();
                        filter2.setOfferTypeId(json_user2.getString("id_offertype"));
                        filter2.setNameFilt(json_user2.getString("name"));
                        filter2.setOfferParentId(json_user2.getString("parent_id"));
                        filter2.setStatus(json_user2.getInt("status"));
                        filter2.setOfferImage(json_user2.getString("image"));
                        System.out.println("json_user.getJSONObject (child 2) : "+json_user2.getJSONObject("child").toString());

                        JSONObject json_array3 =json_user2.getJSONObject("child");
                        RealmList<FilterSubChild> filterSubChildren = new RealmList<>();
                        for (int k=0;k<json_array3.length();k++){
                            JSONObject json_user3 = json_array3.getJSONObject(k + "");
                            FilterSubChild filter3 = new FilterSubChild();
                            filter3.setOfferTypeId(json_user3.getString("id_offertype"));
                            filter3.setNameFilt(json_user3.getString("name"));
                            filter3.setOfferParentId(json_user3.getString("parent_id"));
                            filter3.setStatus(json_user3.getInt("status"));
                            filter3.setOfferImage(json_user3.getString("image"));
                            System.out.println("json_user.getJSONObject (child 2) : "+json_user3.getJSONObject("child").toString());
                            filterSubChildren.add(filter3);

                        }
                        filter2.setFilterSubChild(filterSubChildren);
                        filterChildren.add(filter2);
                        //filter.setFilterChild(filterChildren);
                    }
                    filter.setFilterChild(filterChildren);
                    /*FilterChildParser filterChildParser = new FilterChildParser(json_user.getJSONObject("child"));
                    filterChildren.add(filterChildParser.getFilterChild());
                    filter.setFilterChild(filterChildren);*/

                    list.add(filter);
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
