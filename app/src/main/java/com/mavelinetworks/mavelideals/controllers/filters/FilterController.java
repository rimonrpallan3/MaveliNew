package com.mavelinetworks.mavelideals.controllers.filters;

import com.mavelinetworks.mavelideals.classes.Filter;
import com.mavelinetworks.mavelideals.classes.Filter;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Droideve on 11/12/2017.
 */

public class FilterController {


    public static Filter findFilterById(int id){
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Filter.class).equalTo("id",id).findFirst();
    }

    public static List<Filter> findFiltersByStoreId(int id){
        Realm realm = Realm.getDefaultInstance();
        RealmResults result = realm.where(Filter.class).equalTo("store_id",id).findAllSorted("id", Sort.DESCENDING);
        List <Filter> array = new ArrayList<>();
        array.addAll(result.subList(0, result.size()));
        return  array;
    }

    public static void deleteAllFilters(int id){
        Realm realm = Realm.getDefaultInstance();
        final RealmResults result = realm.where(Filter.class).equalTo("store_id",id).findAllSorted("id", Sort.DESCENDING);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteAllFromRealm();
            }
        });
    }

    public static boolean insertFilters(final RealmList<Filter> list){

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Filter filter : list){
                    realm.copyToRealmOrUpdate(filter);
                }
            }
        });
        return  true;
    }


    public static void removeAll(){

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults result = realm.where(Filter.class).findAll();
                result.deleteAllFromRealm();

            }
        });

    }

}
