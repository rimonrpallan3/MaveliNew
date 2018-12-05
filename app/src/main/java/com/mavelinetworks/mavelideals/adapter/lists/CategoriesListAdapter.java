package com.mavelinetworks.mavelideals.adapter.lists;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.AppController;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.classes.Category;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import java.util.List;



public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListAdapter.mViewHolder> {


    private LayoutInflater infalter;
    private List<Category> data;
    private Context context;
    private ClickListener clickListener;



    public void setData(List<Category> data){
        for (Category c : data){
            addItem(c);
        }
        notifyDataSetChanged();
    }



    public CategoriesListAdapter(Context context, List<Category> data){
        this.data = data;
        this.infalter = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public CategoriesListAdapter.mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = infalter.inflate(R.layout.item_category, parent, false);
        // TypefaceHelper.typeface(rootView);
        mViewHolder holder = new mViewHolder(rootView);

        return holder;
    }


    @Override
    public void onBindViewHolder(CategoriesListAdapter.mViewHolder holder, int position) {


        holder.name.setText(data.get(position).getNameCat());

        com.mavelinetworks.mavelideals.utils.Utils.setFontBold(context,holder.name);
        com.mavelinetworks.mavelideals.utils.Utils.setFontBold(context,holder.stores);

        if(data.get(position).getImages()!=null){

            Picasso.with(context)
                    .load(data.get(position).getImages().getUrl500_500())
                    .fit().centerCrop().into(holder.image);

        }



        Drawable storeDrawable = new IconicsDrawable(context)
                .icon(CommunityMaterial.Icon.cmd_store)
                .color(ResourcesCompat.getColor(context.getResources(),R.color.white,null))
                .sizeDp(12);

        if(AppController.isRTL()){
            holder.stores.setCompoundDrawables(null,null,storeDrawable,null);
            holder.stores.setCompoundDrawablePadding(10);
        }else {
            holder.stores.setCompoundDrawables(storeDrawable,null,null,null);
            holder.stores.setCompoundDrawablePadding(10);
        }

        holder.stores.setText(  String.format(
                context.getString(R.string.nbr_stores_message),
                String.valueOf(data.get(position).getNbr_stores())
        ));

    }


    public void removeAll(){

        int size = this.data.size();

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.data.remove(0);
            }

            if(size>0)
                this.notifyItemRangeRemoved(0, size);
        }
    }

    public Category getItem(int position){

        try{
            return  data.get(position);
        }catch (Exception e){
            return  null;
        }

    }




    public void addItem(Category item){

        int index = (data.size());
        data.add(item);
        //notifyItemInserted(index);
    }



    @Override
    public int getItemCount() {
        return data.size();
    }





    public  class mViewHolder extends RecyclerView.ViewHolder{



        public TextView name;
        public ImageView image;
        public TextView stores;
        public View mainLayout;


        public mViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.cat_name);
            image = (ImageView) itemView.findViewById(R.id.image);
            stores = (TextView) itemView.findViewById(R.id.stores);
            mainLayout = itemView.findViewById(R.id.mainLayout);

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener!=null){
                        clickListener.itemClicked(view,getPosition());
                    }
                }
            });
        }


    }


    public void setClickListener(ClickListener clicklistener){

        this.clickListener = clicklistener;

    }

    public interface ClickListener{
        public void itemClicked(View view, int position);
    }


}