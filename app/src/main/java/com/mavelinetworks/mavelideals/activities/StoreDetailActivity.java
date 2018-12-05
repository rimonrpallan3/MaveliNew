package com.mavelinetworks.mavelideals.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.GPS.Position;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Category;
import com.mavelinetworks.mavelideals.classes.Discussion;
import com.mavelinetworks.mavelideals.classes.Store;
import com.mavelinetworks.mavelideals.classes.User;
import com.mavelinetworks.mavelideals.controllers.CampagneController;
import com.mavelinetworks.mavelideals.controllers.SettingsController;
import com.mavelinetworks.mavelideals.controllers.categories.CategoryController;
import com.mavelinetworks.mavelideals.controllers.sessions.SessionsController;
import com.mavelinetworks.mavelideals.controllers.stores.StoreController;
import com.mavelinetworks.mavelideals.fragments.StoreOffersFragment;
import com.mavelinetworks.mavelideals.fragments.StoreReviewsFragment;
import com.mavelinetworks.mavelideals.load_manager.ViewManager;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.StoreParser;
import com.mavelinetworks.mavelideals.unbescape.html.HtmlEscape;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;
import static com.mavelinetworks.mavelideals.appconfig.AppConfig.BASE_URL;
import static com.mavelinetworks.mavelideals.appconfig.AppConfig.SHOW_ADS;
import static com.mavelinetworks.mavelideals.appconfig.AppConfig.SHOW_ADS_IN_STORE;

