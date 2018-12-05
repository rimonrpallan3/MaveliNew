package com.mavelinetworks.mavelideals.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.OffersActivity;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.classes.Offer;
import com.mavelinetworks.mavelideals.utils.OfferUtils;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Droideve on 11/12/2017.
 */

public class StoreOfferAdapter {

    private Context context;
    private List<Offer> list;
    private int resLayout;
    private LayoutInflater inflater;

    public StoreOfferAdapter(Context context){
        this.context = context;
        try {
            inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }catch (Exception e){
        }

    }

    public StoreOfferAdapter load(List<Offer> list){
       this.list = list;
        return this;
    }

    public static StoreOfferAdapter newInstance(Context context){
        return new StoreOfferAdapter(context);
    }

    public StoreOfferAdapter inflate(int resLayout){

        this.resLayout = resLayout;

        return this;
    }


    public StoreOfferAdapter into(LinearLayout rootView){
        loop(rootView);
        return this;
    }


    private View prepareView(View layout,Offer offer){

        ImageView image = (ImageView) layout.findViewById(R.id.image);
        TextView title = (TextView) layout.findViewById(R.id.name);
        TextView detail = (TextView) layout.findViewById(R.id.detail);
        TextView price = (TextView) layout.findViewById(R.id.price);

        title.setText(offer.getName());
        detail.setText(Html.fromHtml(offer.getContent().getDescription()));

        if(offer.getContent().getPrice()>=0) {
            price.setText(OfferUtils.parseCurrencyFormat(
                    offer.getContent().getPrice(),
                    offer.getContent().getCurrency()));
        }else {
            price.setText(offer.getContent().getPercent()+"%");
        }

        if(offer.getContent().getPrice()==0 || offer.getContent().getPercent()==0){
            price.setText(context.getString(R.string.promo));
        }

        try {

            Picasso.with(context).load(offer.getImages()
                    .getUrl200_200()).fit().centerCrop()
                    .into(image);

        }catch (Exception e){

        }


        return layout;
    }

    public void loop(LinearLayout rootView){

        rootView.removeAllViews();
        if(inflater!=null){

            for (int i=0;i<list.size();i++) {

                View layout = inflater.inflate(resLayout, null);
                TypefaceHelper.typeface(layout);

                final int finalI = i;
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mListener!=null)
                            mListener
                                    .onOfferClicked(finalI);
                    }
                });

                rootView.addView(prepareView(layout,list.get(i)));

                if(i==5)
                    break;
            }
        }

        if(list.size()>=7){

            try {

                View layout = inflater.inflate(R.layout.item_store_review_load_more, null);
                Button button = (Button) layout.findViewById(R.id.loadMore);
                button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent  = new Intent(context,OffersActivity.class);
                        intent.putExtra("store_id",list.get(0).getStore_id());
                        context.startActivity(intent);

                    }
                });

                rootView.addView(layout);

            }catch (Exception e){
                if(AppConfig.APP_DEBUG)
                    e.printStackTrace();
            }


        }

    }

    private Listener mListener;
    public void setOnistener(Listener l){
            mListener = l;
    }

    public interface Listener{
        public void onOfferClicked(int position);
    }
}
