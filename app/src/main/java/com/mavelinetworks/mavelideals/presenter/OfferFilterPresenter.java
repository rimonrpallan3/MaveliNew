package com.mavelinetworks.mavelideals.presenter;

import com.mavelinetworks.mavelideals.classes.Filter;
import com.mavelinetworks.mavelideals.views.IOfferFilterView;

import java.util.List;

import io.realm.RealmList;

/**
 * Created by User on 06-Apr-18.
 */

public class OfferFilterPresenter implements IOfferFilterPresenter{

    IOfferFilterView iOfferFilterView;

    public OfferFilterPresenter(IOfferFilterView iOfferFilterView) {
        this.iOfferFilterView = iOfferFilterView;
    }

    @Override
    public void setAdapter(RealmList<Filter> filterList) {
        iOfferFilterView.setFilterList(filterList);
    }
}
