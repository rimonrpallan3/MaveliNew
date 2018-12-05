package com.mavelinetworks.mavelideals.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Store;
import com.mavelinetworks.mavelideals.load_manager.ViewManager;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.StoreParser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;
import com.mavelinetworks.mavelideals.utils.MapsUtils;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;

public class MapStoresListActivity extends AppCompatActivity implements OnMapReadyCallback {

    public ViewManager mViewManager;
    private GoogleMap mMap;
    private Document mDoc;
    private Button buttonRequest;

    // private GoogleMap map;
    private Timer timer;
    private Context context;


    private LatLng customerPosition;
    private LatLng myPosition;
    private int COUNT = 0;
    private Toolbar toolbar;
    private Marker marker = null;
    private Location MyLocation = null;
    //init request http
    private RequestQueue queue;
    private String TAG = ".MapMainActivity";
    private GPStracker mGPS;
    private TextView APP_TITLE_VIEW = null;
    private TextView APP_DESC_VIEW = null;

    @Override
    protected void onResume() {
        super.onResume();

    }


    private LinearLayout store_focus_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);

        context = this;
        setupToolbar();

        mViewManager = new ViewManager(this);
        mViewManager.setLoading(findViewById(R.id.loading));
        mViewManager.setNoLoading(findViewById(R.id.content_my_store));
        mViewManager.setError(findViewById(R.id.error));
        mViewManager.setEmpty(findViewById(R.id.empty));
        mViewManager.loading();

        store_focus_layout  = (LinearLayout) findViewById(R.id.store_focus_layout);
        initStoreFocusLayout();


        mGPS = new GPStracker(this);

        if (!mGPS.canGetLocation())
            mGPS.showSettingsAlert();


        queue = VolleySingleton.getInstance(this).getRequestQueue();


        //GET TREAD GEO POINT FROM INTENT


        //INITIALIZE MY LOCATION
        mGPS = new GPStracker(this);


       /* mMap = ((SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();*/

        attachMap();

        myPosition = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());

    }

    private void initStoreFocusLayout(){

        store_focus_layout.setVisibility(View.GONE);
        TypefaceHelper.typeface(store_focus_layout);
    }

    private void hideStoreFocusLayout(){

        if(store_focus_layout.isShown())
            com.mavelinetworks.mavelideals.animation
                    .Animation.hideWithZoomEffect(store_focus_layout);

    }

    private void showStoreFocusLayout(final Store store){


        TextView title = (TextView) store_focus_layout.findViewById(R.id.name);
        RatingBar rateBar = (RatingBar) store_focus_layout.findViewById(R.id.ratingBar2);
        TextView rateNbr = (TextView) store_focus_layout.findViewById(R.id.rate);

        title.setText(store.getName());
        rateBar.setRating((float) store.getVotes());

        float rated = (float) store.getVotes();
        DecimalFormat decim = new DecimalFormat("#.##");

        rateNbr.setText(decim.format(rated) +"  ("+store.getNbr_votes()+")");


        store_focus_layout.setVisibility(View.VISIBLE);
        store_clicked = true;
        com.mavelinetworks.mavelideals.animation.Animation.startCustomZoom(store_focus_layout);
        //give permission to hide this layout after 3 second


        store_focus_layout.findViewById(R.id.closeLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideStoreFocusLayout();

            }
        });

        store_focus_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideStoreFocusLayout();

                int id = store.getId();
                Intent intent = new Intent(MapStoresListActivity.this, StoreDetailActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });




    }

    private void attachMap(){

        try {

            SupportMapFragment mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mSupportMapFragment == null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                mSupportMapFragment = SupportMapFragment.newInstance();
                mSupportMapFragment.setRetainInstance(true);
                fragmentTransaction.replace(R.id.mapping, mSupportMapFragment).commit();
            }
            if (mSupportMapFragment != null) {
                mSupportMapFragment.getMapAsync(MapStoresListActivity.this);
            }

        }catch (Exception e){

        }

    }


    private boolean requestStarted = false;
    private void moveHandler(){

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(requestStarted){

                    LatLng po = new LatLng(
                            mMap.getCameraPosition().target.latitude,
                            mMap.getCameraPosition().target.longitude
                    );

                   // getStores(po,true);
                }
            }
        },5000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        initMapping();

        mMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {

                if(AppConfig.APP_DEBUG)
                    Log.e("onCameraMoveCanceled", String.valueOf(mMap.getCameraPosition().target.latitude));
            }
        });



        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

                if(AppConfig.APP_DEBUG)
                    Log.e("onCameraMove", String.valueOf(mMap.getCameraPosition().target.latitude));

                moveHandler();

            }
        });

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

                if(AppConfig.APP_DEBUG)
                    Log.e("onCameraMoveStarted", String.valueOf(i));

            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                if(AppConfig.APP_DEBUG)
                    Log.e("onCameraIdle", String.valueOf(mMap.getCameraPosition().target.latitude));
            }
        });


    }


    private void initMapping() {

        if (mMap != null) {

            mMap.getUiSettings().setZoomControlsEnabled(true);

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);

            mGPS = new GPStracker(getBaseContext());
            final LatLng po = new LatLng(mGPS.getLatitude(),mGPS.getLatitude());

            getStores(po,false);

            //reload
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    getStores(po,true);
                }
            },3000);

        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private boolean store_clicked = false;
    private void getStores(final LatLng position, final boolean refresh) {

        requestStarted = true;

        if(refresh==false){
            mViewManager.loading();
        }

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_GET_STORES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    requestStarted = false;
                    if(AppConfig.APP_DEBUG)
                        Log.e("____response",response);

                    JSONObject jsonObject = new JSONObject(response);
                    final StoreParser mStoreParser = new StoreParser(jsonObject);

                    COUNT = mStoreParser.getIntArg(Tags.COUNT);
                    int success = Integer.parseInt(mStoreParser.getStringAttr("success"));

                    if (success == 1) {

                        if (COUNT > 0) {

                            if(refresh==false){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 14));
                                mViewManager.showResult();
                            }

                            final List<Store> list = mStoreParser.getStore();

                            Bitmap b = ((BitmapDrawable) Utils.changeDrawableIconMap(
                                    MapStoresListActivity.this, R.drawable.ic_marker)).getBitmap();
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(b);

                            if(refresh==true){
                                mMap.clear();
                            }

                            for (int i = 0; i < list.size(); i++) {

                                String imageUrl = null;

                                if(list.get(i).getListImages()!=null && list.get(i).getListImages().size()>0){
                                    imageUrl = list.get(i).getListImages().get(0).getUrl100_100();
                                }

                                if(imageUrl!=null){

                                    final int finalI = i;
                                    Picasso.with(context).load(imageUrl).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {


                                            String promo = null;
                                            if(!list.get(finalI).getLastOffer().equals("")){
                                                promo= list.get(finalI).getLastOffer();
                                            }

                                            Marker marker=null;
                                            marker = mMap.addMarker(

                                                    MapsUtils.generateMarker( MapStoresListActivity.this,
                                                            String.valueOf(list.get(finalI).getId())     ,
                                                            new LatLng(list.get(finalI).getLatitude(), list.get(finalI).getLongitude()
                                                            ),
                                                            bitmap,
                                                            promo
                                                    ).draggable(false)

                                            );

                                            marker.setTag(finalI);
                                            MapsUtils.addMarker(  String.valueOf(list.get(finalI).getId())  ,marker);

                                        }
                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {
                                        }
                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }
                                    });


                                }else{

                                    String promo = null;
                                    if(!list.get(i).getLastOffer().equals("")){
                                        promo = list.get(i).getLastOffer();
                                    }

                                    Marker marker=null;
                                    marker = mMap.addMarker(

                                            MapsUtils.generateMarker( MapStoresListActivity.this,
                                                    String.valueOf(list.get(i).getId())     ,
                                                    new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude()
                                                    ),
                                                    null,
                                                    promo
                                            ).draggable(false)

                                    );

                                    marker.setTag(i);
                                    MapsUtils.addMarker(  String.valueOf(list.get(i).getId())  ,marker);

                                }



                            }

                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {

                                    int position = (int)(marker.getTag());

                                    if(AppConfig.APP_DEBUG)
                                        Toast.makeText(MapStoresListActivity.this,list.get(position).getName(),
                                                Toast.LENGTH_LONG).show();

//                                    int id = list.get(position).getId();
//
//                                    Intent intent = new Intent(MapStoresListActivity.this, StoreDetailActivity.class);
//                                    intent.putExtra("id", id);
//                                    startActivity(intent);

                                    showStoreFocusLayout(list.get(position));

                                    return false;
                                }
                            });

                        } else {
                            mViewManager.empty();
                        }
                    }


                } catch (JSONException e) {
                    //send a rapport to support
                    e.printStackTrace();

                    requestStarted = false;
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (APP_DEBUG) {
                    Log.e("ERROR", error.toString());
                }

                //mViewManager.error();
                requestStarted = false;
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                if (mGPS.canGetLocation()) {
                    params.put("latitude", String.valueOf(position.latitude));
                    params.put("longitude", String.valueOf(position.longitude));
                }


               // params.put("token", Utils.getToken(getBaseContext()));
               // params.put("mac_adr", ServiceHandler.getMacAddress(getBaseContext()));

                params.put("limit", "10");
                params.put("page", "1");
//                params.put("type", "1");
//                params.put("status", "1");


                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);


    }



    public void setupToolbar() {

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setSubtitle("E-shop");
        getSupportActionBar().setTitle("---");
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
        Utils.setFont(this, APP_DESC_VIEW, "SourceSansPro-Black.otf");
        Utils.setFont(this, APP_TITLE_VIEW, "SourceSansPro-Black.otf");

        APP_TITLE_VIEW.setText(getString(R.string.MapStores));

        APP_DESC_VIEW.setVisibility(View.GONE);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(R.anim.righttoleft_enter, R.anim.righttoleft_exit);

        }else if(id == R.id.action_refresh){

            if(mMap!=null){
                LatLng po = new LatLng(
                        mMap.getCameraPosition().target.latitude,
                        mMap.getCameraPosition().target.longitude
                );
                getStores(po,true);
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {



        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void OpenStoreActivity(Store store) {

        Intent intent = new Intent(this, StoreDetailActivity.class);


        intent.putExtra(Store.Tags.ID, store.getId());
        intent.putExtra(Store.Tags.NAME, store.getName());
        intent.putExtra(Store.Tags.ADDRESS, store.getAddress());
        intent.putExtra(Store.Tags.DISTANCE, store.getDistance());
        intent.putExtra(Store.Tags.LAT, store.getLatitude());
        intent.putExtra(Store.Tags.LONG, store.getLongitude());
        intent.putExtra(Store.Tags.STATUS, store.getStatus());
        intent.putExtra(Store.Tags.TYPE, store.getType());
        intent.putExtra(Store.Tags.PHONE, store.getPhone());
        intent.putExtra(Store.Tags.DETAIL, store.getDetail());
        intent.putExtra(Store.Tags.VOTES, store.getVotes());


        try {

            if (APP_DEBUG) {
                Log.e("ListImageToPass", store.getVotes() + "");
            }

            intent.putExtra(Store.Tags.LISTIMAGES, store.getImageJson().toString());


        } catch (Exception e) {
            e.printStackTrace();
        }


        startActivity(intent);
    }


}
