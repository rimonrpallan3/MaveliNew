package com.mavelinetworks.mavelideals.parser.api_parser;


import com.mavelinetworks.mavelideals.classes.OfferContent;
import com.mavelinetworks.mavelideals.parser.Parser;

import org.json.JSONException;
import org.json.JSONObject;


public class OfferContentParser extends Parser {

    public OfferContentParser(JSONObject json) {
        super(json);
    }

    public OfferContent getContent(int offerId) {

        OfferContent mOfferContent = new OfferContent();

        try {
            Float price = (float) json.getDouble("price");
            System.out.println("OfferContentParser price : " + price);
            mOfferContent.setId(offerId);
            mOfferContent.setCurrency(json.getString("currency"));
            mOfferContent.setDescription(json.getString("description"));
            mOfferContent.setPercent((float) json.getDouble("percent"));
            mOfferContent.setPrice((float) json.getDouble("price"));
            mOfferContent.setActualPrice((float) json.getDouble("actualPrice"));
            mOfferContent.setOfferPercent((float) json.getDouble("offerpercent"));

            return mOfferContent;

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }


}
