package com.mavelinetworks.mavelideals.fragments;

import android.app.Dialog;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

/**
 * Created by Droideve on 11/18/2017.
 */

public class SearchDialog extends Dialog {

    private TextView doSearch;
    private SeekBar mDistanceRange;
    private TextView mDistanceText;
    private MaterialAutoCompleteTextView searchEditText;
    private TextView searchBy;

    private static int mOldDistance=-1;
    private static String mOldValue="";

    public SearchDialog setHeader(String text){
        searchBy.setText(text);
        return this;
    }

    public SearchDialog(@NonNull Context context) {
        super(context);

       requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_search);

        doSearch = (TextView) findViewById(R.id.doSearch);
        mDistanceRange = (SeekBar) findViewById(R.id.distance);
        mDistanceText = (TextView) findViewById(R.id.md);
        searchEditText = (MaterialAutoCompleteTextView) findViewById(R.id.search);
        searchBy= (TextView) findViewById(R.id.searchBy);


        Utils.setFont(context,doSearch, Constances.Fonts.BOLD);
        Utils.setFont(context,mDistanceText, Constances.Fonts.BOLD);
        Utils.setFont(context,searchEditText, Constances.Fonts.BOLD);
        Utils.setFont(context,searchBy, Constances.Fonts.BOLD);

        if(mOldDistance==-1){
            int radius = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("distance_value", 100);
            mOldDistance = radius;
        }

        String val = String.valueOf(mOldDistance);
        if(mOldDistance==100){
            val = "+"+String.valueOf(mOldDistance);
        }

        String msg = String.format(getContext().getString(R.string.settings_notification_distance_dis),val);
        mDistanceText.setText(msg);
        mDistanceRange.setProgress(mOldDistance);
        searchEditText.setText(mOldValue);

        mDistanceRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                String val = String.valueOf(progress);
                if(progress==100){
                    val = "+"+String.valueOf(progress);
                }

                String msg = String.format(getContext().getString(R.string.settings_notification_distance_dis),val);
                mDistanceText.setText(msg);
                mOldDistance = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        doSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.onSearchClicked(SearchDialog.this,searchEditText.getText().toString(),mOldDistance);
                }
            }
        });

    }

    private Listener mListener;
    public SearchDialog setOnSearchListener(Listener l){
        if(mListener==null){
            mListener = l;
        }

        return this;
    }


    public interface Listener{
        public void onSearchClicked(SearchDialog mSearchDialog,String value,int radius);
    }

    public void showDialog(){
        if(!isShowing())
        show();
    }


//    public void dismissDialog(){
//
//        try {
//            if(isShowing())
//                dismiss();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }

    public static SearchDialog newInstance(Context context){
        return new SearchDialog(context);
    }



}
