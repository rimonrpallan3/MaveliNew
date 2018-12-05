package com.mavelinetworks.mavelideals.controllers.users;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mavelinetworks.mavelideals.AppController;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.LoginActivity;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.User;
import com.mavelinetworks.mavelideals.controllers.sessions.SessionsController;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.UserParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Droideve on 7/13/2017.
 */

public class UserController {

    public static boolean insertUsers(final RealmList<User> list){

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(list);
            }
        });
        return  true;

    }

    public static void checkUserConnection(final FragmentActivity context){

        (new android.os.Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserWithThread(context);
            }
        },10000);

    }

    private static int nbrOfCheck = 0;
    public static void checkUserWithThread(final FragmentActivity context){

        if(nbrOfCheck>0)
            return;

        if(SessionsController.isLogged()){


            User user = SessionsController.getSession().getUser();
            final String email = user.getEmail();
            final String userid = String.valueOf(user.getId());
            final String username = user.getUsername();
            final String senderid = user.getSenderid();

            SimpleRequest request = new SimpleRequest(Request.Method.POST,
                    Constances.API.API_USER_CHECK_CONNECTION, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try{

                        if(AppConfig.APP_DEBUG)
                            Log.e("___checkUser",response);


                        JSONObject jsonObject = new JSONObject(response);
                        final UserParser mUserParser = new UserParser(jsonObject);

                        if(mUserParser.getSuccess()==0 || mUserParser.getSuccess()==-1){

                            userLogoutAlert(context);
                            nbrOfCheck=0;

                        }else{

                            nbrOfCheck++;

                        }

                    }catch (JSONException e){
                        //send a rapport to support
                        e.printStackTrace();

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("email",email);
                    params.put("userid",userid);
                    params.put("username",username);
                    params.put("senderid",senderid);

                    return params;

                }

            };

            request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(AppController.getInstance())
                    .getRequestQueue().add(request);


        }

    }


    public static void userLogoutAlert(final FragmentActivity activity){

        new android.app.AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.Logout)+"!")
                .setMessage(activity.getString(R.string.logout_alert))
                .setPositiveButton(activity.getString(R.string.Login), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        SessionsController.logOut();
                        ActivityCompat.finishAffinity(activity);
                        activity.startActivity(new Intent(activity, LoginActivity.class));

                    }
                })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        SessionsController.logOut();
                        ActivityCompat.finishAffinity(activity);
                        activity.startActivity(new Intent(activity, MainActivity.class));

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }
}
