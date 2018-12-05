package com.mavelinetworks.mavelideals.push_notification_firebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mavelinetworks.mavelideals.activities.EventActivity;
import com.mavelinetworks.mavelideals.activities.OfferDetailActivity;
import com.mavelinetworks.mavelideals.activities.StoreDetailActivity;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.controllers.CampagneController;
import com.mavelinetworks.mavelideals.dtmessenger.InComingDataParserSender;
import com.mavelinetworks.mavelideals.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;

/**
 * Created by Droideve on 11/7/2017.
 */

public class DTNotificationManager {

    private Map<String,String> data;
    private Context context;

    public Map<String,String> getData() {
        return data;
    }

    public void setData(HashMap data) {
        this.data = data;
    }

    public static class Tags{

        public final static String CAMPAIGN = "campaign";
        public final static String NOTIFICATION = "notification";

        public final static String TITLE = "title";
        public final static String SUB_TITLE = "sub-title";
        public final static String BODY = "body";
        public final static String ID = "id";
        public final static String IMAGE = "image";
        public final static String TYPE = "type";
        public final static String CAMPAGNE_ID = "cid";

        public final static String OFFER_PRICE = "price";
        public final static String OFFER_PERCENT = "percent";
        public final static String OFFER_DESCRIPTION = "description";
        public final static String OFFER_ATTACHMENT = "attachment";
        public final static String OFFER_CURRENCY = "currency";
        public final static String OFFER_STORE_NAME = "store_name";
    }

    public DTNotificationManager(Context context, Map<String,String> data) {
        this.data = data;
        this.context = context;
    }


    public void push(){

        String incomingData="";
        /*
            parse incoming data to client message
         */

        if(data.containsKey("type") && data.get("type").equals(Tags.NOTIFICATION)
         && AppConfig.CHAT_WITH_FIREBASE && AppConfig.ENABLE_CHAT){

            incomingData = data.get("data");

            try {
                InComingDataParserSender.parseAndSend(context,new JSONObject(incomingData));
            } catch (JSONException e) {
                if(AppConfig.APP_DEBUG)
                            e.printStackTrace();
            }

            if(AppConfig.APP_DEBUG)
                Log.e("DTNotificationManager","Something is wrong!");



            return;
        }else if(data.containsKey("type") && data.get("type").equals(Tags.CAMPAIGN) ){

            /*
                parse incoming data to client compaign
             */
            incomingData = data.get("data");
            parse(incomingData);
        }

    }

