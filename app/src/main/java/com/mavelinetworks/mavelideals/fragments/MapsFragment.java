package com.mavelinetworks.mavelideals.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.GPS.GoogleDirection;
import com.mavelinetworks.mavelideals.GPS.Position;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class MapsFragment extends Fragment implements OnMapReadyCallback {


    private MapsFragment mContext;
    private Double targetLatitude = 0.0;
    private Double targetLongitude = 0.0;
    private String targetName = "";
    private boolean selected = false;
    private GPStracker trackMe;
    private LatLng customerPosition;
    private LatLng customerTargetPosition;
    private GoogleMap googleMap = null;
    private GoogleDirection googleDirection;
    private Document mDoc;
    private SupportMapFragment mSupportMapFragment;
    private ArrayList<Marker> listMarker;
    private TextView mapTitle;

    private void getDataFromIntent(Bundle bundle) {

        try {
            selected = bundle.getBoolean(Tags.SELECTED, false);
            targetLatitude = bundle.getDouble(Tags.TARGET_LAT, 0.0);
            targetLongitude = bundle.getDouble(Tags.TARGET_LONG, 0.0);
        } catch (Exception e) {

        }
    }

    private void getDataFromIntent() {

        try {
            selected = getArguments().getBoolean(Tags.SELECTED, false);
            targetLatitude = getArguments().getDouble(Tags.TARGET_LAT, 0.0);
            targetLongitude = getArguments().getDouble(Tags.TARGET_LONG, 0.0);
        } catch (Exception e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mContext = this;
        trackMe = new GPStracker(getActivity());

        listMarker = new ArrayList<Marker>();

        getDataFromIntent();
        attachMap();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //attach mapfragment

    @Override
    public void onMapReady(GoogleMap googleMap) {

        customerPosition = new LatLng(trackMe.getLatitude(), trackMe.getLongitude());

        this.googleMap = googleMap;

        Marker perth = googleMap.addMarker(new MarkerOptions()
                .position(customerPosition)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
                .draggable(true));

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerPosition, 13));


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.googleMap.setMyLocationEnabled(true);

    }

    private void moveToPosition(){

        LatLng targetPosition = new LatLng(targetLatitude,targetLongitude);

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetPosition, 13));
        this.googleMap.addMarker(new MarkerOptions()
                .title("Store")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
                .position(targetPosition));


    }

    private void attachMap(){

        try {
            SupportMapFragment mSupportMapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);

            if (mSupportMapFragment == null) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                mSupportMapFragment = SupportMapFragment.newInstance();
                mSupportMapFragment.setRetainInstance(true);
                fragmentTransaction.replace(R.id.maps, mSupportMapFragment).commit();
            }
            if (mSupportMapFragment != null) {
                mSupportMapFragment.getMapAsync(MapsFragment.this);
            }
        }catch (Exception e){

        }

    }

    private void removeMarkers() {
        for (Marker marker: listMarker) {
            marker.remove();
        }
        listMarker.clear();
    }

    private void getDistance(){

        customerTargetPosition = new LatLng(targetLatitude ,targetLongitude);

        Position p = new Position();
        double dis = p.distance(trackMe.getLatitude(), trackMe.getLongitude(), targetLatitude, targetLongitude);

        CameraPosition camPos = new CameraPosition.Builder()
                .target(getCenterCoordinate())
                .zoom(17)
                .build();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        //add the marker locations that you'd like to display on the map
        boundsBuilder.include(customerPosition);
        boundsBuilder.include(customerTargetPosition);

        final LatLngBounds bounds = boundsBuilder.build();

        CameraUpdate camUp = CameraUpdateFactory.newLatLngBounds(bounds, MainActivity.width, MainActivity.height, 100);

        googleMap.animateCamera(camUp);


        googleDirection = new GoogleDirection(getActivity());


        if(!ServiceHandler.isNetworkAvailable(getActivity()))
           Toast.makeText(getActivity(),"Check your device network",Toast.LENGTH_LONG).show();
        else {

            googleDirection.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {

                @Override
                public void onResponse(String status, Document doc, GoogleDirection gd) {
                    mDoc = doc;


                    int colorId = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);

                    if (googleMap != null) {
                        googleMap.addPolyline(gd.getPolyline(doc, 10, colorId));


                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }

                        googleMap.setMyLocationEnabled(true);
                    } else {
                        attachMap();
                    }
                }
            });

            googleDirection.setLogging(true);
            googleDirection.request(customerPosition, customerTargetPosition, GoogleDirection.MODE_WALKING);
        }

    }

    public LatLng getCenterCoordinate(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(customerPosition);
        builder.include(customerTargetPosition);
        LatLngBounds bounds = builder.build();
        return bounds.getCenter();
    }

    private int calculateZoomLevel(int screenWidth) {
        double equatorLength = 40075004; // in meters
        double widthInPixels = screenWidth;
        double metersPerPixel = equatorLength / 256;
        int zoomLevel = 1;
        while ((metersPerPixel * widthInPixels) > 2000) {
            metersPerPixel /= 2;
            ++zoomLevel;
        }
        //Log.i("ADNAN", "zoom level = "+zoomLevel);
        return zoomLevel;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {

//
//            Log.e("mapFragment","destroyed");
//            if(this.googleMap!=null){
//               googleMap.clear();
//            }
        }catch (Exception e){

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public static class Tags {
        public static String SELECTED = "SELECTED";
        public static String TARGET_LAT = "MYTARGET_LAT";
        public static String TARGET_LONG = "MYTARGET_LONG";
        public static String IS_DIRECTION = "IS_DIRECTION";
        public static String TITLE = "TITLE";
    }
}
