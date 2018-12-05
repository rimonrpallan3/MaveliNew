package com.mavelinetworks.mavelideals.activities.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.classes.UserDetails;
import com.mavelinetworks.mavelideals.classes.UserRow;
import com.mavelinetworks.mavelideals.common.Helper;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 31-Oct-18.
 */

public class ProfilePage extends AppCompatActivity {

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    String phoneNo = "";
    @BindView(R.id.ivProfile)
    ImageView ivProfile;
    @BindView(R.id.ivUserName)
    ImageView ivUserName;
    @BindView(R.id.ivEmail)
    ImageView ivEmail;
    @BindView(R.id.ivPhoneNo)
    ImageView ivPhoneNo;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.tvPhoneNo)
    TextView tvPhoneNo;
    @BindView(R.id.tlProfilePage)
    Toolbar tlProfilePage;
    UserDetails userDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        sharedPrefs = getSharedPreferences(Helper.UserDetails,
                Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        setSupportActionBar(tlProfilePage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.profile_account));

        userDetails = getUserSDetails();

        Drawable drUserName = MaterialDrawableBuilder.with(this) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.ACCOUNT) // provide an icon
                .setColor(getResources().getColor(R.color.gray)) // set the icon color
                .setSizeDp(24) // set the icon size
                .build();
        Drawable drEmail = MaterialDrawableBuilder.with(this) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.EMAIL) // provide an icon
                .setColor(getResources().getColor(R.color.gray)) // set the icon color
                .setSizeDp(24) // set the icon size
                .build();
        Drawable drPhoneNo = MaterialDrawableBuilder.with(this) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.PHONE) // provide an icon
                .setColor(getResources().getColor(R.color.gray)) // set the icon color
                .setSizeDp(24) // set the icon size
                .build();


        ivUserName.setImageDrawable(drUserName);
        ivEmail.setImageDrawable(drEmail);
        ivPhoneNo.setImageDrawable(drPhoneNo);
        if(userDetails!=null){
            tvUserName.setText(userDetails.getName());
            tvEmail.setText(userDetails.getEmail());
            tvPhoneNo.setText(userDetails.getPhone());
        }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private UserDetails getUserSDetails() {
        UserDetails userDetails = new UserDetails();
        Gson gson = new Gson();
        String json = sharedPrefs.getString("UserDetails", null);
        if (json != null) {
            System.out.println("-----------LandingPage uploadProfileName UserDetails" + json);
            userDetails = gson.fromJson(json, UserDetails.class);
            //emailAddress = userDetails.getEmail();
        }
        return  userDetails;

    }
}
