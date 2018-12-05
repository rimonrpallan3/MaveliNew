package com.mavelinetworks.mavelideals.adapter.lists;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.classes.Event;
import com.mavelinetworks.mavelideals.utils.DateUtils;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;


public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.mViewHolder> {


    private LayoutInflater infalter;
    private List<Event> data;
    private Context context;
    private ClickListener clickListener;



    public EventListAdapter(Context context, List<Event> data){
        this.data = data;
        this.infalter = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public EventListAdapter.mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = infalter.inflate(R.layout.item_event_custom, parent, false);
        TypefaceHelper.typeface(rootView);
        mViewHolder holder = new mViewHolder(rootView);

        return holder;
    }



    @Override
    public void onBindViewHolder(EventListAdapter.mViewHolder holder, int position) {




        int size = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

        if (Configuration.SCREENLAYOUT_SIZE_XLARGE == size) {

            holder.image.getLayoutParams().height = (int) (MainActivity.width / 2.5);
        } else {
            holder.image.getLayoutParams().height = (int) (MainActivity.width / 1.5);
        }


        if(this.data.get(position).getListImages()!=null && data.get(position).getListImages().size()>0) {

            if(APP_DEBUG) {
                Log.e("image",data.get(position).getListImages()
                    .get(0).getUrl500_500());

            }

                Picasso.with(context)
                        .load(this.data.get(position).getListImages()
                                .get(0).getUrl500_500())
                        .fit().centerCrop().placeholder(R.drawable.def_logo)
                        .into(holder.image);

        }else {
            Picasso.with(context).load(R.drawable.def_logo).into(holder.image);
        }

        if(this.data.get(position).getListImages()==null)
            if(data.get(position).getType()==1 && data.get(position).getType()==2){
                holder.image.setImageResource(R.drawable.def_logo);
            }else if(data.get(position).getType()==3){
                holder.image.setImageResource(R.drawable.def_logo);
            }


        if(this.data.get(position).getListImages().size()>0){

            Picasso.with(context)
                    .load(this.data.get(position).getListImages().get(0).getUrl500_500())
                    .into(holder.image);
        }

        if(DateUtils.isLessThan24(this.data.get(position).getDateB(),null)){
            holder.upcoming.setVisibility(View.VISIBLE);
        }else {
            holder.upcoming.setVisibility(View.GONE);
        }


        holder.name.setText(data.get(position).getName());

        holder.address.setText(
                String.format(context.getString(R.string.FromTo),
                        DateUtils.getDateByTimeZone(data.get(position).getDateB(),"dd MMMM yyyy"),
                        DateUtils.getDateByTimeZone(data.get(position).getDateE(),"dd MMMM yyyy")
                )
        );

        String symbole = com.mavelinetworks.mavelideals.utils.Utils.getDistanceBy(
                data.get(position).getDistance()
        );
        String distance = com.mavelinetworks.mavelideals.utils.Utils.preparDistance(
                data.get(position).getDistance()
        );
        holder.distance.setText(
                String.format(context.getString(R.string.offerIn), distance+" "+symbole.toUpperCase())
        );



        Utils.setFont(context,holder.name,"");



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

    public Event getItem(int position){

        try{
            return  data.get(position);
        }catch (Exception e){
            return  null;
        }

    }




    public void addItem(Event item){

        int index = (data.size());
        data.add(item);
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return data.size();
    }





    public  class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public ImageView image;
        public TextView name;
        public TextView address;
        public TextView distance;
        //public ImageView location;
        public TextView featured;
        public TextView upcoming;



        public mViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            //location = (ImageView) itemView.findViewById(R.id.location);
            name = (TextView) itemView.findViewById(R.id.name);
            address  = (TextView) itemView.findViewById(R.id.address);
            distance  = (TextView) itemView.findViewById(R.id.distance);
            featured  = (TextView) itemView.findViewById(R.id.featured);
            upcoming  = (TextView) itemView.findViewById(R.id.upcoming);


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


    public void setClickListener(ClickListener clicklistener){

        this.clickListener = clicklistener;

    }

    public interface ClickListener{
        public void itemClicked(View view, int position);
        public void iconImageViewOnClick(View v, int position);
    }


}
