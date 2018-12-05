package com.mavelinetworks.mavelideals.adapter.lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.classes.Review;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewsListAdapter.mViewHolder> {


    private LayoutInflater infalter;
    private List<Review> data;
    private Context context;
    private ClickListener clickListener;




    public ReviewsListAdapter(Context context, List<Review> data){
        this.data = data;
        this.infalter = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ReviewsListAdapter.mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = infalter.inflate(R.layout.item_store_review, parent, false);
       // TypefaceHelper.typeface(rootView);
        mViewHolder holder = new mViewHolder(rootView);

        return holder;
    }


    @Override
    public void onBindViewHolder(ReviewsListAdapter.mViewHolder holder, int position) {

        Review mReview= data.get(position);

        holder.title.setText(mReview.getPseudo());
        holder.detail.setText(mReview.getReview());

        Picasso.with(context).load(mReview.getImage())
                .placeholder(R.drawable.profile_placeholder).fit().centerCrop().into(holder.image);

        holder.mRatingBar.setRating((float) mReview.getRate());

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

    public Review getItem(int position){

        try{
            return  data.get(position);
        }catch (Exception e){
            return  null;
        }

    }


    public void addItem(Review item){

        int index = (data.size());
        data.add(item);
        notifyItemInserted(index);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }



    public  class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public TextView name;
        public TextView title;
        public TextView detail;
        public CircularImageView image;
        public   RatingBar mRatingBar;

        public mViewHolder(View itemView) {
            super(itemView);

             image = (CircularImageView) itemView.findViewById(R.id.image);
             title = (TextView) itemView.findViewById(R.id.name);
             detail = (TextView) itemView.findViewById(R.id.detail);
             mRatingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            if(clickListener!=null){
                clickListener.itemClicked(v,getPosition());
            }
        }
    }


    public void setClickListener(ClickListener clicklistener){

        this.clickListener = clicklistener;

    }

    public interface ClickListener{
        public void itemClicked(View view, int position);
    }


}
