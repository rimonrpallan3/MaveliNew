package com.mavelinetworks.mavelideals.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.classes.Store;
import com.mavelinetworks.mavelideals.controllers.stores.StoreController;
import com.mavelinetworks.mavelideals.fragments.ListOffersFragment;
import com.mavelinetworks.mavelideals.utils.Utils;


public class OffersActivity extends AppCompatActivity {

    Toolbar toolbar;
    private TextView APP_TITLE_VIEW = null;
    private TextView APP_DESC_VIEW = null;

    private int store_id = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_store_favortie);
        initToolbar();


        try {
            store_id = getIntent().getExtras().getInt("store_id");
        }catch (Exception e){}


        Bundle bundle = new Bundle();
        bundle.putInt("store_id",store_id);

        ListOffersFragment fragment = new ListOffersFragment();
        fragment.setArguments(bundle);


        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction()
                .replace(R.id.store_content, fragment)
                .commit();

        Store store = StoreController.findStoreById(store_id);

        APP_TITLE_VIEW.setText(R.string.offers);
        APP_DESC_VIEW.setText(store.getName());
        APP_DESC_VIEW.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(android.R.id.home==item.getItemId()){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void initToolbar(){

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setSubtitle("E-shop");
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_36dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        APP_TITLE_VIEW = (TextView) toolbar.findViewById(R.id.toolbar_title);
        APP_DESC_VIEW = (TextView) toolbar.findViewById(R.id.toolbar_description);
        Utils.setFont(this, APP_DESC_VIEW, "SourceSansPro-Black.otf");
        Utils.setFont(this, APP_TITLE_VIEW, "SourceSansPro-Black.otf");

        APP_DESC_VIEW.setVisibility(View.GONE);

    }
}
