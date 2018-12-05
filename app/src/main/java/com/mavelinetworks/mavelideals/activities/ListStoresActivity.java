package com.mavelinetworks.mavelideals.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.classes.Category;
import com.mavelinetworks.mavelideals.fragments.ListStoresFragment;
import com.mavelinetworks.mavelideals.fragments.MainFragment;
import com.mavelinetworks.mavelideals.utils.Utils;

import io.realm.Realm;


public class ListStoresActivity extends AppCompatActivity {

    Toolbar toolbar;
    private Category mCat=null;
    private int CatId,userId;
    private TextView APP_TITLE_VIEW = null;
    private TextView APP_DESC_VIEW = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_list_activity);

        initToolbar();
        Realm realm = Realm.getDefaultInstance();

        try {
            userId = getIntent().getExtras().getInt("user_id");

        }catch (Exception e){

        }

        try {

            CatId = getIntent().getExtras().getInt("category");
            mCat = realm.where(Category.class).equalTo("numCat",CatId).findFirst();
            APP_TITLE_VIEW.setText(mCat.getNameCat());

        }catch (Exception ex){

            Toast.makeText(this,"Error @655",Toast.LENGTH_LONG).show();

            if(AppConfig.APP_DEBUG)
                ex.printStackTrace();

            finish();
        }



        ListStoresFragment listFrag = new ListStoresFragment();


        Bundle b = new Bundle();
        try {
            b.putInt("category",mCat.getNumCat());
        }catch (Exception e){

        }

        b.putInt("user_id",userId);
        listFrag.setArguments(b);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.frame, listFrag, MainFragment.TAG);
       // transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(android.R.id.home==item.getItemId()){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cat_menu, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void initToolbar(){

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

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
