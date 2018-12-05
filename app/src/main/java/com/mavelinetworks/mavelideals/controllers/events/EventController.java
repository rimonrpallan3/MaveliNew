package com.mavelinetworks.mavelideals.controllers.events;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.EventActivity;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.classes.Event;
import com.mavelinetworks.mavelideals.classes.EventNotification;
import com.mavelinetworks.mavelideals.utils.ImageUtils;
import com.mavelinetworks.mavelideals.utils.NotificationUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Droideve on 7/12/2017.
 */

public class EventController {


    public static void removeAll(){

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults result = realm.where(Event.class).equalTo("liked",false).findAll();
                result.deleteAllFromRealm();
            }
        });

    }

    private static boolean isTimeToNotify(String date){

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        input.setTimeZone(TimeZone.getTimeZone("UTC"));


        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
        output.setTimeZone(TimeZone.getDefault());

        Date eventDate = null;
        try {

            //event date
            eventDate = input.parse(date);
            String eventStrDate = output.format(eventDate);

            //current date

            SimpleDateFormat outputc = new SimpleDateFormat("yyyy-MM-dd");
            outputc.setTimeZone(TimeZone.getDefault());

            if(AppConfig.APP_DEBUG)
                Log.e("notifyIfExist","eventDate: "+eventStrDate);


            Calendar timerc = Calendar.getInstance();
            String currentStrDate =  outputc.format(timerc.getTime());

            if(AppConfig.APP_DEBUG)
                Log.e("notifyIfExist","clientDate: "+currentStrDate);


            if(currentStrDate.equals(eventStrDate)){
                return  true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return false;

    }


    public static void notifyIfExist(final Context context){

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<EventNotification> mEventNotificationList = realm.where(EventNotification.class).findAll();

                if(mEventNotificationList!=null && mEventNotificationList.isValid()){

                    if(mEventNotificationList.size()>0){

                        for (int i=0;i<mEventNotificationList.size();i++){

                            Event event = mEventNotificationList.get(i).getEvent();
                            if(event!=null) {

                                if (isTimeToNotify(event.getDateB()) && event.isLiked()) {

                                    if (AppConfig.APP_DEBUG)
                                        Log.e("notifyIfExist", "true");

                                    String url = "";
                                    try {
                                        url = event.getListImages().get(0).getUrl200_200();
                                    } catch (Exception e) {

                                    }
                                    Bitmap icon = ImageUtils.getBitmapfromUrl(url);

                                    Map<String, String> data = new HashMap<>();
                                    data.put("id", String.valueOf(event.getId()));


                                    NotificationUtils.sendNotification(
                                            context,
                                            context.getString(R.string.event_coming_soon),
                                            event.getName(),
                                            icon,
                                            EventActivity.class,
                                            data
                                    );

                                    mEventNotificationList.get(i).deleteFromRealm();

                                }

                            }else {
                                mEventNotificationList.deleteAllFromRealm();
                            }

                        }
                    }

                }
            }
        });

    }

    public static void addEventToNotify(final Event event){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EventNotification  mEventNotification = new EventNotification();
                if(event.isLiked()){
                    mEventNotification.setId(event.getId());
                    mEventNotification.setEvent(event);
                    realm.copyToRealmOrUpdate(mEventNotification);
                }



            }
        });

    }

    public static boolean insertEvents(final RealmList<Event> list){

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Event str : list){
                    Event hasStore = realm.where(Event.class).equalTo("id",str.getId()).findFirst();
                    if(hasStore!=null && hasStore.isLoaded()){
                        str.setLiked(hasStore.isLiked());
                        realm.copyToRealmOrUpdate(str);
                    }else {
                        realm.copyToRealmOrUpdate(str);
                    }
                }

            }
        });
        return  true;
    }


    public static boolean isEventLiked(int id){

        Realm realm = Realm.getDefaultInstance();
        Event event = realm.where(Event.class).equalTo("id",id).equalTo("liked",true).findFirst();

        if(event!=null)
            return true;

        return false;
    }

    public static boolean doLike(final int id){

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Event event = realm.where(Event.class).equalTo("id",id).findFirst();
                if(event!=null){
                    event.setLiked(true);
                    realm.copyToRealm(event);
                }
            }
        });

        return false;
    }

    public static boolean doDisLike(final int id){

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Event event = realm.where(Event.class).equalTo("id",id).findFirst();
                event.setLiked(false);
                realm.copyToRealm(event);
            }
        });


        return false;
    }

    public static RealmList<Event> list(){

        Realm realm = Realm.getDefaultInstance();
        RealmResults result = realm.where(Event.class).findAll();

        RealmList <Event> results = new RealmList<Event>();
        results.addAll(result.subList(0, result.size()));

        return  results;
    }


    public static Event findEventById(int id){

        Realm realm = Realm.getDefaultInstance();
        Event result = realm.where(Event.class).equalTo("id",id).findFirst();
        return  result;
    }


    public static List<Event> getLikeEventsAsArrayList(){

        Realm realm = Realm.getDefaultInstance();
        RealmResults result = realm.where(Event.class).equalTo("liked",true).findAll();

        List <Event> array = new ArrayList<>();

        array.addAll(result.subList(0, result.size()));
        return  array;
    }


    public static List<Event> getArrayList() {

        List <Event> results = new ArrayList<>();
        RealmList<Event> listCats = EventController.list();

        results.addAll(listCats.subList(0, listCats.size()));
        return results;
    }
}
