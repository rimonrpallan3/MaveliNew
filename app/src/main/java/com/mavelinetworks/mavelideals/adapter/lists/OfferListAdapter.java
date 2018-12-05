package com.mavelinetworks.mavelideals.adapter.lists;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.AppController;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.classes.Offer;
import com.mavelinetworks.mavelideals.utils.DateUtils;
import com.mavelinetworks.mavelideals.utils.OfferUtils;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;


public class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.mViewHolder> {


    private LayoutInflater infalter;
    private List<Offer> data;
    private Context context;
    private ClickListener clickListener;


    public OfferListAdapter(Context context, List<Offer> data){
        this.data = data;
        this.infalter = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public OfferListAdapter.mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = infalter.inflate(R.layout.fragment_offer_custom_item, parent, false);

        TypefaceHelper.typeface(rootView);

        mViewHolder holder = new mViewHolder(rootView);

        return holder;
    }



    @Override
    public void onBindViewHolder(final OfferListAdapter.mViewHolder holder, final int position) {

        DateUtils.getDateByTimeZone(data.get(position).getDate_end(), "dd-MM-yyyy");
        int size = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

        if (Configuration.SCREENLAYOUT_SIZE_XLARGE == size) {
            holder.image.getLayoutParams().height = (int) (MainActivity.width / 2.8);
        } else {
            holder.image.getLayoutParams().height = (int) (MainActivity.width / 1.8);
        }

        if(data.get(position).getContent()!=null){

            if(data.get(position).getContent().getPercent()>0 || data.get(position).getContent().getPercent()<0){
                DecimalFormat decimalFormat = new DecimalFormat("#0");
                holder.offer.setText( decimalFormat.format(data.get(position).getContent().getPercent())+"%");
                holder.offerPercentage.setVisibility(View.GONE);
                holder.priceCutLayout.setVisibility(View.GONE);
            }else{
                if(data.get(position).getContent().getPrice()!=0){
                    holder.offerPercentage.setVisibility(View.VISIBLE);
                    holder.priceCutLayout.setVisibility(View.VISIBLE);

                    /*holder.offer.setText(OfferUtils.parseCurrencyFormat(
                            data.get(position).getContent().getPrice(),
                            data.get(position).getContent().getCurrency()
                    ));*/

                    holder.offer.setText(
                            OfferUtils.parseCurrencyFormat(
                                    data.get(position).getContent().getPrice(),
                                    data.get(position).getContent().getCurrency()));
                    //holder.offer.setText(String.format("%.2f", data.get(position).getContent().getPrice()));
                    Drawable priceCutImg = new IconicsDrawable(context)
                            .icon(CommunityMaterial.Icon.cmd_close)
                            .color(ResourcesCompat.getColor(context.getResources(), R.color.white, null))
                            .sizeDp(1);

                    DecimalFormat decimalFormat = new DecimalFormat("#0");
                    holder.offerMainPrice.setText(String.format("%.0f", data.get(position).getContent().getActualPrice()));
                    holder.offerMainPrice.setText(
                            OfferUtils.parseCurrencyFormat(
                                    data.get(position).getContent().getActualPrice(),
                                    data.get(position).getContent().getCurrency()
                            ));
                    holder.offerPercentage.setText(decimalFormat.format(data.get(position).getContent().getOfferPercent()) + "%");
                    holder.offerPriceImgCut.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.price_cut_draw));
                    holder.offerPriceImgCut.setScaleType(ImageView.ScaleType.FIT_XY);

                }else {
                    holder.offer.setText(context.getString(R.string.promo));
                }
            }

        }else {
            Picasso.with(context).load(R.drawable.def_logo).into(holder.image);
        }


        String symbole = com.mavelinetworks.mavelideals.utils.Utils.getDistanceBy(
                data.get(position).getDistance()
        );
        String distance = com.mavelinetworks.mavelideals.utils.Utils.preparDistance(
                data.get(position).getDistance()
        );

        holder.distance.setText(
                String.format(context.getString(R.string.offerIn), distance+" "+symbole.toUpperCase())
        );

        holder.name.setText(   data.get(position).getName()   );
        holder.description.setText(  data.get(position).getStore_name()  );

        Drawable locationDrawable = new IconicsDrawable(context)
                .icon(CommunityMaterial.Icon.cmd_map_marker)
                .color(ResourcesCompat.getColor(context.getResources(),R.color.iconColor,null))
                .sizeDp(12);

        if(!AppController.isRTL())
            holder.description.setCompoundDrawables(locationDrawable,null,null,null);
        else
            holder.description.setCompoundDrawables(null,null,locationDrawable,null);

        holder.description.setCompoundDrawablePadding(14);

        if(data.get(position).getImages()!=null){
            /*Picasso.with(context).load(data.get(position).getImages().getUrl500_500())
                    .fit().centerCrop().into(holder.image);*/
            Picasso.with(context)
                    .load(data.get(position).getImages().getUrl500_500())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .resize(0, 200)
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            //Try again online if cache failed
                            Picasso.with(context)
                                    .load(data.get(position).getImages().getUrl500_500())
                                    .error(R.drawable.def_logo)
                                    .resize(0, 200)
                                    .into(holder.image, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                        }
                                    });
                        }
                    });
        }else {

            Picasso.with(context).load(R.drawable.def_logo)
                    .fit().centerCrop().into(holder.image);
        }


        if(data.get(position).getFeatured()==0){
            holder.ivFeatured.setVisibility(View.GONE);
        }else {
            holder.ivFeatured.setVisibility(View.VISIBLE);
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

    public Offer getItem(int position){

        try{
            return  data.get(position);
        }catch (Exception e){
            return  null;
        }

    }




    public void addItem(Offer item){

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
        public ImageView offerPriceImgCut;
        public TextView name;
        public TextView description;
        public TextView distance;
        public TextView offer;
        public TextView offerMainPrice;
        public TextView offerPercentage;
        public FrameLayout priceCutLayout;
        public ImageView ivFeatured;



        public mViewHolder(View itemView) {
            super(itemView);
            /*image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.address);
            distance = (TextView) itemView.findViewById(R.id.distance);
            offer = (TextView) itemView.findViewById(R.id.offer);
            featured = (TextView) itemView.findViewById(R.id.featured);
            image = (ImageView) itemView.findViewById(R.id.image);*/

            image = (ImageView) itemView.findViewById(R.id.image);
            offerPriceImgCut = (ImageView) itemView.findViewById(R.id.offerPriceImgCut);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.address);
            distance = (TextView) itemView.findViewById(R.id.distance);
            offer = (TextView) itemView.findViewById(R.id.offer);
            offerMainPrice = (TextView) itemView.findViewById(R.id.offerMainPrice);
            offerPercentage = (TextView) itemView.findViewById(R.id.offerPercentage);
            priceCutLayout = (FrameLayout) itemView.findViewById(R.id.priceCutLayout);
            ivFeatured = (ImageView) itemView.findViewById(R.id.ivFeatured);


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