public class StoreDetailActivity extends AppCompatActivity implements ViewManager.CustomView,
         GoogleMap.OnMapLoadedCallback, View.OnClickListener, OnMapReadyCallback {


    private static boolean opened=false;
    public static boolean isOpend(){
        return opened;
    }


    public ViewManager mViewManager;
    private Context context;
    ////////////////////////MAPPING
    private GoogleMap mMap;
    private List<String> listImages;
    private LatLng customerPosition;
    private LatLng myPosition;
    private Toolbar toolbar;
    private Store storedata;
    private ImageButton phoneBtn, mapBtn, saveBtn, shareBtn, unsaveBtn;
    private ImageView image , btnChat;


    private TextView address, description,category_content;
    private TextView distanceView, nbrPictures;

    private GPStracker mGPS;
    private ParallaxScrollView mScroll;
    //init request http
    private RequestQueue queue;
    private SharedPreferences preferences;
    private User user;
    private String pseudo;
    private LinearLayout  progressMapLL;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    ///////////////////////////////////////
    private String TAG = ".StoreActivity";
    private int success = 0;
    private float votes = -1;
    private String comment = null;
    private TextView APP_TITLE_VIEW = null;
    private TextView APP_DESC_VIEW = null;
    private boolean IS_READY_FOR_LOCATION = false;
    private static boolean isFirstTime=true;


    private TextView offersBtn,reviewsBtn;

    private void attachMap(){

        try {

            SupportMapFragment mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapping);
            if (mSupportMapFragment == null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                mSupportMapFragment = SupportMapFragment.newInstance();
                mSupportMapFragment.setRetainInstance(true);
                fragmentTransaction.replace(R.id.mapping, mSupportMapFragment).commit();
            }
            if (mSupportMapFragment != null) {
                mSupportMapFragment.getMapAsync(StoreDetailActivity.this);
            }

        }catch (Exception e){
            progressMapLL.setVisibility(View.GONE);
        }

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap = googleMap;
        if (storedata.getLatitude() != null && storedata.getLatitude() != null) {

            double TraderLat = storedata.getLatitude();
            double TraderLng = storedata.getLongitude();
            customerPosition = new LatLng(TraderLat, TraderLng);
            //INITIALIZE MY LOCATION
            GPStracker trackMe = new GPStracker(this);
            myPosition = new LatLng(trackMe.getLatitude(), trackMe.getLongitude());

            if(APP_DEBUG)
                Log.e("__lat", String.valueOf(customerPosition.latitude));
            moveToPosition(mMap, customerPosition);
        }

        progressMapLL.setVisibility(View.GONE);

    }

    private void moveToPosition(GoogleMap gm,LatLng targetPosition){

        gm.moveCamera(CameraUpdateFactory.newLatLngZoom(targetPosition, 16));
        gm.getUiSettings().setZoomControlsEnabled(true);
        gm.addMarker(new MarkerOptions()
                .title(context.getString(R.string.your_destination))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                .position(targetPosition));
    }


    @Override
    protected void onDestroy() {

        if(mAdView!=null)
                mAdView.destroy();


        super.onDestroy();
        opened = false;

        final android.app.FragmentManager fragManager = this.getFragmentManager();
        final Fragment fragment = fragManager.findFragmentById(R.id.mapping);
        if(fragment!=null){
            fragManager.beginTransaction().remove(fragment).commit();
        }
    }

    private final void focusOnView(final int redId){

        mScroll.post(new Runnable() {
            @Override
            public void run() {
                //mScroll.scrollTo(0, (findViewById(redId)).getBottom());
               // mScroll.fullScroll(View.FOCUS_DOWN);

                View lastChild = mScroll.getChildAt(mScroll.getChildCount() - 1);
                int bottom = lastChild.getBottom() + mScroll.getPaddingBottom();
                int sy = mScroll.getScrollY();
                int sh = mScroll.getHeight();
                int delta = bottom - (sy + sh);

                mScroll.smoothScrollBy(0, delta);
            }
        });


    }

    @Override
    protected void onPause() {

        if(mAdView!=null)
        mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {

        if(mAdView!=null)
         mAdView.resume();
        super.onResume();

//        mScroll.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mScroll.requestFocusFromTouch();
//                return false;
//            }
//        });

    }

    private User mUserSession;

    private AdView mAdView;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_store_detail);

        try {

            Uri uri = getIntent().getData();
            String path = uri.getPath();
            Toast.makeText(getApplicationContext(),path,Toast.LENGTH_LONG).show();
        }catch (Exception e){

        }


        if (SHOW_ADS && SHOW_ADS_IN_STORE) {


            mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("FFD811D6CAB26FA340E98A773B3408ED")
                    .addTestDevice("3CB74DFA141BF4D0823B8EA7D94531B5")
                    .build();
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
            findViewById(R.id.adsLayout).setVisibility(View.VISIBLE);

        }else
            findViewById(R.id.adsLayout).setVisibility(View.GONE);



        offersBtn = (TextView) findViewById(R.id.offersBtn);
        reviewsBtn = (TextView) findViewById(R.id.reviewsBtn);

        queue = VolleySingleton.getInstance(this).getRequestQueue();

        TypefaceHelper.typeface(this);

        //GET USER SESSION
        if(SessionsController.isLogged())
            mUserSession = SessionsController.getSession().getUser();
        //INIT TOOLBAR
        initToolbar();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //GET DATA
        if(isFirstTime==true){

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    getStore();
                }
            },1000);

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    isFirstTime = false;
                    // openMap();
                    mViewManager.showResult();
                }
            },1000);

        }else{

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mViewManager.showResult();
                    getStore();
                }
            },500);
        }

        invalidateOptionsMenu();

        context = this;

        mScroll = (ParallaxScrollView) findViewById(R.id.mScroll);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);




        //Initialize map fragment

