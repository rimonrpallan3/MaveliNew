package com.mavelinetworks.mavelideals.activities;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mavelinetworks.mavelideals.AppController;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.Services.BusMessage;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.controllers.SettingsController;
import com.mavelinetworks.mavelideals.controllers.categories.CategoryController;
import com.mavelinetworks.mavelideals.controllers.users.UserController;
import com.mavelinetworks.mavelideals.dtmessenger.MessengerHelper;
import com.mavelinetworks.mavelideals.fragments.MainFragment;
import com.mavelinetworks.mavelideals.load_manager.ViewManager;
import com.mavelinetworks.mavelideals.navigationdrawer.NavigationDrawerFragment;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.CategoryParser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;
import static com.mavelinetworks.mavelideals.appconfig.AppConfig.SHOW_ADS;
import static com.mavelinetworks.mavelideals.appconfig.AppConfig.SHOW_INTERSTITIAL_ADS_IN_STARTUP;
import static com.mavelinetworks.mavelideals.appconfig.AppConfig.SHOW_ADS_IN_HOME;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static boolean opened=false;
    private Context context;

    public static boolean isOpend(){
        return opened;
    }



    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static int height = 0;
    public static int width = 0;
    //Access location permissions
    private  final String[] permissonNeeded = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    public ViewManager mViewManager;
    Toolbar toolbar;
    private String TAG = ".MainActivity";
    private InterstitialAd mInterstitialAd;
    /**********************************************/

    //init request http
    private RequestQueue queue;
    private TextView APP_TITLE_VIEW = null;
    private TextView APP_DESC_VIEW = null;

    //SHARED ¨PREFERENCES

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onDestroy() {

        if(mAdView!=null)
             mAdView.destroy();
        super.onDestroy();
        opened = false;
    }

    private Tracker mTracker;

    /************   EVENT ALERT *******************/

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        context = this;

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();


        //initialize the Google Mobile Ads SDK at app launch
        MobileAds.initialize(getApplicationContext(),getString(R.string.ad_app_id));

        Display display = getWindowManager().getDefaultDisplay();
        width = getScreenWidth();
        height = display.getHeight();

        //Initialize web service API
        queue = VolleySingleton.getInstance(this).getRequestQueue();

        //Show Interstitial Ads

        //Show Banner Ads
        if(SHOW_ADS && SHOW_ADS_IN_HOME) {

            if(AppConfig.APP_DEBUG)
                Toast.makeText(this,"SHOW_ADS_IN_HOME:"+getResources()
                        .getString(R.string.banner_ad_unit_id),Toast.LENGTH_LONG).show();

             mAdView = (AdView) findViewById(R.id.adView);
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("4A55E4EA2535643C0D74A5A73C4F550A")
                    .addTestDevice("3CB74DFA141BF4D0823B8EA7D94531B5")
                    .build();


            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener(){
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    mAdView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
        }


        //Apply permission for all devices (Version > 5)
       requestPermissionM(permissonNeeded);


        mViewManager = new ViewManager(this);
        mViewManager.setLoading(findViewById(R.id.loading));
        mViewManager.setNoLoading(findViewById(R.id.content_my_store));
        mViewManager.setError(findViewById(R.id.error));
        mViewManager.setEmpty(findViewById(R.id.empty));
        mViewManager.showResult();

        initToolbar();


        int size = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

        switch (size){

            case Configuration.SCREENLAYOUT_SIZE_XLARGE:


                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                NavigationDrawerFragment frag = new NavigationDrawerFragment();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //transaction.setCustomAnimations(R.animator.fade_in_listoffres, R.animator.fade_out_listoffres);

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.nav_container, frag, MainFragment.TAG);
                //transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

                break;

            default:

                NavigationDrawerFragment NaDrawerFrag =
                        (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.frag_nav_drawer);

                NaDrawerFrag.setUp(
                        R.id.frag_nav_drawer,
                        (android.support.v4.widget.DrawerLayout) findViewById(R.id.drawerLayout),
                        toolbar);
                break;
        }






        MainFragment frag = new MainFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.animator.fade_in_listoffres, R.animator.fade_out_listoffres);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.main_container, frag,MainFragment.TAG);
        //transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();



        getCategories();


        //Show Interstitial Ads
        if(SHOW_ADS && SHOW_INTERSTITIAL_ADS_IN_STARTUP) {
            //show ad
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestNewInterstitial();
                }
            }, 5000);
        }


        UserController.checkUserConnection(this);


        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                getUpcomingEvent();
            }
        }, 6000);


    }

    @Override
    protected void onPause() {

        if(mAdView!=null)
                mAdView.pause();
        super.onPause();

    }



    public static Menu mainMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        mainMenu = menu;
        updateBadge();

        return true;
    }


    private void updateBadge(){

        if (MessengerHelper.NbrMessagesManager.getNbrTotalMessages() > 0) {
            ActionItemBadge.update(this, mainMenu.findItem(R.id.item_samplebadge), CommunityMaterial.Icon.cmd_bell_ring_outline,
                    ActionItemBadge.BadgeStyles.RED,
                    MessengerHelper.NbrMessagesManager.getNbrTotalMessages());
        } else {
            ActionItemBadge.hide(mainMenu.findItem(R.id.item_samplebadge));
        }
    }


    public static void updateBadge(FragmentActivity activity){

        try {

            if (MessengerHelper.NbrMessagesManager.getNbrTotalMessages() > 0) {
                ActionItemBadge.update(activity, mainMenu.findItem(R.id.item_samplebadge), CommunityMaterial.Icon.cmd_bell_ring_outline,
                        ActionItemBadge.BadgeStyles.RED,
                        MessengerHelper.NbrMessagesManager.getNbrTotalMessages());
            } else {
                ActionItemBadge.hide(mainMenu.findItem(R.id.item_samplebadge));
            }

        }catch (Exception e){

        }

    }


    //Manage menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


       if(item.getItemId()==R.id.map_action){

           startActivity(new Intent(this, MapStoresListActivity.class));
           overridePendingTransition(R.anim.lefttoright_enter, R.anim.lefttoright_exit);


        } else if(R.id.item_samplebadge==item.getItemId()){

            MainFragment.getPager().setCurrentItem(3);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        settingsrequest();
        EventBus.getDefault().register(this);
        opened = true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onNewNotifs(BusMessage bus){

        if(bus.getType()==BusMessage.GET_NBR_NEW_NOTIFS){
            if(AppConfig.APP_DEBUG)
                if(MessengerHelper.NbrMessagesManager.getNbrTotalMessages()> 0) {
                    Toast.makeText(this, "New message " + MessengerHelper.NbrMessagesManager.getNbrTotalMessages()
                            , Toast.LENGTH_LONG).show();
                }
            updateBadge();
        }

    }


    //DIALOG PLUS BUTTON LISTNER
   /* OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(DialogPlus dialog, View view) {




        }
    };*/

    public void initToolbar(){

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setSubtitle("E-shop");
        getSupportActionBar().setTitle("E-COMMERCE");
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



    @Override
    protected void onResume() {
        super.onResume();

        if(mAdView!=null)
            mAdView.resume();

        mTracker.setScreenName("Image~"+MainActivity.class.getName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        try {
            updateBadge();
        }catch (Exception e){}
    }

    //Get all categories from server and save them in  the database
    private void getCategories(){


        SimpleRequest request = new SimpleRequest(Request.Method.GET,
                Constances.API.API_USER_GET_CATEGORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    if(APP_DEBUG) {  Log.e("catsResponse",response); }

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("response", jsonObject.toString());
                    final CategoryParser mCategoryParser = new CategoryParser(jsonObject);
                    int success = Integer.parseInt(mCategoryParser.getStringAttr(Tags.SUCCESS));
                    if(success==1){
                        //database.deleteCats();
                        //update list categories
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
                if(APP_DEBUG) {  Log.e("ERROR", error.toString()); }


            }


        }) {



        };

        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        GPStracker gps = new GPStracker(this);
                        gps.getLongitude();
                        gps.getLatitude();
                        break;
                    case Activity.RESULT_CANCELED:
                        //settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }


    public void requestPermissionM(String[] perms) {

        for(String permission :perms) {// Here, thisActivity is the current activity

            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    permission)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        permission)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    Toast.makeText(this, R.string.permission_disabled_notice,Toast.LENGTH_LONG).show();
                } else {

                    // No explanation needed, we can request the permission.
                    try{
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{permission}, 101);
                    }catch(Exception e)
                        {
                            Log.e("Permission",e.getMessage());
                        }

                    }
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an

                if(AppConfig.APP_DEBUG)
                    Toast.makeText(this, "Permission garaunteed successful!",Toast.LENGTH_LONG).show();

            }


        }
    }




    public void settingsrequest()
    {

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        String event ;
        if(extras != null)
        {
            event = extras.getString("Notified");
            if(APP_DEBUG) { Log.e("Notified","Event notified  "+event); }
        }else {
            if(APP_DEBUG) { Log.e("Notified", "Extras are NULL"); }

        }
    }


  /*  private void showNotification(int id ,String titleStore,String message) {

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;


        Intent i = new Intent(this,MainActivity.class);
        i.putExtra("Notified",id);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(titleStore)
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);



        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        manager.notify(m,builder.build());
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    GPStracker gps = new GPStracker(this);
                    gps.getLongitude();
                    gps.getLatitude();

                        //settingsrequest();
                    // keep asking if imp or do whatever
                   if(APP_DEBUG) {Log.e("PermissionRequest","Granted"); }
                } else {
                    if(APP_DEBUG) { Log.e("PermissionRequest","Not Granted"); }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getUpcomingEvent()
    {
        //check first if the upcoming event checkbox is checked before launching


    }

    private void requestNewInterstitial() {

        if(AppConfig.APP_DEBUG)
        Toast.makeText(this,"requestNewInterstitial:"+getResources()
                .getString(R.string.ad_interstitial_id),Toast.LENGTH_LONG).show();
        // Load Interstitial Ad
        // Initializing InterstitialAd - ADMob
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_interstitial_id));
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("4A55E4EA2535643C0D74A5A73C4F550A")
                .addTestDevice("3CB74DFA141BF4D0823B8EA7D94531B5")
                .build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // Full screen advertise will show only after loading complete
                mInterstitialAd.show();
            }
        });
    }

    @Override
    public void onBackPressed() {

        NavigationDrawerFragment.getInstance().closeDrawers();

        if(AppConfig.RATE_US_FORCE){
            if(SettingsController.rateOnApp(this)){
                super.onBackPressed();
            }
        }else {
            super.onBackPressed();
        }

    }
}
