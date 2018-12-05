package com.mavelinetworks.mavelideals.dtmessenger;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.appconfig.Messages;
import com.mavelinetworks.mavelideals.classes.Guest;
import com.mavelinetworks.mavelideals.classes.Store;
import com.mavelinetworks.mavelideals.classes.UpComingEvent;
import com.mavelinetworks.mavelideals.controllers.events.UpComingEventsController;
import com.mavelinetworks.mavelideals.controllers.sessions.GuestController;
import com.mavelinetworks.mavelideals.fragments.SettingsFragment;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.StoreParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;

/**
 * Created by Droideve on 7/24/2016.
 */

public class DCMBroadcastReceiver extends BroadcastReceiver {

    protected List<NetworkStateReceiverListener> listeners;
    protected Boolean connected;
    private String Message;


    public DCMBroadcastReceiver() {
        listeners = new ArrayList<NetworkStateReceiverListener>();
        connected = null;
    }


    @Override
    public void onReceive(final Context context, final Intent intent) {

        if(intent == null || intent.getExtras() == null)
            return;

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                runBgApp(context,intent);
            }
        },5000);

    }

    private void runBgApp(Context context, Intent intent){

        if(AppConfig.APP_DEBUG)
            Log.e("DCMBroadcastReceiver","changed-->"+ServiceHandler.isNetworkAvailable(context));

        if(ServiceHandler.isNetworkAvailable(context)) {

            //refresh position
            if(GuestController.isStored()){
                refreshPositionGuest(GuestController.getGuest(),context);
            }

            //get nearby stores
            if (SettingsFragment.isNotifyNearTrue(context)) {
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
                boolean Notif_NearBy = sh.getBoolean("notif_nearby_stores", true);
                if (Notif_NearBy) {
                   // getNearStore(context);
                }
            }

            // the thread
            boolean notif_upcomingevent = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("notif_upcomingevent", false);

            if(notif_upcomingevent) {
                List<UpComingEvent> list = UpComingEventsController.getUpComingEventsNotNotified();
                if(list.size()>1){
                    NotificationsManager.pushUpcomingEvent(context,context.getString(R.string.app_name),context.getString(R.string.upComingEventsMessage));
                }else if(list.size()==1){
                    NotificationsManager.pushUpcomingEvent(context,context.getString(R.string.upComingEventMessage),list.get(0).getEvent_name());
                }
                UpComingEventsController.notified();
                //notified
            }

            connected = true;

        } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {

            connected = false;
        }
    }

    private void notifyStateToAll() {
        for(NetworkStateReceiverListener listener : listeners)
            notifyState(listener);
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if(connected == null || listener == null)
            return;

        if(connected == true) {
            listener.networkAvailable();
        }else {
            listener.networkUnavailable();
        }
    }

    public void addListener(NetworkStateReceiverListener l) {
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(NetworkStateReceiverListener l) {
        listeners.remove(l);
    }

    public interface NetworkStateReceiverListener {
        public void networkAvailable();
        public void networkUnavailable();
    }

    private int COUNT=0;
    private RequestQueue queue;

    private void refreshPositionGuest(final Guest mGuest, final Context context){


        GPStracker gps = new GPStracker(context);
        if(mGuest!=null && gps.canGetLocation()) {

            queue = VolleySingleton.getInstance(context).getRequestQueue();

            final int user_id = mGuest.getId();
            final double lat =  gps.getLatitude();
            final double lng =  gps.getLongitude();

            SimpleRequest request = new SimpleRequest(Request.Method.POST,
                    Constances.API.API_REFRESH_POSITION, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {

                    Log.e("response", response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR", error.toString());

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("guest_id", String.valueOf(user_id));
                    params.put("lat", String.valueOf(lat));
                    params.put("lng", String.valueOf(lng));

                    if(APP_DEBUG)
                        Log.e("onRefreshSync",params.toString());

                    return params;
                }

            };


            request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(request);

        }
    }

    public void getNearStore(final Context context) {

        final GPStracker mGPS = new GPStracker(context);
        final RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_GET_STORES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                final NotificationCompat.Builder  builder;
                builder = new NotificationCompat.Builder(context);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("response", jsonObject.toString());

                    if(APP_DEBUG) {Log.e("response", response);}

                    final StoreParser mStoreParser = new StoreParser(jsonObject);

                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
                            int raduis = Integer.parseInt(sh.getString("key_notif_radius", context.getString(R.string.radius_near_by)));

                            List<Store> list = mStoreParser.getStore();
                            int count = 0;
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getDistance() <= raduis) {
                                    count++;
                                }
                            }

                            //
                            if (count > 0) {

                                if (count > 10) {
                                    Message = Messages.ALLStandardMessage.NOTIFICAION_MORE_STORES;
                                } else {
                                    Message =  count +Messages.ALLStandardMessage.NOTIFICAION_STORE_NEAR_YOU;
                                }
                            }

                            if (Message != null) {
                                builder.setContentText(Message);
                                // builder.setLargeIcon(R.drawable)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    builder.setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText(Message)
                                            .setSummaryText(Messages.ALLStandardMessage.NOTIFICAION_SHOW_MORE))
                                            .setAutoCancel(true);
                                }

                                builder.setSmallIcon(R.mipmap.ic_launcher);
                                builder.setContentTitle(context.getString(R.string.notification_title));

                                Intent intent1 = new Intent(context, MainActivity.class);
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                                stackBuilder.addParentStack(MainActivity.class);
                                stackBuilder.addNextIntent(intent1);
                                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.setContentIntent(pendingIntent);

                                NotificationManager NM;
                                NM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                NM.notify(0, builder.build());

                            }


                        }
                    }, 800);


                } catch (JSONException e) {
                    //send a rapport to support
                    e.printStackTrace();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(APP_DEBUG)
                    Log.e("ERROR", error.toString());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                if (mGPS.canGetLocation()) {
                    params.put("latitude", mGPS.getLatitude() + "");
                    params.put("longitude", mGPS.getLongitude() + "");
                }
                return params;
            }


        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);


    }


}
