package com.mavelinetworks.mavelideals.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.BuildConfig;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.wuadam.awesomewebview.AwesomeWebView;

public class SettingActivity  extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        AppBarLayout bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.appbar_setting, root, false);
            root.addView(bar, 0);
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            root.removeAllViews();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.appbar_setting, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }


        Toolbar Tbar = (Toolbar) bar.getChildAt(0);
        Tbar.setClickable(true);

        int resId = getResIdFromAttribute(this, R.attr.homeAsUpIndicator);
        Drawable arrow  = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_close_white_24dp, null);
        // arrow.setColorFilter(ResourcesCompat.getColor(getResources(),R.color.defaultColor,null), PorterDuff.Mode.MULTIPLY);
        Tbar.setNavigationIcon(arrow);

        TextView title = (TextView) Tbar.findViewById(R.id.toolbar_title);
        TextView toolbar_description = (TextView) Tbar.findViewById(R.id.toolbar_description);

        toolbar_description.setVisibility(View.GONE);
        title.setText(getString(R.string.settings));
        Utils.setFont(this, title, Constances.Fonts.BOLD);

        Tbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        setupSimplePreferencesScreen();

    }




    @SuppressWarnings("deprecation")

    //private SeekBarPreference _seekBarPref;
    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.settings);

//
//        Preference edit_profile = (Preference) findPreference("edit_profile");
//        Preference about_us = (Preference) findPreference("about_us");
//        Preference privacy_policy = (Preference) findPreference("privacy_policy");
//        Preference term_of_use = (Preference) findPreference("term_of_use");

//        _seekBarPref = (SeekBarPreference) this.findPreference("distance_value");
//
//        // Set listener :
//        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
//        // Set seekbar summary :
//        int radius = PreferenceManager.getDefaultSharedPreferences(this).getInt("distance_value", 100);
//
//        String val = String.valueOf(radius);
//        if(radius==100){
//            val = "+"+String.valueOf(radius);
//        }
//        _seekBarPref.setSummary(
//                String.format(getString(R.string.settings_notification_distance_msg),val)
//        );



        Preference app_version = (Preference) findPreference("app_version");
        app_version.setSummary(BuildConfig.VERSION_NAME);



        Preference app_term_of_uses = (Preference) findPreference("app_term_of_uses");
        Preference app_privacy = (Preference) findPreference("app_privacy");



        app_term_of_uses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                new AwesomeWebView.Builder(SettingActivity.this)
                        .showMenuOpenWith(false)
                        .statusBarColorRes(R.color.colorPrimary)
                        .theme(R.style.FinestWebViewAppTheme)
                        .titleColor(
                                ResourcesCompat.getColor(getResources(),R.color.defaultColor,null)
                        ).urlColor(
                        ResourcesCompat.getColor(getResources(),R.color.defaultColor,null)
                ).show(Constances.TERMS_OF_USE_URL);


                return false;
            }
        });

        app_privacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                new AwesomeWebView.Builder(SettingActivity.this)
                        .showMenuOpenWith(false)
                        .statusBarColorRes(R.color.colorPrimary)
                        .theme(R.style.FinestWebViewAppTheme)
                        .titleColor(
                                ResourcesCompat.getColor(getResources(),R.color.defaultColor,null)
                        ).urlColor(
                        ResourcesCompat.getColor(getResources(),R.color.defaultColor,null)
                ).show(Constances.PRIVACY_POLICY_URL);

                return false;
            }
        });


    }


    private static int getResIdFromAttribute(final Activity activity, final int attr) {
        if (attr == 0) {
            return 0;
        }
        final TypedValue typedvalueattr = new TypedValue();
        activity.getTheme().resolveAttribute(attr, typedvalueattr, true);
        return typedvalueattr.resourceId;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        int radius = PreferenceManager.getDefaultSharedPreferences(this).getInt("distance_value", 100);


        String val = String.valueOf(radius);
        if(radius==100){
            val = "+"+String.valueOf(radius);
        }
//        _seekBarPref.setSummary(
//                String.format(getString(R.string.settings_notification_distance_msg),val)
//        );

    }



}
