package com.mavelinetworks.mavelideals.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.classes.Filter;
import com.mavelinetworks.mavelideals.classes.FilterChild;
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

public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.MyViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 2;

    IOfferFilterView iOfferFilterView;

    Activity activity;
    private CardView headerView, footerView;
    List<Filter> filterList;
    boolean isPressed = false;

    public FilterListAdapter(List<Filter> filterList, Activity activity,IOfferFilterView iOfferFilterView) {
        this.filterList = filterList;
        this.activity = activity;
        this.iOfferFilterView = iOfferFilterView;
        System.out.println("Filter List : "+filterList.toString());
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item_list_content, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.itemFilterClick.getLayoutParams();
        if (filterList.size() == (position + 1)) {
            mlp.setMargins(0, 0, 0, (int) activity.getResources().getDimension(R.dimen._80));
        } else {
            mlp.setMargins(0, 0, 0, 0);
        }

        final Drawable rightDrawable = new IconicsDrawable(activity)
                .icon(CommunityMaterial.Icon.cmd_menu_right)
                .color(ResourcesCompat.getColor(activity.getResources(), R.color.iconColor, null))
                .sizeDp(16);

        holder.innerArrayList.setImageDrawable(rightDrawable);
        final Filter filter = filterList.get(position);
        final RealmList<FilterChild> filterChildren = filter.getFilterChild();
        if (filterChildren != null) {
            holder.innerArrayList.setVisibility(View.VISIBLE);
        } else {
            holder.innerArrayList.setVisibility(View.GONE);
        }
        holder.filterSubDetailsTitle.setText(filter.getNameFilt());
        if (filterList.size() > 0) {
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //holder.checkBoxContentFileUpload.setChecked(!holder.checkBoxContentFileUpload.isChecked());
                    if (holder.checkBox.isChecked()) {
                        iOfferFilterView.onItemCheck(filter.getOfferTypeId());
                    } else {
                        iOfferFilterView.onItemUncheck(filter.getOfferTypeId());
                    }
                }
            });

            holder.itemFilterClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iOfferFilterView.setChildList(filterChildren);
                }
            });
        }
    }

    public void addItem(Filter item){

        int index = (filterList.size());
        filterList.add(item);
        notifyDataSetChanged();
        //notifyItemInserted(index);
    }



    public void removeAll(){
        int size = this.filterList.size();

        if (size > 0) {

            for (int i = 0; i < size; i++) {
                this.filterList.remove(0);
            }
            if(size>0)
                this.notifyItemRangeRemoved(0, size);

        }

    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView filterSubDetailsTitle;
        public TextView selectedDetailListTxt;
        public LinearLayout selectedDetailListLayout;
        public LinearLayout itemFilterClick;
        public ImageView innerArrayList;
        public CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            filterSubDetailsTitle = (TextView) view.findViewById(R.id.filterSubDetailsTitle);
            selectedDetailListTxt = (TextView) view.findViewById(R.id.selectedDetailListTxt);
            innerArrayList = (ImageView) view.findViewById(R.id.innerArrayList);
            selectedDetailListLayout = (LinearLayout) view.findViewById(R.id.selectedDetailListLayout);
            itemFilterClick = (LinearLayout) view.findViewById(R.id.itemFilterClick);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
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