//        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapping);
//        mapFrag.getMapAsync(this);


        mGPS = new GPStracker(this);

        image = (ImageView) findViewById(R.id.image);
        btnChat = (ImageView) findViewById(R.id.btn_chat_customer);
        address = (TextView) findViewById(R.id.address_content);
        description = (TextView) findViewById(R.id.description_content);
        category_content = (TextView) findViewById(R.id.category_content);

        nbrPictures = (TextView) findViewById(R.id.nbrPictures);
        distanceView = (TextView) findViewById(R.id.distanceView);

        mapBtn = (ImageButton) findViewById(R.id.mapBtn);
        saveBtn = (ImageButton) findViewById(R.id.saveBtn);
        shareBtn = (ImageButton) findViewById(R.id.shareBtn);
        shareBtn.setVisibility(View.GONE); //HIDE THE SHARE BTN
        unsaveBtn = (ImageButton) findViewById(R.id.deleteBtn);
        phoneBtn = (ImageButton) findViewById(R.id.phoneBtn);
        progressMapLL = (LinearLayout) findViewById(R.id.progressMapLL);

        if(!AppConfig.ENABLE_CHAT){

            btnChat.setVisibility(View.GONE);

            LinearLayout btnsLayout = (LinearLayout) findViewById(R.id.btnsLayout);
            btnsLayout.setWeightSum(9f);
            //btnsLayout.setPadding(1,1,1,1);

            btnChat.setVisibility(View.GONE);

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.weight = 3f;


            saveBtn.setLayoutParams(lp);
            unsaveBtn.setLayoutParams(lp);
            phoneBtn.setLayoutParams(lp);
            mapBtn.setLayoutParams(lp);

        }




        //  INIT BUTTON CLICK LISTNER
        //make links in a TextView clickable
        description.setMovementMethod(LinkMovementMethod.getInstance());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (storedata != null) {

                    StoreController.doSave(storedata.getId());
                    saveBtn.setVisibility(View.GONE);
                    unsaveBtn.setVisibility(View.VISIBLE);

                    if(SessionsController.isLogged())
                        doSave(mUserSession.getId(),storedata.getId());

                }

            }
        });



        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject json = new JSONObject();
                Intent intent = new Intent(StoreDetailActivity.this, SliderActivity.class);
                try {

                    for (int i = 0; i < storedata.getListImages().size(); i++) {
                        json.put(i + "", storedata.getListImages().get(i).getUrlFull());
                    }
                    intent.putExtra(Store.Tags.LISTIMAGES, json.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(APP_DEBUG)
                    Log.e("mages",json.toString());


                startActivity(intent);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (storedata != null && mGPS.canGetLocation()) {

                    Intent intent = new Intent(StoreDetailActivity.this, MapDirectionActivity.class);
                    intent.putExtra("latitude", storedata.getLatitude() + "");
                    intent.putExtra("longitude", storedata.getLongitude() + "");
                    intent.putExtra("name", storedata.getName() + "");
                    intent.putExtra("description", storedata.getAddress() + "");
                    intent.putExtra("distance", storedata.getDistance() + "");

                    startActivity(intent);

                } else if (!mGPS.canGetLocation() || !ServiceHandler.isNetworkAvailable(context)){
                    mGPS.showSettingsAlert();
                    Toast.makeText(StoreDetailActivity.this, "Enable network !", Toast.LENGTH_LONG).show();
                }
            }
        });


        unsaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storedata != null) {

                    StoreController.doDelete(storedata.getId());
                    unsaveBtn.setVisibility(View.GONE);
                    saveBtn.setVisibility(View.VISIBLE);

                    if(SessionsController.isLogged())
                        doUnsave(mUserSession.getId(),storedata.getId());

                }
            }
        });


        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //INIT VIEW MANAGER
        mViewManager = new ViewManager(this);
        mViewManager.setLoading(findViewById(R.id.loading));
        mViewManager.setNoLoading(findViewById(R.id.content_my_store));
        mViewManager.setError(findViewById(R.id.error));
        mViewManager.setEmpty(findViewById(R.id.empty));
        mViewManager.setCustumizeView(this);

        mViewManager.loading();

        try {
            int  cid = Integer.parseInt( getIntent().getExtras().getString("cid"));
            CampagneController.markView(cid);
        }catch (Exception e){

        }
    }


    private void openMap() {


    }



    private void shareStore() {


        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                storedata.getName()+"\n"+
                        BASE_URL+"/storeid="+storedata.getId()
        );

        sendIntent.putExtra(StoreDetailActivity.class.getName(),storedata.getId());

        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }

    @Override
    public void customErrorView(View v) {

    }

    @Override
    public void customLoadingView(View v) {

    }

    @Override
    public void customEmptyView(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        /////////////////////////////
        menu.findItem(R.id.rate_review).setVisible(false);
        Drawable review = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_thumb_up_outline)
                .color(ResourcesCompat.getColor(getResources(),R.color.defaultColor,null))
                .sizeDp(22);
        menu.findItem(R.id.rate_review).setIcon(review);
        //////////////////////////////

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (storedata!=null && !storedata.isVoted()) {
                    menu.findItem(R.id.rate_review).setVisible(true);
                }
            }
        },5000);

        /////////////////////////////
        menu.findItem(R.id.send_location).setVisible(true);
        Drawable send_location = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_share_variant)
                .color(ResourcesCompat.getColor(getResources(),R.color.defaultColor,null))
                .sizeDp(22);
        menu.findItem(R.id.send_location).setIcon(send_location);
        /////////////////////////////


        return true;
    }


    @Override
    public void onStart() {
        super.onStart();

        Drawable heart = new IconicsDrawable(context)
                .icon(CommunityMaterial.Icon.cmd_heart)
                .color(ResourcesCompat.getColor(context.getResources(),R.color.colorPrimaryDark,null))
                .sizeDp(24);

        Drawable heart_outline = new IconicsDrawable(context)
                .icon(CommunityMaterial.Icon.cmd_heart_outline)
                .color(ResourcesCompat.getColor(context.getResources(),R.color.white,null))
                .sizeDp(24);


        saveBtn.setImageDrawable(heart_outline);
        unsaveBtn.setImageDrawable(heart);
        unsaveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.white,null));



        opened = true;
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //AppIndex.AppIndexApi.end(client, getIndexApiAction());
        //client.disconnect();
    }

    public void initToolbar() {

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setSubtitle("E-shop");
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_36dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        APP_TITLE_VIEW = (TextView) toolbar.findViewById(R.id.toolbar_title);
        APP_DESC_VIEW = (TextView) toolbar.findViewById(R.id.toolbar_description);

        APP_DESC_VIEW.setVisibility(View.GONE);
        Utils.setFont(this, APP_DESC_VIEW, Constances.Fonts.BOLD);
        Utils.setFont(this, APP_TITLE_VIEW, Constances.Fonts.BOLD);

        APP_TITLE_VIEW.setText(R.string.store_title_detail);
        APP_DESC_VIEW.setVisibility(View.GONE);

    }




    private void getStore(){

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Bundle bundle = getIntent().getExtras();
        int store_id = bundle.getInt("id");
        if(store_id==0)
            store_id = Integer.parseInt(bundle.getString("id"));


        if(APP_DEBUG)
            Log.e("_2_store_id", String.valueOf(store_id));

        storedata = StoreController.getStore(store_id);

        if(storedata!=null && storedata.isLoaded() && storedata!=null){
            putDataIntoViews();
        }else{
           syncStore(store_id);
        }

        realm.commitTransaction();
    }

    private void putDataIntoViews() {

        if (storedata.getListImages().size() > 0) {

            findViewById(R.id.mapcontainer).setVisibility(View.VISIBLE);

            Picasso.with(getBaseContext())
                    .load(storedata.getListImages().get(0)
                            .getUrl500_500())
                    .fit().centerCrop().placeholder(R.drawable.def_logo)
                    .into(image);

        } else {

            Picasso.with(getBaseContext())
                    .load(R.drawable.def_logo)
                    .fit().centerCrop().placeholder(R.drawable.def_logo)
                    .into(image);

        }

        Drawable markerDrwable = new IconicsDrawable(context)
                .icon(CommunityMaterial.Icon.cmd_map_marker)
                .color(ResourcesCompat.getColor(context.getResources(),R.color.colorPrimary,null))
                .sizeDp(18);

        address.setText(storedata.getAddress());
        address.setCompoundDrawablePadding(10);
        address.setCompoundDrawables(markerDrwable,null,null,null);
        double votes = storedata.getVotes();
        phoneBtn.setOnClickListener(this);

        if(storedata.getPhone().trim().equals("")){
            phoneBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.gray_field,null));
            phoneBtn.setEnabled(false);
        }

        if (StoreController.isSaved(storedata.getId())) {
            saveBtn.setVisibility(View.GONE);
            unsaveBtn.setVisibility(View.VISIBLE);
        } else {
            unsaveBtn.setVisibility(View.GONE);
            saveBtn.setVisibility(View.VISIBLE);
        }

        if (storedata.getListImages().size() > 1) {

            Drawable camera = new IconicsDrawable(context)
                    .icon(CommunityMaterial.Icon.cmd_camera)
                    .color(ResourcesCompat.getColor(context.getResources(),R.color.white,null))
                    .sizeDp(12);

            nbrPictures.setText(storedata.getListImages().size()+"");
            nbrPictures.setCompoundDrawables(camera,null,null,null);
            nbrPictures.setCompoundDrawablePadding(10);

        }else{
            nbrPictures.setVisibility(View.GONE);
        }

        Position newPosition = new Position();
        Double mDistance = newPosition.distance(mGPS.getLatitude(),mGPS.getLongitude(),storedata.getLatitude(),storedata.getLongitude());
        distanceView.setText(
                Utils.preparDistance(mDistance)
                        +" "+
                Utils.getDistanceBy(mDistance).toUpperCase()
        );

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SessionsController.isLogged()) {

                    int userId = 0;
                    try {
                        userId = storedata.getUser().getId();
                    }catch (Exception e){
                        userId = storedata.getUser_id();
                    }

                    Intent intent = new Intent(StoreDetailActivity.this, MessengerActivity.class);
                    intent.putExtra("type", Discussion.DISCUSION_WITH_USER);
                    intent.putExtra("userId", userId);
                    intent.putExtra("storeName", storedata.getName());
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        Drawable location = new IconicsDrawable(context)
                .icon(CommunityMaterial.Icon.cmd_map_marker)
                .color(ResourcesCompat.getColor(context.getResources(),R.color.white,null))
                .sizeDp(12);


        APP_TITLE_VIEW.setText(storedata.getName());
        APP_DESC_VIEW.setText(storedata.getAddress());
        APP_DESC_VIEW.setVisibility(View.VISIBLE);
        APP_DESC_VIEW.setCompoundDrawablePadding(10);
        APP_DESC_VIEW.setCompoundDrawables(location,null,null,null);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                attachMap();
            }
        },3000);

        new decodeHtml().execute(storedata.getDetail());

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showOfferFrag();
            }
        },3000);

        offersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOfferFrag();
            }
        });

        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviewsFrag();
            }
        });

        offersBtn.setText(String.format(getString(R.string.offers)));
        reviewsBtn.setText(getString(R.string.review_title));



        try {

            final Category cat = CategoryController.findId(storedata.getCategory_id());
            category_content.setText(cat.getNameCat());

            findViewById(R.id.category_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(StoreDetailActivity.this,ListStoresActivity.class);
                    intent.putExtra("category",cat.getNumCat());
                    overridePendingTransition(R.anim.lefttoright_enter, R.anim.lefttoright_exit);
                    startActivity(intent);

                }
            });

            ImageView imageCat = (ImageView) findViewById(R.id.catImage);

            if(cat.getImages()!=null){

              Picasso.with(this).load(cat.getImages().getUrl200_200()).fit().centerCrop().into(imageCat);

            }

            Drawable categoryDrawable = new IconicsDrawable(context)
                    .icon(CommunityMaterial.Icon.cmd_format_list_bulleted)
                    .color(ResourcesCompat.getColor(context.getResources(),R.color.defaultColorText,null))
                    .sizeDp(18);
                category_content.setCompoundDrawables(categoryDrawable,null,null,null);
            category_content.setCompoundDrawablePadding(15);


        }catch (Exception e){

            findViewById(R.id.category_layout).setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.phoneBtn){
            try {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + storedata.getPhone().trim()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    String[] permission = new String[]{ Manifest.permission.CALL_PHONE};
                    SettingsController.requestPermissionM(StoreDetailActivity.this,permission);

                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.store_call_error) + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }



    private void doSave(final int user_id, final int store_id){

        saveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.gray_field,null));
        unsaveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.gray_field,null));

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_SAVE_STORE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                    if(APP_DEBUG)
                        Log.e("response", response);

                   Toast.makeText(StoreDetailActivity.this,R.string.saved_messag,Toast.LENGTH_LONG).show();
                saveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.colorPrimary,null));
                unsaveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.white,null));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(APP_DEBUG) {   Log.e("ERROR", error.toString());}

                saveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.colorPrimary,null));
                unsaveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.white,null));

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", String.valueOf(user_id));
                params.put("store_id", String.valueOf(store_id));

                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }


    private void doUnsave(final int user_id, final int store_id){

        saveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.gray_field,null));
        unsaveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.white,null));


        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_REMOVE_STORE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                saveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.colorPrimary,null));
                unsaveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.white,null));

                if(APP_DEBUG) { Log.e("response", response); }

                Toast.makeText(StoreDetailActivity.this,getString(R.string.unsaved_message),Toast.LENGTH_LONG).show();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(APP_DEBUG) {   Log.e("ERROR", error.toString());}

                saveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.colorPrimary,null));
                unsaveBtn.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.white,null));

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", String.valueOf(user_id));
                params.put("store_id", String.valueOf(store_id));

                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }


    @Override
    public void onMapLoaded() {

        Toast.makeText(getApplicationContext(),"Map is ready ",Toast.LENGTH_SHORT).show();
    }



    public void syncStore(final int store_id) {

        mViewManager.loading();

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_GET_STORES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (APP_DEBUG) {
                        Log.e("responseStoresString", response);
                    }

                    JSONObject jsonObject = new JSONObject(response);

                    //Log.e("response",response);

                    final StoreParser mStoreParser = new StoreParser(jsonObject);
                    RealmList<Store> list = mStoreParser.getStore();

                    if(list.size()>0){

                        StoreController.insertStores(list);

                        storedata = list.get(0);
                        putDataIntoViews();

                        mViewManager.showResult();



                    }else{


                        Toast.makeText(StoreDetailActivity.this,getString(R.string.not_found),Toast.LENGTH_LONG).show();
                        finish();

                    }

                } catch (JSONException e) {
                    //send a rapport to support
                    e.printStackTrace();

                    mViewManager.error();

                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (APP_DEBUG) {
                    Log.e("ERROR", error.toString());
                }
                mViewManager.error();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                //
                params.put("limit", "1");
                params.put("store_id", String.valueOf(store_id));


                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);


    }


    private class decodeHtml extends AsyncTask<String,String,String> {

        @Override
        protected void onPostExecute(final String text) {
            super.onPostExecute(text);
            description.setText(Html.fromHtml(text));
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        storedata.setDetail(text);
                        realm.copyToRealmOrUpdate(storedata);
                    }catch (Exception e){

                    }

                }
            });
            //eventData.setDescription(text);
        }

        @Override
        protected String doInBackground(String... params) {

            return HtmlEscape.unescapeHtml(params[0]);
        }
    }



    private void showOfferFrag(){

        getOffersFragment();

        offersBtn.setTextColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null));
        offersBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.colorWhite,null));

        reviewsBtn.setTextColor(ResourcesCompat.getColor(getResources(),R.color.colorWhite,null));
        reviewsBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null));

    }

    private void showReviewsFrag(){

        getReviewsFragment();

        offersBtn.setTextColor(ResourcesCompat.getColor(getResources(),R.color.colorWhite,null));
        offersBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null));

        reviewsBtn.setTextColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null));
        reviewsBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.colorWhite,null));
    }



    private void getReviewsFragment(){

        try {
            StoreReviewsFragment frag =new StoreReviewsFragment();
            Bundle b = new Bundle();

            if(APP_DEBUG)
                Log.e("_3_store_id", String.valueOf(storedata.getId()));

            b.putInt("store_id",storedata.getId());
            frag.setArguments(b);

            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.scontainer,frag).commit();
        }catch (Exception e){
            if(APP_DEBUG)
                e.printStackTrace();
        }

    }

    private void getOffersFragment(){

        try {
            StoreOffersFragment frag =new StoreOffersFragment();
            Bundle b = new Bundle();

            b.putInt("store_id",storedata.getId());
            frag.setArguments(b);

            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.scontainer,frag).commit();

        }catch (Exception e){
            if(APP_DEBUG)
                e.printStackTrace();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if(!MainActivity.isOpend()){
                startActivity(new Intent(this,MainActivity.class));
            }
            finish();
        }
//        else if (item.getItemId() == R.id.action_map) {
//            openMap();
//        }
        else if (item.getItemId() == R.id.rate_review) {

            focusOnView(R.id.scontainer);
            showReviewsFrag();

        }else if (item.getItemId() == R.id.send_location) {


            double lat = storedata.getLatitude();
            double lon = storedata.getLongitude();
            String link = null;

            try {
                //https://www.google.com/maps/search/?api=1&query=47.5951518,-122.3316393
                link = "https://maps.google.com/?q="+ URLEncoder.encode(storedata.getAddress(),"UTF-8")+"&ll="+ String.format("%f,%f", lat, lon);
            } catch (UnsupportedEncodingException e) {
                link = "https://maps.google.com/?ll=" + String.format("%f,%f", lat, lon);
                e.printStackTrace();
            }


            @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String shared_text =
                    String.format(getString(R.string.shared_text),
                    getString(R.string.app_name),
                    storedata.getName(),
                    storedata.getAddress(),
                    link
            );

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shared_text);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(!MainActivity.isOpend()){
            startActivity(new Intent(this,MainActivity.class));
        }
        super.onBackPressed();
    }
}
