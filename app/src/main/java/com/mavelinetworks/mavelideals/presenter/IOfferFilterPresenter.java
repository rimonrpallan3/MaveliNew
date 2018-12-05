package com.mavelinetworks.mavelideals.presenter;

import com.mavelinetworks.mavelideals.classes.Filter;


import java.util.List;

import io.realm.RealmList;

/**
 * Created by User on 06-Apr-18.
 */

public interface IOfferFilterPresenter {
    void setAdapter(RealmList<Filter> filterList);
}
