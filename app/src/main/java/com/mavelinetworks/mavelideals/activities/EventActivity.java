package com.mavelinetworks.mavelideals.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.mavelinetworks.mavelideals.GPS.GoogleDirection;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Event;
import com.mavelinetworks.mavelideals.controllers.CampagneController;
import com.mavelinetworks.mavelideals.controllers.SettingsController;
import com.mavelinetworks.mavelideals.controllers.events.EventController;
import com.mavelinetworks.mavelideals.controllers.events.UpComingEventsController;
import com.mavelinetworks.mavelideals.controllers.stores.StoreController;
import com.mavelinetworks.mavelideals.load_manager.ViewManager;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.EventParser;
import com.mavelinetworks.mavelideals.unbescape.html.HtmlEscape;
import com.mavelinetworks.mavelideals.utils.DateUtils;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;
import com.wuadam.awesomewebview.AwesomeWebView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import io.realm.Realm;
import io.realm.RealmList;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;
import static com.mavelinetworks.mavelideals.appconfig.AppConfig.SHOW_ADS;
import static com.mavelinetworks.mavelideals.appconfig.AppConfig.SHOW_ADS_IN_EVENT;

public class EventActivity extends AppCompatActivity implements ViewManager.CustomView, OnMapReadyCallback {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    LatLng customerPosition;
    LatLng myPosition;
    /*
    *   DECLARATION OF VIEWS
    */
    private PullToZoomScrollViewEx scrollView;
    private Toolbar toolbar;
    private TextView APP_TITLE_VIEW = null;
    private TextView APP_DESC_VIEW = null;
    private Button mParticipate;
    private Button mUnparticipate;
    private ViewManager mViewManager;
    private AdView mAdView;
    private ImageView image;
    private TextView name, description, tel, website, date, upcomingEvent;
    private LinearLayout layout_phone, layout_website;
    /*
    *   DECLARATION OF GOOGLE MAPS API
    */
    private GoogleMap mMap;
    private GoogleDirection gd;
    //TIMER
    private Timer timer;
    //DATABASE
    private Context context;
    //FloatingActionButton fab;
    //OBJECTS
    private Event eventData;
    private List<String> listImages;
    private LinearLayout event_store_layout;
    private TextView event_store_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        context = this;

        setuToolbar();



        //VIEW LAYOUT MANAGER
        mViewManager = new ViewManager(this);
        mViewManager.setLoading(findViewById(R.id.loading));
        mViewManager.setNoLoading(findViewById(R.id.content_my_store));
        mViewManager.setError(findViewById(R.id.error));
        mViewManager.setCustumizeView(this);
        mViewManager.showResult();

