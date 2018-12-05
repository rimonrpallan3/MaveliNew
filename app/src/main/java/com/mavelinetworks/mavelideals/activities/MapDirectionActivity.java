package com.mavelinetworks.mavelideals.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.GPS.GoogleDirection;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;

import java.util.Timer;
import java.util.TimerTask;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;

public class MapDirectionActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button buttonRequest;
    Timer timer;
    LatLng customerPosition;
    LatLng myPosition;
    Context context;

    // private GoogleMap map;
    private GoogleMap mMap;
    private GoogleDirection gd;
    private Document mDoc;
    private Location MyLocation = null;
    private Double lng;
    private Double lat;
    private Double distance;

    private String TAG = ".MapDirection";
    private Toolbar toolbar;
    private TextView APP_TITLE_VIEW = null;
    private TextView APP_DESC_VIEW = null;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        initToolbar();

        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        GPStracker gps = new GPStracker(this);
        customerPosition = new LatLng(gps.getLatitude(), gps.getLongitude());
        mMap = googleMap;
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            if (APP_DEBUG) {
                Log.e("GooglePlayServices", "Available");
            }
            System.gc();
            mMap.clear();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerPosition, 16));

        initMapping();

    }

    private void initMapping() {

        //GET TREAD GEO POINT FROM INTENT
        Intent intent = getIntent();
        lat = Double.parseDouble(intent.getStringExtra("latitude"));
        lng = Double.parseDouble(intent.getStringExtra("longitude"));
        distance = Double.parseDouble(intent.getStringExtra("distance"));
        String traderName = intent.getStringExtra("name");
        String traderAdresse = intent.getStringExtra("description");

        APP_TITLE_VIEW.setText(traderName);

        customerPosition = new LatLng(lat, lng);

        //INITIALIZE MY LOCATION
        GPStracker trackMe = new GPStracker(this);
        myPosition = new LatLng(trackMe.getLatitude(), trackMe.getLongitude());

        if (mMap != null) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerPosition, 17));

            mMap.getUiSettings().setZoomControlsEnabled(true);


            Bitmap b = ((BitmapDrawable) Utils.changeDrawableIconMap(
                    MapDirectionActivity.this, R.drawable.ic_marker)).getBitmap();
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(b);

            //trader location

            mMap.addMarker(new MarkerOptions().position(customerPosition)
                    .title(traderName)
                    .anchor(0.0f, 1.0f).icon(icon)
                    .snippet(traderAdresse)).showInfoWindow();

            mMap.addMarker(new MarkerOptions().position(myPosition)
                    .title(getApplicationContext().getString(R.string.my_position))
                    .anchor(0.0f, 1.0f)
                    .draggable(true))
                    .showInfoWindow();

            gd = new GoogleDirection(this);


            if (ServiceHandler.isNetworkAvailable(this)) {
                try {
                    gd = new GoogleDirection(this);
                    //My Location
                    if (distance <= getResources().getInteger(R.integer.radius_map)) {
                        gd.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {

                            @Override
                            public void onResponse(String status, Document doc, GoogleDirection gd) {
                                mDoc = doc;
                                mMap.addPolyline(gd.getPolyline(doc, 7,
                                        ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null)));
                                //mMap.setMyLocationEnabled(true);
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.store_to_far_map, Toast.LENGTH_SHORT).show();
                    }

                    gd.setLogging(true);
                    gd.request(myPosition, customerPosition, GoogleDirection.MODE_DRIVING);

                } catch (Exception e) {
                    if (APP_DEBUG)
                        e.printStackTrace();
                }

            }


            try {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        /*
                        if (ServiceHandler.isNetworkAvailable(getApplicationContext())) {

                            gd.setCameraUpdateSpeed(10);
                        }*/
                    }
                };
                timer = new Timer();
                timer.scheduleAtFixedRate(task, 0, 6000);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    public void initToolbar() {

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
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

        APP_TITLE_VIEW.setText("Map");

        APP_DESC_VIEW.setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(R.anim.righttoleft_enter, R.anim.righttoleft_exit);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    @Override
    protected void onDestroy() {
            super.onDestroy();
    }
}
