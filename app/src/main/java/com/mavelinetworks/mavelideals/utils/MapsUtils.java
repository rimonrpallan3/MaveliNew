package com.mavelinetworks.mavelideals.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.mavelinetworks.mavelideals.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Droideve on 1/24/2017.
 */

public class MapsUtils {


    private static Map<String,Marker> markers =new HashMap<>();
    public static boolean markerIsExist(String id){
        if(markers.containsKey(id)){
            return true;
        }
        return false;
    }

    public static void markerClear(){
        markers =new HashMap<>();
    }

    public static Marker getMarker(String id){
        if(markers.containsKey(id)){
            return markers.get(id);
        }
        return null;
    }
    public static void addMarker(String id, Marker marker){
        if(markers.containsKey(id)){
            return;
        }
        markers.put(id,marker);
    }

    private static Map<String,MarkerOptions> markersOption =new HashMap<>();


    public static MarkerOptions generateMarker(FragmentActivity context, String id, LatLng pos,Bitmap bitmap,String promo){

        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setColor(ResourcesCompat.getColor(context.getResources(),R.color.colorPrimary,null));

        MarkerOptions markerOptions;
        if(markersOption.containsKey(id)){

            markerOptions = markersOption.get(id);

        }else{

            View multiProfile = context.getLayoutInflater().inflate(R.layout.view_image_marker_map, null);

            iconGenerator.setContentView(multiProfile);

            ImageView image = (ImageView) multiProfile.findViewById(R.id.image);
            TextView promoView = (TextView) multiProfile.findViewById(R.id.promo);

            if(bitmap!=null)
                image.setImageBitmap(bitmap);

            if(promo==null)
                promoView.setVisibility(View.GONE);
            else {
                promoView.setText(context.getString(R.string.promo));
                promoView.setVisibility(View.VISIBLE);
            }


            markerOptions = new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(""))).
                    position(pos).
                    anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV());

            markersOption.put(id,markerOptions);

        }

        return markerOptions;
    }

    public static MarkerOptions generateMarker(FragmentActivity context, LatLng pos, String imageUrl){
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setColor(Color.WHITE);

        View multiProfile = context.getLayoutInflater().inflate(R.layout.view_image_marker_map, null);
        iconGenerator.setContentView(multiProfile);

        Picasso.with(context).load(imageUrl).placeholder(R.drawable.profile_placeholder).fit().centerCrop()
                .into((ImageView) multiProfile.findViewById(R.id.image));

        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(""))).
                position(pos).
                anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV());

        return markerOptions;
    }


    public static String getAddress(Context context, LatLng pos){
        String address="";
        try {

            Geocoder geo = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(pos.latitude, pos.longitude, 1);
            if (addresses.isEmpty()) {
                address = "";
            }
            else {
                if (addresses.size() > 0) {
                    address = addresses.get(0).getLocality() +", "+addresses.get(0).getAdminArea() +", " + addresses.get(0).getCountryName();

                    //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }

        return  address;
    }


    public static void animateMarker(GoogleMap gmap, final Marker marker,
                                     final LatLng toPosition, final boolean hideMarker) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = gmap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });

    }


//    public static void animateMarker(LatLng pos, final Marker marker) {
//
//        final Location destination = new Location(LocationManager.GPS_PROVIDER);
//        destination.setLatitude(pos.latitude);
//        destination.setLongitude(pos.longitude);
//        if (marker != null) {
//            final LatLng startPosition = marker.getPosition();
//            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());
//
//            final float startRotation = marker.getRotation();
//
//            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
//            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
//            valueAnimator.setDuration(1000); // duration 1 second
//            valueAnimator.setInterpolator(new LinearInterpolator());
//            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    try {
//                        float v = animation.getAnimatedFraction();
//                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
//                        marker.setPosition(newPosition);
//                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
//                    } catch (Exception ex) {
//                        // I don't care atm..
//                    }
//                }
//            });
//
//            valueAnimator.start();
//        }
//    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }
}
