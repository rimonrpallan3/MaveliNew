package com.mavelinetworks.mavelideals.adapter.lists;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.classes.Store;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;


public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.mViewHolder> {


    private LayoutInflater infalter;
    private List<Store> data;
    private Context context;
    private ClickListener clickListener;


    public StoreListAdapter(Context context, List<Store> data){
        this.data = data;
        this.infalter = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public StoreListAdapter.mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = infalter.inflate(R.layout.fragment_store_custom_item, parent, false);

        TypefaceHelper.typeface(rootView);

        mViewHolder holder = new mViewHolder(rootView);

        return holder;
    }



    @Override
    public void onBindViewHolder(StoreListAdapter.mViewHolder holder, int position) {

        int size = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

        if (Configuration.SCREENLAYOUT_SIZE_XLARGE == size) {

            holder.image.getLayoutParams().height = (int) (MainActivity.width / 2.5);
        } else {
            holder.image.getLayoutParams().height = (int) (MainActivity.width / 1.5);
        }

        if(this.data.get(position).getImages()!=null) {

            if(APP_DEBUG) {  Log.e("image",data.get(position).getImages()
                    .getUrl200_200()); }

                Picasso.with(context)
                        .load(this.data.get(position).getImages()
                                .getUrl500_500())
                        .fit().centerCrop().placeholder(R.drawable.def_logo)
                        .into(holder.image);

        }


        if(this.data.get(position).getListImages()!=null){
            if(this.data.get(position).getListImages().size()>0){

                Picasso.with(context)
                        .load(this.data.get(position).getListImages().get(0).getUrl500_500())
                        .into(holder.image);

            }else{
                Picasso.with(context).load(R.drawable.def_logo).into(holder.image);
            }
        }else
            Picasso.with(context).load(R.drawable.def_logo).into(holder.image);



        if(data.get(position).getDistance()!=null){

            if(this.data.get(position).getLatitude()!=0 && this.data.get(position).getLongitude()!=0) {
                holder.distance.setText(
                        Utils.preparDistance(this.data.get(position).getDistance())
                                + " " +
                                Utils.getDistanceBy(this.data.get(position).getDistance())
                );
            }else{
                holder.distance.setText("N/A KM");
            }

            holder.distance.setText(holder.distance.getText().toString().toUpperCase());
        }



            /*if(data.get(position).getVotes() == 0)
            {
                holder.comment.setVisibility(View.GONE);
            }*/


        float rated = (float) data.get(position).getVotes();
        DecimalFormat decim = new DecimalFormat("#.##");


        holder.rate.setText(decim.format(rated) +"  ("+data.get(position).getNbr_votes()+")");

        holder.ratingBar.setRating(rated);

        holder.name.setText(data.get(position).getName());
        holder.address.setText(data.get(position).getAddress());


        holder.nbrOffer.setText(
                String.format( context.getString(R.string.nbrOffers),  data.get(position).getNbrOffers() )
        );

        if(data.get(position).getNbrOffers()==0)
            holder.nbrOffer.setVisibility(View.GONE);


        if(data.get(position).getLastOffer().equals("")){
            holder.offer.setVisibility(View.GONE);
        }else {
            holder.offer.setVisibility(View.VISIBLE);
            holder.offer.setText(data.get(position).getLastOffer());
        }


        if(data.get(position).getFeatured()==0){
            holder.featured.setVisibility(View.GONE);
        }else {
            holder.featured.setVisibility(View.VISIBLE);
        }

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

    public Store getItem(int position){

        try{
            return  data.get(position);
        }catch (Exception e){
            return  null;
        }

    }




    public void addItem(Store item){

        int index = (data.size());
        data.add(item);
        notifyDataSetChanged();
        //notifyItemInserted(index);
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setClickListener(ClickListener clicklistener) {

        this.clickListener = clicklistener;

    }


    public interface ClickListener {
        void itemClicked(View view, int position);

        void iconImageViewOnClick(View v, int position);
    }

    public  class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public ImageView image;
        public TextView name;
        public TextView address;
        public TextView distance;
        public TextView rate;
        public RatingBar ratingBar;
        public TextView nbrOffer;
        public TextView offer;
        public TextView featured;



        public mViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            address  = (TextView) itemView.findViewById(R.id.address);
            rate = (TextView) itemView.findViewById(R.id.rate);
            distance = (TextView) itemView.findViewById(R.id.distance);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar2);
            nbrOffer = (TextView) itemView.findViewById(R.id.offers);
            offer = (TextView) itemView.findViewById(R.id.offer);
            featured = (TextView) itemView.findViewById(R.id.featured);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {


            if(clickListener!=null){
                clickListener.itemClicked(v,getPosition());
            }

            //delete(getPosition());


        }
    }


}