        invalidateOptionsMenu();


        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);

        loadViewForCode();


        //MAPS

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);

        //Floating Action Button
        mParticipate = (Button) findViewById(R.id.participate);
        Utils.setFontBold(this, mParticipate);

        mUnparticipate = (Button) findViewById(R.id.unparticipate);
        Utils.setFontBold(this, mUnparticipate);


        getEventFromDB();

        try {
            int  cid =Integer.parseInt( getIntent().getExtras().getString("cid"));
            CampagneController.markView(cid);
        }catch (Exception e){

        }

    }

    private void initializeBtn() {

        if (EventController.isEventLiked(eventData.getId())) {
            //mParticipate.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            mParticipate.setVisibility(View.GONE);
            mUnparticipate.setVisibility(View.VISIBLE);

        } else {

            mParticipate.setVisibility(View.VISIBLE);
            mUnparticipate.setVisibility(View.GONE);
            //fab.setImageResource(R.drawable.ic_favorite_white_24dp);
        }
    }

    @Override
    protected void onResume() {

        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();

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

    public void setuToolbar() {

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
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
        Utils.setFont(this, APP_DESC_VIEW,Constances.Fonts.BOLD);
        Utils.setFont(this, APP_TITLE_VIEW,Constances.Fonts.BOLD);

        APP_TITLE_VIEW.setText(R.string.event_detail);

        APP_DESC_VIEW.setVisibility(View.GONE);

    }

    private void loadViewForCode() {

        View headView = LayoutInflater.from(this).inflate(R.layout.fragment_event_head_view, null, false);
        View zoomView = LayoutInflater.from(this).inflate(R.layout.fragment_event_zoom_view, null, false);

        image = (ImageView) zoomView.findViewById(R.id.iv_zoom);


        View contentView = LayoutInflater.from(this).inflate(R.layout.fragment_event_content, null, false);
        //name = (TextView) contentView.findViewById(R.id.event_name);
        description = (TextView) contentView.findViewById(R.id.event_desc);
        tel = (TextView) contentView.findViewById(R.id.event_tel);
        website = (TextView) contentView.findViewById(R.id.event_website);
        date = (TextView) contentView.findViewById(R.id.event_date);
        upcomingEvent = (TextView) contentView.findViewById(R.id.upcoming);
        layout_phone =   (LinearLayout) contentView.findViewById(R.id.layout_phone);
        layout_website =  (LinearLayout) contentView.findViewById(R.id.layout_website);

        event_store_layout = (LinearLayout) contentView.findViewById(R.id.event_store_layout);
        event_store_view = (TextView) event_store_layout.findViewById(R.id.event_store_view);

        //ADD ADMOB BANNER
        if (SHOW_ADS && SHOW_ADS_IN_EVENT) {

            mAdView = (AdView) contentView.findViewById(R.id.adView);
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("4A55E4EA2535643C0D74A5A73C4F550A")
                    .addTestDevice("3CB74DFA141BF4D0823B8EA7D94531B5")
                    .build();
            mAdView.loadAd(adRequest);
        }

        // Utils.setFont(this, name, "");
        Utils.setFont(this, tel, "");
        Utils.setFont(this, description, "");
        Utils.setFont(this, website, "");
        Utils.setFont(this, date, "");

        //make links in a TextView clickable
        description.setMovementMethod(LinkMovementMethod.getInstance());


        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        putMap();

    }



    private void attachMap(){

        try {

            SupportMapFragment mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapping);
            if (mSupportMapFragment == null) {
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                mSupportMapFragment = SupportMapFragment.newInstance();
                mSupportMapFragment.setRetainInstance(true);
                fragmentTransaction.replace(R.id.mapping, mSupportMapFragment).commit();
            }
            if (mSupportMapFragment != null) {
                mSupportMapFragment.getMapAsync(this);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initMapping() {

        if (eventData.getLat() != null && eventData.getLng() != null) {

            double TraderLat = eventData.getLat();
            double TraderLng = eventData.getLng();

            customerPosition = new LatLng(TraderLat, TraderLng);
            customerPosition = new LatLng(TraderLat, TraderLng);
            //INITIALIZE MY LOCATION
            GPStracker trackMe = new GPStracker(this);

            myPosition = new LatLng(trackMe.getLatitude(), trackMe.getLongitude());

            attachMap();

        }else {
            if(AppConfig.APP_DEBUG){
                Toast.makeText(this,"Debug mode: Couldn't get position maps",Toast.LENGTH_LONG).show();
            }
            findViewById(R.id.mapcontainer).setVisibility(View.GONE);
        }

    }

    private void putMap(){

        if (mMap != null) {

            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerPosition, 16));
            //trader location

            Bitmap b = ((BitmapDrawable) Utils.changeDrawableIconMap(
                    EventActivity.this, R.drawable.ic_marker)).getBitmap();
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(b);


            mMap.addMarker(new MarkerOptions().position(customerPosition)
                    .title(eventData.getName())
                    .anchor(0.0f, 1.0f)
                    .icon(icon)
                    .snippet(eventData.getAddress())).showInfoWindow();

            mMap.addMarker(new MarkerOptions().position(myPosition)
                    .title(eventData.getName())
                    .anchor(0.0f, 1.0f)
                    .draggable(true)
                    //.icon(icon)
                    .snippet(eventData.getAddress())).showInfoWindow();

            if (ServiceHandler.isNetworkAvailable(this)) {
                try {
                    gd = new GoogleDirection(this);
                    //My Location
                    gd.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {

                        @Override
                        public void onResponse(String status, Document doc, GoogleDirection gd) {
                            mMap.addPolyline(gd.getPolyline(doc, 8,
                                    ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null)));
                            //mMap.setMyLocationEnabled(true);
                        }
                    });
                    gd.setLogging(true);
                    gd.request(myPosition, customerPosition, GoogleDirection.MODE_DRIVING);

                } catch (Exception e) {
                    if (APP_DEBUG)
                        e.printStackTrace();
                }

            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        /////////////////////////////
        menu.findItem(R.id.rate_review).setVisible(false);

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
    protected void onPause() {

        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (mAdView != null) {
            mAdView.destroy();
        }

    }




    private void putDataIntoViews(){

        APP_TITLE_VIEW.setText(eventData.getName());

        //description.setText(eventData.getAddress());
        description.setText(
                Html.fromHtml(/*HtmlEscape.unescapeHtml(*/eventData.getDescription()/*)*/)
        );

        String mDateEvent = String.format(context.getString(R.string.FromTo),
               DateUtils.getDateByTimeZone(eventData.getDateB(),"dd-MM-yyyy")+"",
                DateUtils.getDateByTimeZone(eventData.getDateE(),"dd-MM-yyyy")
        );

        date.setText((mDateEvent));

        if (eventData.getTel().length() > 0 && !eventData.getTel().equals("null")) {
            tel.setText(eventData.getTel());
            tel.setPaintFlags(tel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            layout_phone.setVisibility(View.GONE);

        }

        if (eventData.getListImages().size() > 0) {

            Picasso.with(getBaseContext())
                    .load(eventData.getListImages().get(0).getUrl500_500())
                    .fit().centerCrop().placeholder(R.drawable.def_logo)
                    .into(image);
        } else {

            Picasso.with(getBaseContext())
                    .load(R.drawable.def_logo)
                    .fit().centerCrop().placeholder(R.drawable.def_logo)
                    .into(image);

        }
        

        if (eventData.getWebSite().length() > 0 && !eventData.getWebSite().equalsIgnoreCase("null") ) {

            try {

                website.setText(eventData.getWebSite());
                website.setPaintFlags(website.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                website.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AwesomeWebView.Builder(EventActivity.this)
                                .statusBarColorRes(R.color.colorPrimary)
                                .theme(R.style.FinestWebViewAppTheme)
                                .show(eventData.getWebSite());


                    }
                });

            } catch (Exception e) {
                if (APP_DEBUG) {
                    Log.e("WebView", e.getMessage());
                }
            }


        } else {
            layout_website.setVisibility(View.GONE);
        }

        tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + eventData.getTel().trim()));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        String[] permission = new String[]{ Manifest.permission.CALL_PHONE};
                        SettingsController.requestPermissionM(EventActivity.this,permission);

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
        });


        if(APP_DEBUG){
            Log.e("eventStore",eventData.getStore_id()+" - "+eventData.getStore_name());
        }

        if(eventData.getStore_id()>0 && !eventData.getStore_name().equals("") && !eventData.getStore_name().toLowerCase()
                .equals("null")){

            event_store_layout.setVisibility(View.VISIBLE);
            event_store_view.setVisibility(View.VISIBLE);

            Drawable storeDrawable = new IconicsDrawable(context)
                    .icon(CommunityMaterial.Icon.cmd_map_marker)
                    .color(ResourcesCompat.getColor(context.getResources(),R.color.colorPrimary,null))
                    .sizeDp(18);

            event_store_view.setText(eventData.getStore_name());
            event_store_view.setCompoundDrawables(storeDrawable,null,null,null);
            event_store_view.setCompoundDrawablePadding(20);
            event_store_view.setTextColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null));
            event_store_view.setPaintFlags(event_store_view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


            event_store_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    // Do verification if the store exsit before launching the activity
                    // if does not exist launch message
                    if(StoreController.getStore(eventData.getStore_id()) != null) {

                        Intent intent = new Intent(EventActivity.this, StoreDetailActivity.class);
                        intent.putExtra("id", eventData.getStore_id());
                        startActivity(intent);

                    }else
                    {
                        Toast.makeText(getApplicationContext(),"Sorry !! this store does not exist ",Toast.LENGTH_LONG).show();
                    }

                    realm.commitTransaction();

                }
            });

        }else {


            event_store_layout.setVisibility(View.VISIBLE);
            event_store_view.setVisibility(View.VISIBLE);

            Drawable storeDrawable = new IconicsDrawable(context)
                    .icon(CommunityMaterial.Icon.cmd_map_marker)
                    .color(ResourcesCompat.getColor(context.getResources(),R.color.colorPrimary,null))
                    .sizeDp(18);

            event_store_view.setText(eventData.getAddress());
            event_store_view.setCompoundDrawables(storeDrawable,null,null,null);
            event_store_view.setCompoundDrawablePadding(20);

            event_store_layout.setVisibility(View.GONE);
            event_store_view.setVisibility(View.GONE);


        }



        initializeBtn();
        mParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                if (eventData != null) {

                    if (!EventController.isEventLiked(eventData.getId())) //CHECK IS THIS EVENT IS ALLREADY LIKED
                    {

                        UpComingEventsController.save(eventData.getId(),eventData.getName(),eventData.getDateB());

                        EventController.doLike(eventData.getId());

                        //ENABLE ALARM MANAGER TO START
                        EventController.addEventToNotify(eventData);
                        Toast.makeText(getApplicationContext(), R.string.event_participation_popup, Toast.LENGTH_SHORT).show();
                    }

                    initializeBtn();
                }

            }
        });

        mUnparticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                if (eventData != null) {
                    if (EventController.isEventLiked(eventData.getId())) //CHECK IS THIS EVENT IS ALLREADY LIKED
                    {

                        UpComingEventsController.remove(eventData.getId());


                        EventController.doDisLike(eventData.getId());
                        Toast.makeText(getApplicationContext(), R.string.event_participation_canceled, Toast.LENGTH_SHORT).show();
                    }
                    initializeBtn();
                }
            }
        });

        new decodeHtml().execute(eventData.getDescription());

        initMapping();

    }

    protected void getEventFromDB() {

        int eid = getIntent().getExtras().getInt("id");
        if(eid==0)
            eid = Integer.parseInt(getIntent().getExtras().getString("id"));

        eventData = EventController.findEventById(eid);
        mViewManager.showResult();

        if (eventData != null) {
            putDataIntoViews();
        } else {
            syncEvent(eid);
        }

    }


    private class decodeHtml extends AsyncTask<String,String,String>{

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
            description.setText(Html.fromHtml(text));
            //eventData.setDescription(text);
        }

        @Override
        protected String doInBackground(String... params) {

            return HtmlEscape.unescapeHtml(params[0]);
        }
    }



    public void syncEvent(final int evnt_id) {

       RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();

        mViewManager.loading();

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_GET_EVENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    mViewManager.showResult();

                    if (APP_DEBUG) {
                        Log.e("responseStoresString", response);
                    }

                    JSONObject jsonObject = new JSONObject(response);

                    //Log.e("response",response);

                    final EventParser mEventParser = new EventParser(jsonObject);
                    RealmList<Event> list = mEventParser.getEvents();

                    if(list.size()>0){
                        EventController.insertEvents(list);
                        eventData = list.get(0);
                        putDataIntoViews();

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
                params.put("event_id", String.valueOf(evnt_id));


                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(!MainActivity.isOpend()){
                startActivity(new Intent(this,MainActivity.class));
            }
            finish();

        }else if (item.getItemId() == R.id.send_location) {


            double lat = eventData.getLat();
            double lon = eventData.getLng();
            String link = null;

            try {
                //https://www.google.com/maps/search/?api=1&query=47.5951518,-122.3316393
                link = "https://maps.google.com/?q="+ URLEncoder.encode(eventData.getAddress(),"UTF-8")+"&ll="+ String.format("%f,%f", lat, lon);
            } catch (UnsupportedEncodingException e) {
                link = "https://maps.google.com/?ll=" + String.format("%f,%f", lat, lon);
                e.printStackTrace();
            }
            String shared_text = String.format(getString(R.string.shared_text),
                    getString(R.string.app_name),
                    eventData.getName(),
                    eventData.getAddress(),
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
