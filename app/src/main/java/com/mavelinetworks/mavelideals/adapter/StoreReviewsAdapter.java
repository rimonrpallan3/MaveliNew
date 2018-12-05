package com.mavelinetworks.mavelideals.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.ReviewsActivity;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.classes.Review;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Droideve on 11/12/2017.
 */

public class StoreReviewsAdapter {

    private Context context;
    private List<Review> list;
    private int count;
    private int resLayout;
    private LayoutInflater inflater;

    public StoreReviewsAdapter(Context context){
        this.context = context;
        try {
            inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }catch (Exception e){
        }
    }

    public StoreReviewsAdapter load(List<Review> list){
       this.list = list;
        return this;
    }

    public static StoreReviewsAdapter newInstance(Context context){
        return new StoreReviewsAdapter(context);
    }


    public StoreReviewsAdapter inflate(int resLayout){

        this.resLayout = resLayout;

        return this;
    }


    public StoreReviewsAdapter into(LinearLayout rootView){
        loop(rootView);
        return this;
    }


    private View prepareView(View layout,Review mReview){

        CircularImageView image = (CircularImageView) layout.findViewById(R.id.image);
        TextView title = (TextView) layout.findViewById(R.id.name);
        TextView detail = (TextView) layout.findViewById(R.id.detail);
        RatingBar mRatingBar = (RatingBar) layout.findViewById(R.id.ratingBar);

        title.setText(mReview.getPseudo());
        detail.setText(mReview.getReview());

        Picasso.with(context).load(mReview.getImage()).placeholder(R.drawable.profile_placeholder).fit().centerCrop().into(image);
        mRatingBar.setRating((float) mReview.getRate());

        return layout;
    }

    public void loop(LinearLayout rootView){

        rootView.removeAllViews();

        if(inflater!=null){

            for (int i=0;i<list.size();i++) {

                if(AppConfig.APP_DEBUG)
                    Log.e("StoreReviewAdapter","Put it "+list.get(i).getReview());

                View layout = inflater.inflate(resLayout, null);

                TypefaceHelper.typeface(layout);

                final int finalI = i;
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mListener!=null)
                            mListener
                                    .onReviewClicked(finalI);
                    }
                });
                rootView.addView(prepareView(layout,list.get(i)));

                if(i==5)
                    break;
            }


            if(list.size()>=7){

                View layout = inflater.inflate(R.layout.item_store_review_load_more, null);
                Button button = (Button) layout.findViewById(R.id.loadMore);
                button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent  = new Intent(context,ReviewsActivity.class);
                        intent.putExtra("store_id",list.get(0).getStore_id());
                        context.startActivity(intent);

                    }
                });

                rootView.addView(layout);

            }

        }


    }

    private Listener mListener;
    public void setOnistener(Listener l){
            mListener = l;
    }

    public interface Listener{
        public void onReviewClicked(int position);
    }
}
