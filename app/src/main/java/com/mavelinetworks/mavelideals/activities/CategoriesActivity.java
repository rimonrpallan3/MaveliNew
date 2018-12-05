package com.mavelinetworks.mavelideals.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.adapter.lists.CategoriesListAdapter;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Category;
import com.mavelinetworks.mavelideals.controllers.categories.CategoryController;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.CategoryParser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.norbsoft.typefacehelper.TypefaceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;


public class CategoriesActivity extends AppCompatActivity implements CategoriesListAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener {

    RecyclerView lisyView;
    CategoriesListAdapter adapter;
    //GET CATEGORIES FROM  DATABASE
    Toolbar toolbar;
    private TextView APP_TITLE_VIEW = null;
    private TextView APP_DESC_VIEW = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RequestQueue queue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_categories);


        initToolbar();

        APP_TITLE_VIEW.setText(R.string.categories);

        lisyView = (RecyclerView) findViewById(R.id.list);
        lisyView.setVisibility(View.VISIBLE);

        adapter = new CategoriesListAdapter(this,getData());


        lisyView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lisyView.setLayoutManager(mLayoutManager);
        lisyView.setAdapter(adapter);

        adapter.setClickListener(this);


        mSwipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout)
                findViewById(R.id.refresh);

        mSwipeRefreshLayout.setOnRefreshListener(this);


        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary
        );



    }

    public List<Category> getData() {

        List <Category> results = new ArrayList<>();

        RealmList<Category> listCats = CategoryController.list();

        for(Category cat : listCats){
            if(cat.getNumCat()>0)
            results.add(cat);
        }


        return results;
    }

    @Override
    public void itemClicked(View view, int position) {

        try {

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            Intent intent = new Intent(this,ListStoresActivity.class);
            intent.putExtra("category",adapter.getItem(position).getNumCat());
            realm.commitTransaction();
            overridePendingTransition(R.anim.lefttoright_enter, R.anim.lefttoright_exit);
            startActivity(intent);

        }catch (Exception e){

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(android.R.id.home==item.getItemId()){
            finish();
            overridePendingTransition(R.anim.righttoleft_enter, R.anim.righttoleft_exit);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.righttoleft_enter, R.anim.righttoleft_exit);

    }


    public void initToolbar(){

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
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

        TypefaceHelper.typeface(APP_DESC_VIEW);
        TypefaceHelper.typeface(APP_TITLE_VIEW);

        APP_DESC_VIEW.setVisibility(View.GONE);

    }


    @Override
    public void onRefresh() {

        getCategories();

    }


    private void getCategories(){

        if(!ServiceHandler.isNetworkAvailable(this)){
            if(CategoryController.list().size()==0){
                //database.insertCats(Loader.parseCategoriesFromAssets(this));
            }
        }

        queue = VolleySingleton.getInstance(this).getRequestQueue();

        SimpleRequest request = new SimpleRequest(Request.Method.GET,
                Constances.API.API_USER_GET_CATEGORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                mSwipeRefreshLayout.setRefreshing(false);

                try{

                    if(APP_DEBUG) { Log.e("catsResponse",response);}

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("response", jsonObject.toString());
                    final CategoryParser mCategoryParser = new CategoryParser(jsonObject);
                    int success = Integer.parseInt(mCategoryParser.getStringAttr(Tags.SUCCESS));

                    if(success==1){
                        //database.deleteCats();
                        CategoryController.insertCategories(
                                mCategoryParser.getCategories()
                        );
                    }

                }catch (JSONException e){
                    //send a rapport to support
                    e.printStackTrace();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(APP_DEBUG) { Log.e("ERROR", error.toString());}

                mSwipeRefreshLayout.setRefreshing(false);

            }


        }) {



        };

        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);



    }

}
