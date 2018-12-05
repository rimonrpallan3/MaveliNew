package com.mavelinetworks.mavelideals.controllers.stores;

import android.util.Log;

import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.classes.SavedStores;
import com.mavelinetworks.mavelideals.classes.Store;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Droideve on 7/12/2017.
 */

public class StoreController {

    public static boolean insertStores(final RealmList<Store> list){

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Store str : list){
                    Store hasStore = realm.where(Store.class).equalTo("id",str.getId()).findFirst();

                    if(hasStore!=null && hasStore.isLoaded()){
                        str.setSaved(hasStore.isSaved());
                        realm.copyToRealmOrUpdate(hasStore);
                    }else {
                        realm.copyToRealmOrUpdate(str);
                    }
                }

            }
        });
        return  true;
    }

    public static Store findStoreById(int id){

        Realm realm = Realm.getDefaultInstance();
        Store obj = realm.where(Store.class).equalTo("id",id).findFirst();

        return obj;
    }

    public static Store findStoreByUserId(int id){

        Realm realm = Realm.getDefaultInstance();
        Store obj = realm.where(Store.class).equalTo("id",id).findFirst();

        return obj;
    }

    public static Store getStore(int id){

        Realm realm = Realm.getDefaultInstance();
        Store obj = realm.where(Store.class).equalTo("id",id).findFirst();

        return obj;
    }



    public static boolean doSave(int id){

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Store obj = realm.where(Store.class).equalTo("id",id).findFirst();
        obj.setSaved(true);
        realm.copyToRealmOrUpdate(obj);
        realm.commitTransaction();


        SavedStores saved_stores = realm.where(SavedStores.class).findFirst();

        if(saved_stores==null){
            saved_stores = new SavedStores();
            saved_stores.setId(1);
        }

        if(saved_stores!=null){
            if(!saved_stores.isExist(id)){
                realm.beginTransaction();
                saved_stores.getListID().add(id);
                realm.copyToRealmOrUpdate(saved_stores);
                realm.commitTransaction();
            }
        }


        return false;
    }


    public static boolean isSaved(int id){

        Realm realm = Realm.getDefaultInstance();

        SavedStores saved_stores = realm.where(SavedStores.class).findFirst();

        if(saved_stores==null){
            saved_stores = new SavedStores();
            saved_stores.setId(1);
        }

        if(saved_stores!=null){
            if(saved_stores.isExist(id)){
                return true;
            }
        }

        return false;
    }


    public static String getSavedStores(){

        Realm realm = Realm.getDefaultInstance();

        SavedStores saved_stores = realm.where(SavedStores.class).findFirst();

        if(saved_stores==null){
            saved_stores = new SavedStores();
        }

        JSONArray ids = new JSONArray();

        if(saved_stores!=null && saved_stores.getListID()!=null)
            for (int i=0;i<saved_stores.getListID().size();i++){
                ids.put(saved_stores.getListID().get(i));
            }

        String result = null;
        if(ids.length()==0){
            result = null;
        }else
            result = ids.toString();

        return result;
    }

    public static void parseSavedStoresID(String my_saved_stores){

        Realm realm = Realm.getDefaultInstance();
        SavedStores saved_stores = realm.where(SavedStores.class).findFirst();

        if(saved_stores==null){
            saved_stores = new SavedStores();
            saved_stores.setId(1);
        }

        try {

            JSONArray json = new JSONArray(my_saved_stores);
            if(AppConfig.APP_DEBUG)
                Log.e("parseSavedStoresID-2",json.toString());

            for(int i=0;i<json.length();i++){
                saved_stores.getListID().add(json.getInt(i));
            }

            if(AppConfig.APP_DEBUG)
                Log.e("parseSavedStoresID-3",json.toString());

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(saved_stores);
            realm.commitTransaction();
        } catch (JSONException e) {
            if(AppConfig.APP_DEBUG)
                e.printStackTrace();
        }

    }

    public static String getSavedStoresAsString(){
        Realm realm = Realm.getDefaultInstance();

        SavedStores saved_stores = realm.where(SavedStores.class).findFirst();

        if(saved_stores==null){
            saved_stores = new SavedStores();
            saved_stores.setId(1);
        }

        String ids = "";

        if(saved_stores!=null && saved_stores.getListID()!=null)
            for (int i=0;i<saved_stores.getListID().size();i++){
                ids = ids+","+saved_stores.getListID().get(i);
            }


        return ids;
    }

    public static List<Store> getSavedStoresAsArrayList(){

        Realm realm = Realm.getDefaultInstance();
        RealmResults result = realm.where(Store.class).equalTo("saved",true).findAll();

        List <Store> array = new ArrayList<>();

        array.addAll(result.subList(0, result.size()));
        return  array;
    }

    public static boolean doDelete(int id){

        Realm realm = Realm.getDefaultInstance();
        final Store obj = realm.where(Store.class).equalTo("id",id).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                obj.setSaved(false);
                realm.copyToRealm(obj);
            }
        });


        SavedStores saved_stores = realm.where(SavedStores.class).findFirst();

        if(saved_stores==null){
            saved_stores = new SavedStores();
            saved_stores.setId(1);
        }

        if(saved_stores!=null){
            if(saved_stores.isExist(id)){
                realm.beginTransaction();
                saved_stores.delete(id);
                realm.copyToRealmOrUpdate(saved_stores);
                realm.commitTransaction();
            }
        }


        return false;
    }

    public static void removeAll(){

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Store> result = realm.where(Store.class).findAll();
                for(Store o : result){
                    o.deleteFromRealm();
                }
            }
        });

    }

}
