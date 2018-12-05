package com.mavelinetworks.mavelideals.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.RetorHelper.Child;
import com.mavelinetworks.mavelideals.RetorHelper.MainOffers;
import com.mavelinetworks.mavelideals.classes.Filter;
import com.mavelinetworks.mavelideals.classes.FilterChild;
import com.mavelinetworks.mavelideals.classes.FilterSubChild;
import com.mavelinetworks.mavelideals.classes.FilterSubChildSub;
import com.mavelinetworks.mavelideals.views.IOfferFilterView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by rimon on 05-04-2018.
 */

public class OfferChildListAdapter extends RecyclerView.Adapter<OfferChildListAdapter.MyViewHolder> {

    IOfferFilterView iOfferFilterView;
    List<MainOffers> mainOffersList;
    List<MainOffers> childMainOffersList;
    List<MainOffers> childInnerMainOffersList;
    OfferInnerChildListAdapter offerInnerChildListAdapter;
    Activity activity;
    boolean isPressed = false;
    RealmList<FilterChild> filterChildren;

    public OfferChildListAdapter(RealmList<FilterChild> filterChildren, IOfferFilterView iOfferFilterView, Activity activity) {
        this.filterChildren = filterChildren;
        this.iOfferFilterView = iOfferFilterView;
        this.activity =activity;
        System.out.println("-----------OfferChildListAdapter onBindViewHolder childMainOffersList const");
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item_list_inner_content, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.itemFilterClick.getLayoutParams();
        if (filterChildren.size() == (position + 1)) {
            mlp.setMargins(0, 0, 0, (int) activity.getResources().getDimension(R.dimen._80));
        } else {
            mlp.setMargins(0, 0, 0, 0);
        }

        final Drawable upDrawable = new IconicsDrawable(activity)
                .icon(CommunityMaterial.Icon.cmd_menu_up)
                .color(ResourcesCompat.getColor(activity.getResources(),R.color.iconColor,null))
                .sizeDp(16);
        final Drawable downDrawable = new IconicsDrawable(activity)
                .icon(CommunityMaterial.Icon.cmd_menu_down)
                .color(ResourcesCompat.getColor(activity.getResources(),R.color.iconColor,null))
                .sizeDp(16);
        final Drawable downDisDrawable = new IconicsDrawable(activity)
                .icon(CommunityMaterial.Icon.cmd_menu_down)
                .color(ResourcesCompat.getColor(activity.getResources(),R.color.iconDisableColor,null))
                .sizeDp(16);


        holder.filterSubDetailsImgView.setImageDrawable(downDrawable);

        final FilterChild filterChild = filterChildren.get(position);
        final RealmList<FilterSubChild> filterSubChild = filterChild.getFilterSubChild();
        holder.filterSubDetailsTitle.setText(filterChild.getNameFilt());
        System.out.println("-----------OfferChildListAdapter onBindViewHolder childMainOffersList OfferList");
        if (filterChildren.size() > 0) {


            for(int i=0;i<filterSubChild.size();i++){
                final FilterSubChild filterSubChild1 = filterSubChild.get(i);
                RealmList<FilterSubChildSub> filterSubChildSubs = filterSubChild1.getFilterSubChildSubs();
                if (filterSubChild != null&&filterSubChild.size()>0) {
                    holder.filterSubDetailsImgView.setVisibility(View.VISIBLE);
                } else {
                    holder.filterSubDetailsImgView.setVisibility(View.GONE);
                }
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //holder.checkBoxContentFileUpload.setChecked(!holder.checkBoxContentFileUpload.isChecked());
                    if ( holder.checkBox.isChecked()) {
                        System.out.println(filterChild.getOfferTypeId());
                        iOfferFilterView.onItemInnerCheck(filterChild.getOfferTypeId());
                            holder.filterSubDetailsImgView.setImageDrawable(downDisDrawable);
                            holder.sublist.setVisibility(View.GONE);
                            holder.itemFilterClick.setEnabled(false);


                    } else {
                        System.out.println(filterChild.getOfferTypeId()+", filterChildren.size() : "+filterChildren.size());
                        iOfferFilterView.onItemInnerUncheck(filterChild.getOfferTypeId());
                        holder.filterSubDetailsImgView.setImageDrawable(downDrawable);
                        holder.itemFilterClick.setEnabled(true);
                    }
                }
            });
            offerInnerChildListAdapter = new OfferInnerChildListAdapter(filterSubChild, iOfferFilterView);
            holder.sublist.setLayoutManager(new LinearLayoutManager(activity));
            holder.sublist.setItemAnimator(new DefaultItemAnimator());
            holder.sublist.setAdapter(offerInnerChildListAdapter);

            holder.itemFilterClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.filterSubDetailsImgView.getDrawable()==upDrawable){
                        holder.filterSubDetailsImgView.setImageDrawable(downDrawable);
                        holder.sublist.setVisibility(View.GONE);
                    } else {
                        holder.filterSubDetailsImgView.setImageDrawable(upDrawable);
                        holder.sublist.setVisibility(View.VISIBLE);
                    }
                    isPressed = !isPressed;
                    /*if(holder.sublist.getVisibility()== View.GONE) {
                        *//*Animation slide_down = AnimationUtils.loadAnimation(activity,
                                R.anim.slide_down);
                        holder.sublist.startAnimation(slide_down);*//*
                        holder.sublist.setVisibility(View.VISIBLE);
                    }else if(holder.sublist.getVisibility()== View.VISIBLE){
                        *//*Animation slide_up = AnimationUtils.loadAnimation(activity,
                                R.anim.slid_up);
                        holder.sublist.startAnimation(slide_up);*//*
                        holder.sublist.setVisibility(View.GONE);

                    }*/
                }
            });
        }
        /*if (mainOffersList.size() > 0) {
            holder.expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousGroup = -1;

                @Override
                public void onGroupExpand(int groupPosition) {
                    if (groupPosition != previousGroup)
                        holder.expandableListView.collapseGroup(previousGroup);
                    previousGroup = groupPosition;
                }
            });

        }*/
    }

    @Override
    public int getItemCount() {
        if(filterChildren.size()>0) {
            return filterChildren.size();
        }else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView filterSubDetailsTitle;
        public TextView selectedDetailListTxt;
        public ImageView filterSubDetailsImgView;
        public LinearLayout selectedDetailListLayout;
        public LinearLayout itemFilterClick;
        public CheckBox checkBox;
        RecyclerView sublist;

        public MyViewHolder(View view) {
            super(view);
            filterSubDetailsTitle = (TextView) view.findViewById(R.id.filterSubChildDetailsTitle);
            selectedDetailListTxt = (TextView) view.findViewById(R.id.selectedChildDetailListTxt);
            filterSubDetailsImgView = (ImageView) view.findViewById(R.id.filterSubChildDetailsImgView);
            selectedDetailListLayout = (LinearLayout) view.findViewById(R.id.selectedChildDetailListLayout);
            itemFilterClick = (LinearLayout) view.findViewById(R.id.itemChildFilterClick);
            checkBox = (CheckBox) view.findViewById(R.id.checkBoxChild);
            sublist = (RecyclerView) view.findViewById(R.id.sublist);
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