    public void parse(String incomingData){

        Parser mParser = null;
        try {
            mParser = new Parser(incomingData);
            mParser.parse();
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        if(AppConfig.APP_DEBUG)
            Log.e(FirebaseMessagingService.TAG,data.toString());

        try {
            CampagneController.markReceive(mParser.getCid());
        }catch (Exception e){
        }

        if(mParser.getType().equals(Parser.OFFER)){
            if(AppConfig.APP_DEBUG)
                Log.e(FirebaseMessagingService.TAG,mParser.getBody().toString());



            String bigImageUrl = mParser.getBody().getAttachement();
            Bitmap icon = getBitmapfromUrl(bigImageUrl);

            Map<String,String> data =new HashMap<>();

            data.put("id", String.valueOf(mParser.getId()));
            data.put("cid", String.valueOf(mParser.getCid()));

            if (APP_DEBUG) {
                Log.e("CMarkViewClicked", data.toString());
            }


            boolean notif_offers = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("notif_offers", true);

            if(notif_offers)
                NotificationUtils.sendNotification(context,mParser.getTitle(),mParser.getBody().getStoreName(),icon,bigImageUrl, OfferDetailActivity.class,data);

        }else if(mParser.getType().equals(Parser.STORE)){

            if(AppConfig.APP_DEBUG)
                Log.e(FirebaseMessagingService.TAG,mParser.toString());

            Bitmap icon = getBitmapfromUrl(mParser.getImage());
            Map<String,String> data =new HashMap<>();

            data.put("id", String.valueOf(mParser.getId()));
            data.put("cid", String.valueOf(mParser.getCid()));

            if (APP_DEBUG) {
                Log.e("CMarkViewClicked", data.toString());
            }


            boolean notif_nearby_stores = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("notif_nearby_stores", true);


            if(notif_nearby_stores)
                NotificationUtils.sendNotification(context,mParser.getTitle(),mParser.getSub_title(),icon,null, StoreDetailActivity.class,data);

        }else if(mParser.getType().equals(Parser.EVENT)){

            if(AppConfig.APP_DEBUG)
                Log.e(FirebaseMessagingService.TAG,mParser.toString());


            Bitmap icon = getBitmapfromUrl(mParser.getImage());
            Map<String,String> data =new HashMap<>();

            data.put("id", String.valueOf(mParser.getId()));
            data.put("cid", String.valueOf(mParser.getCid()));

            if (APP_DEBUG) {
                Log.e("CMarkViewClicked", data.toString());
            }

            boolean notif_nearby_events = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("notif_nearby_events", true);

            if(notif_nearby_events)
                NotificationUtils.sendNotification(context,mParser.getTitle(),mParser.getSub_title(),icon,null, EventActivity.class,data);

        }

    }



    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if(AppConfig.APP_DEBUG)
                e.printStackTrace();
            return null;

        }
    }


    public static class Parser{

        public static String STORE="store";
        public static String  OFFER="offer";
        public static String EVENT="event";
        public static String MESSAGE="message";

        private JSONObject data;
        private String type;
        private int id;
        private String title;
        private String sub_title;
        private String image;
        private BodyParser body;
        private int  cid;

        public String getImage() {
            return image;
        }

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Parser(String data) throws JSONException {
            this.data  = new JSONObject(data);
        }

        public void parse() throws JSONException {

            id = data.getInt(Tags.ID);
            type = data.getString(Tags.TYPE);
            title = data.getString(Tags.TITLE);
            sub_title = data.getString(Tags.SUB_TITLE);
            String body = data.getString(Tags.BODY);
            image =  data.getString(Tags.IMAGE);
            cid = data.getInt(Tags.CAMPAGNE_ID);

            if(!body.equals("") && type.equals(Parser.OFFER)){
                this.body = new BodyParser(body);
                this.body.parse();
            }

        }

        public String getSub_title() {
            return sub_title;
        }

        public void setSub_title(String sub_title) {
            this.sub_title = sub_title;
        }

        public JSONObject getData() {
            return data;
        }

        public void setData(JSONObject data) {
            this.data = data;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public BodyParser getBody() {
            return body;
        }


        @Override
        public String toString() {
            return "Parser{" +
                    "type='" + type + '\'' +
                    ", id=" + id +
                    ", title='" + title + '\'' +
                    ", body='" + body + '\'' +
                    ", image='" + image + '\'' +
                    '}';
        }


        class BodyParser{



            private double price;
            private double percent;
            private String currency;
            private String description;
            private String attachement;
            private String storeName;
            private JSONObject json;


            public BodyParser(String body){
                try {
                    json = new JSONObject(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void parse(){
                if(json!=null){
                    try {
                        price = json.getDouble(Tags.OFFER_PRICE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        percent = json.getDouble(Tags.OFFER_PERCENT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } try {
                        currency = json.getString(Tags.OFFER_CURRENCY);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        description = json.getString(Tags.OFFER_DESCRIPTION);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        attachement = json.getString(Tags.OFFER_ATTACHMENT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        storeName = json.getString(Tags.OFFER_STORE_NAME);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public double getPercent() {
                return percent;
            }

            public void setPercent(double percent) {
                this.percent = percent;
            }

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }


            public String getAttachement() {
                return attachement;
            }

            public void setAttachement(String attachement) {
                this.attachement = attachement;
            }

            public String getStoreName() {
                return storeName;
            }

            public void setStoreName(String storeName) {
                this.storeName = storeName;
            }

            @Override
            public String toString() {
                return "BodyParser{" +
                        "price=" + price +
                        ", percent=" + percent +
                        ", currency='" + currency + '\'' +
                        ", description='" + description + '\'' +
                        ", attachement='" + attachement + '\'' +
                        ", storeName='" + storeName + '\'' +
                        '}';
            }
        }

    }




}
