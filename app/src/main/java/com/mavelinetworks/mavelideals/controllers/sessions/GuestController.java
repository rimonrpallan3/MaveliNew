package com.mavelinetworks.mavelideals.controllers.sessions;

import com.mavelinetworks.mavelideals.classes.Guest;
import com.mavelinetworks.mavelideals.push_notification_firebase.FirebaseInstanceIDService;

import io.realm.Realm;

/**
 * Created by Droideve on 11/20/2017.
 */

public class GuestController {

    public static void saveGuest(final Guest guest){

        Realm  mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(guest);
            }
        });

    }

    public static Guest getGuest(){

        Realm  mRealm = Realm.getDefaultInstance();
        return mRealm.where(Guest.class).findFirst();

    }

    public static void clear(){

        Realm  mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Guest g = realm.where(Guest.class).findFirst();
                try {
                    g.deleteFromRealm();
                }catch (Exception e){

                }
            }
        });

    }


    public static void refresh(){

        Guest g = GuestController.getGuest();
        if(g.getLat()==0){
            FirebaseInstanceIDService.reloadToken();
        }
    }


    public static boolean isStored(){

       Realm  mRealm = Realm.getDefaultInstance();
        Guest guest = mRealm.where(Guest.class).findFirst();

        if(guest!=null && guest.isValid() && guest.isManaged()){
            return true;
        }

        return false;
    }


}
