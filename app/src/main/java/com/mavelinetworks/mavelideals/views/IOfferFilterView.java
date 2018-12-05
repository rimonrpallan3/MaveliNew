package com.mavelinetworks.mavelideals.views;

import android.view.View;


import com.mavelinetworks.mavelideals.classes.Filter;
import com.mavelinetworks.mavelideals.classes.FilterChild;

import java.util.List;

import io.realm.RealmList;

/**
 * Created by User on 06-Apr-18.
 */

public interface IOfferFilterView {
    void setFilterList(List<Filter> filterList);
    void setFilterListRecycle(View view, int position, int num);
    void setChildList(RealmList<FilterChild> filterChildren);

    /*************************************************************

    /*Used in FilterAdapter ItemList*/
    void onItemCheck(String offerTypeCheckList);
    void onItemUncheck(String offerTypeCheckedLists);


    /****************************************************************/
    /*Used in FilterInnerAdapter*/
    void onItemInnerCheck(String offerTypeCheckList);
    void onItemInnerUncheck(String offerTypeCheckedLists);

}
