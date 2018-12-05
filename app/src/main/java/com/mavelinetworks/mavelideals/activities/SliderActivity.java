package com.mavelinetworks.mavelideals.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.apis.HackyViewPager;
import com.mavelinetworks.mavelideals.classes.Store;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;


public class SliderActivity extends AppCompatActivity {


    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        //setContentView(mViewPager);

        List<String> list = new ArrayList<String>();

        try {

            JSONObject js = new JSONObject( getIntent().getExtras().getString(Store.Tags.LISTIMAGES));
            list = new ArrayList<String>();
            if(APP_DEBUG) { Log.e("url",js.length()+""); }

            for (int i = 0; i < js.length(); i++) {
                list.add(js.getString(i+""));
                if(APP_DEBUG) {
                    Log.e("addListSlider",js.getString(i+""));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SamplePagerAdapter pager = new SamplePagerAdapter(this,list);
        mViewPager.setAdapter(pager);



    }


    static class SamplePagerAdapter extends PagerAdapter {

        private List<String> images;
        private Context context;

        public SamplePagerAdapter(Context context,List<String> data){
            this.images = data;
            this.context = context;
        }



        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());

            Picasso.with(context).load(images.get(position))
                    .into(photoView);

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }


}
