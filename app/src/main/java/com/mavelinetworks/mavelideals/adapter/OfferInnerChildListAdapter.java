package com.mavelinetworks.mavelideals.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.RetorHelper.Child;
import com.mavelinetworks.mavelideals.RetorHelper.MainOffers;
import com.mavelinetworks.mavelideals.classes.FilterChild;
import com.mavelinetworks.mavelideals.classes.FilterSubChild;
import com.mavelinetworks.mavelideals.classes.FilterSubChildSub;
import com.mavelinetworks.mavelideals.views.IOfferFilterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by rimon on 05-04-2018.
 */

public class OfferInnerChildListAdapter extends RecyclerView.Adapter<OfferInnerChildListAdapter.MyViewHolder> {


    IOfferFilterView iOfferFilterView;
    List<MainOffers> mainOffersList;
    List<MainOffers> childMainOffersList;
    RealmList<FilterSubChildSub> filterSubChildSubs;
    RealmList<FilterSubChild> filterSubChildren;

    public OfferInnerChildListAdapter(RealmList<FilterSubChild> filterSubChildren, IOfferFilterView iOfferFilterView) {
        this.filterSubChildren = filterSubChildren;
        this.iOfferFilterView = iOfferFilterView;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item_list_inner_two_content, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FilterSubChild filterSubChild = filterSubChildren.get(position);
        holder.filterSubDetailsTitle.setText(filterSubChild.getNameFilt());
        if (filterSubChildren.size() > 0) {
            filterSubChildSubs = filterSubChild.getFilterSubChildSubs();


            if (!filterSubChild.getChecked())
                iOfferFilterView.onItemInnerUncheck(filterSubChild.getOfferTypeId());

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //holder.checkBoxContentFileUpload.setChecked(!holder.checkBoxContentFileUpload.isChecked());
                    if (holder.checkBox.isChecked()) {
                        System.out.println(filterSubChild.getOfferTypeId());
                        iOfferFilterView.onItemInnerCheck(filterSubChild.getOfferTypeId());
                    } else {
                        System.out.println(filterSubChild.getOfferTypeId());
                        iOfferFilterView.onItemInnerUncheck(filterSubChild.getOfferTypeId());
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return filterSubChildren.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView filterSubDetailsTitle;
        public TextView selectedDetailListTxt;
        public LinearLayout selectedDetailListLayout;
        public LinearLayout itemFilterClick;
        public CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            filterSubDetailsTitle = (TextView) view.findViewById(R.id.filterSubChildInnerDetailsTitle);
            selectedDetailListTxt = (TextView) view.findViewById(R.id.selectedChildInnerDetailListTxt);
            selectedDetailListLayout = (LinearLayout) view.findViewById(R.id.selectedChildInnerDetailListLayout);
            itemFilterClick = (LinearLayout) view.findViewById(R.id.itemChildInnerFilterClick);
            checkBox = (CheckBox) view.findViewById(R.id.checkInnerBox);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                /*if(clickListener!=null){
                    clickListener.itemClickedFilter(v,getPosition(),filterlists.getNumFilt());
                }*/
        }
    }
}

