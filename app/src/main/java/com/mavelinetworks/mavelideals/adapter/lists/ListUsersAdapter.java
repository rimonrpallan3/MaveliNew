package com.mavelinetworks.mavelideals.adapter.lists;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.AppContext;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.User;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by Droideve on 5/23/2016.
 */
public class ListUsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater infalter;
    private List<User> data;
    private Context context;
    private ClickListener clickListener;
    public List<User> getData() {
        return data;
    }


    public void setData(List<User> data) {
        this.data = data;
    }

    private final int heightBlock = 100;

    public ListUsersAdapter(Context context, List<User> data){
        this.data = data;
        this.infalter = LayoutInflater.from(context);
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {

        if(AppContext.DEBUG)
            Log.e("viewType",viewType+"");

        View rootView = infalter.inflate(R.layout.layout_user, parent, false);
        TypefaceHelper.typeface(rootView);

        com.mavelinetworks.mavelideals.utils.Utils.setFontBold(context, (TextView) rootView.findViewById(R.id.name));

        mViewHolder holder = new mViewHolder(rootView);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) == null ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
    }


    public static boolean firstDistanceMsg = false;
    public static boolean lastDistanceMsg = false;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder mHolder, final int position) {
        final mViewHolder mviewHolder = (ListUsersAdapter.mViewHolder) mHolder;

        mviewHolder.name.setText(data.get(position).getName());
        if(data.get(position).getImages()!=null)
            Picasso.with(context).load(data.get(position).getImages().getUrl200_200())
                    .fit().centerCrop().into(mviewHolder.userimage);

        mviewHolder.desc.setText("@"+ data.get(position).getUsername());
        mviewHolder.desc.setVisibility(View.VISIBLE);


        if(AppConfig.APP_DEBUG)
            Log.e("user", String.valueOf(position)+" "+data.get(position).getUsername());



        Log.e("distance - "+data.get(position).getUsername()+" - "+data.get(position).getType(), String.valueOf(data.get(position).getDistance()));


        if(data.get(position).isWithHeader()){

            try {
                if(data.get(position).getDistance()< Constances.DISTANCE_CONST){
                    mviewHolder.header.setText(context.getString(R.string.lessThan1km));
                }else if(  data.get(position).getDistance() > (Constances.DISTANCE_CONST*2) ){

                    mviewHolder.header.setText(context.getString(R.string.MoreThan2km));
                }else if(  data.get(position).getDistance() > (Constances.DISTANCE_CONST*5) ){

                    mviewHolder.header.setText(context.getString(R.string.MoreThan5km));
                }else if(  data.get(position).getDistance() > (Constances.DISTANCE_CONST*10) ){
                    mviewHolder.header.setText(context.getString(R.string.MoreThan10km));
                }

            }catch (Exception e){
                mviewHolder.header.setVisibility(View.GONE);
            }

            mviewHolder.header.setVisibility(View.VISIBLE);

        }else{
            mviewHolder.header.setVisibility(View.GONE);
        }

        if(data.get(position).getImages()!=null){
            Picasso.with(context).load(data.get(position).getImages()
                    .getUrl200_200()).fit().centerCrop().into(mviewHolder.userimage);
        }else
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerCrop().into(mviewHolder.userimage);


        Drawable icon = new IconicsDrawable(context)
                .icon(CommunityMaterial.Icon.cmd_dots_vertical)
                .color(Color.parseColor("#000000"))
                .sizeDp(16);
        mviewHolder.optionIcon.setImageDrawable(icon);

        if(data.get(position).isBlocked()){
            mviewHolder.name.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }else{
            mviewHolder.name.setTextColor(context.getResources().getColor(R.color.colorPrimary));
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

    public void deleteItem(int position){
        data.remove(position);
        notifyItemRemoved(position);
       // notifyItemRangeChanged(position, data.size());
    }

    public void swap(int firstPosition, int secondPosition){
        Collections.swap(data, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }


    public void uppdate(User user,int pos){
        data.set(pos,user);
        notifyItemChanged(pos);
        notifyItemRangeChanged(pos, data.size());
    }

    //private Realm realm = Realm.getDefaultInstance();


    public void addItem(User item){
        int index = (data.size());
        data.add(item);
        /////////
        if(item!=null)
            notifyItemInserted(index);
        //notifyDataSetChanged();
    }

    public User getItem(int position){
        return data.get(position);
    }
    public void remove(int position){

        int size = data.size();
        data.remove(position);

        notifyItemRemoved(position);
        //notifyItemRangeChanged(position,(size-1));


    }


    @Override
    public int getItemCount() {
        return data.size();
    }



    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_AD = 1;



    public  class mViewHolder extends RecyclerView.ViewHolder{


        public CircularImageView userimage;
        public TextView name;
        public TextView desc;


        public ImageButton close;
        public TextView header;
        public View rootView;
        public CardView mainLayout;
        public ImageButton optionIcon;



        public mViewHolder(View itemView) {
            super(itemView);

            rootView = itemView;

            mainLayout = (CardView) itemView.findViewById(R.id.mainLayout);
            userimage = (CircularImageView) itemView.findViewById(R.id.userimage);
            name = (TextView) itemView.findViewById(R.id.name);
            desc = (TextView) itemView.findViewById(R.id.desc);
            close = (ImageButton) itemView.findViewById(R.id.close);
            header = (TextView) itemView.findViewById(R.id.textHeader);

            optionIcon = (ImageButton) itemView.findViewById(R.id.option);

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener!=null){
                        clickListener.itemClicked(getPosition());
                    }
                }
            });

            optionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener!=null){
                        clickListener.itemOptionsClicked(optionIcon,getPosition());
                    }
                }
            });


        }


    }


    public void setClickListener(ClickListener clicklistener){

        this.clickListener = clicklistener;

    }

    public interface ClickListener{
        public void itemClicked(int position);
        public void itemOptionsClicked(View view,int position);
    }





}
