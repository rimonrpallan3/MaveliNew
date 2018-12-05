package com.mavelinetworks.mavelideals.controllers.sessions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mavelinetworks.mavelideals.AppController;
import com.mavelinetworks.mavelideals.activities.SplashActivity;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.classes.Session;
import com.mavelinetworks.mavelideals.classes.User;


import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Droideve on 8/1/2016.
 */

public class SessionsController {

    public static boolean userVerified=true;

    private static Realm mRealm=Realm.getDefaultInstance();
    private static Session session;
    private static final int aisession = 1;

    public static void restartApplication(Activity context){

        SessionsController.logOut();
        ActivityCompat.finishAffinity(context);
        context.startActivity(new Intent(context, SplashActivity.class));

    }


    public static boolean saveListUsers(final List<User> users){
        if (mRealm == null) {
            mRealm = Realm.getDefaultInstance();
        }

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for(int i=0;i<users.size();i++){
                    realm.copyToRealmOrUpdate(users.get(i));
                }
            }
        });

        return true;
    }


    public static boolean isLogged(){

        if(mRealm==null){
            mRealm = Realm.getDefaultInstance();
        }

        Session session = getSession();

        if(session!=null && session.isValid()){
            User user = session.getUser();
            if(user!=null && user.isValid()){
                return true;
            }
        }

        return false;
    }

    public static boolean isEmpty(){

        if(mRealm==null){
            mRealm = Realm.getDefaultInstance();
        }

        RealmResults<Session> result = mRealm.where(Session.class).findAll();

        if(result.size()==0){
            return true;
        }

        return false;
    }

    public static Session getSession(){

        try {

            if(mRealm==null){
                mRealm = Realm.getDefaultInstance();
            }

            session = mRealm.where(Session.class).equalTo("sessionId",aisession).findFirst();

            if(session==null){
                session = new Session();
                session.setSessionId(aisession);
            }

        }catch (Exception e){

            if(AppConfig.APP_DEBUG)
                e.printStackTrace();
        }


        return session;
    }


    public static void updateSession(final User user){
        if(SessionsController.isLogged()){
           Realm realm = Realm.getDefaultInstance();
           realm.executeTransaction(new Realm.Transaction() {
               @Override
               public void execute(Realm realm) {
                   realm.copyToRealmOrUpdate(user);
               }
           });
        }
    }


    public static Session createSession(final User user){

        if(mRealm==null){
            mRealm = Realm.getDefaultInstance();
        }

        //Guest guest = SessionsController.getSession().getGuest();
        Session session = getSession();

        if(AppConfig.APP_DEBUG)
        Log.e("loggedUser","_wait__");

        if(session!=null){

            session.setUser(user);
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(session);
            mRealm.commitTransaction();

            getLocalDatabase.setUserId(user.getId());

            if(AppConfig.APP_DEBUG)
                Log.e("loggedUser","_ok__");
        }



        //aisession++;
        return session;

    }

    public static void logOut(){

        if(mRealm==null){
            mRealm = Realm.getDefaultInstance();
        }

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.where(Session.class).findAll().deleteAllFromRealm();
                mRealm.where(User.class).findAll().deleteAllFromRealm();
            }
        });

        getLocalDatabase.setUserId(0);

        GuestController.clear();
    }



    public static class getLocalDatabase{


        public static boolean isLogged(){

            if(getUserId()>0)
                return true;

            return false;
        }


        public static int getUserId(){

            SharedPreferences sharedPref = AppController.getInstance().getSharedPreferences("usession", Context.MODE_PRIVATE);
            return sharedPref.getInt("user_id",0);
        }


        public static void setUserId(int id){

            SharedPreferences sharedPref = AppController.getInstance().getSharedPreferences("usession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("user_id",id);
            editor.commit();

        }

    }





}
