package com.mavelinetworks.mavelideals.activities.login.presenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.LoginActivity;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.activities.firstotppage.model.CountryDetails;
import com.mavelinetworks.mavelideals.activities.login.view.ILoginView;
import com.mavelinetworks.mavelideals.activities.signuppage.model.IUserDetails;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.AppContext;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Errors;
import com.mavelinetworks.mavelideals.classes.User;
import com.mavelinetworks.mavelideals.classes.UserDetails;
import com.mavelinetworks.mavelideals.classes.UserRow;
import com.mavelinetworks.mavelideals.controllers.sessions.GuestController;
import com.mavelinetworks.mavelideals.controllers.sessions.SessionsController;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.UserParser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;
import com.mavelinetworks.mavelideals.utils.MessageDialog;
import com.mavelinetworks.mavelideals.utils.Translator;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.mavelinetworks.mavelideals.views.CustomDialog;
import com.mavelinetworks.mavelideals.webservices.ApiClient;
import com.mavelinetworks.mavelideals.webservices.WebServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;
import static com.thefinestartist.utils.content.ContextUtil.startActivity;

/**
 * Created by User on 19-Jul-18.
 */

public class LoginPresenter implements ILoginPresenter{

    Context context;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    private static final String TAG = "LoginPresenter";
    String name;
    String passwd;
    IUserDetails user;
    UserDetails userDetails;
    ILoginView iLoginView;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    Boolean state = false;
    String userId = "";
    String userName = "";
    String userEmailAdress = "";
    String userImageUrl = "";
    String userMob = "";
    Activity activity;
    List<CountryDetails> countryDetailsList;
    private ProgressDialog mPdialog;
    private GPStracker gps;
    private CustomDialog mDialogError;
    private RequestQueue queue;


    public LoginPresenter(Activity activity, SharedPreferences sharedPrefs,
                          SharedPreferences.Editor editor,
                          ILoginView iLoginView,
                          GoogleSignInClient mGoogleSignInClient,
                          FirebaseAuth mAuth) {
        this.activity = activity;
        this.sharedPrefs = sharedPrefs;
        this.editor = editor;
        this.iLoginView = iLoginView;
        this.mGoogleSignInClient = mGoogleSignInClient;
        this.mAuth = mAuth;
        countryDetailsList = getCountryDetailsList();
        iLoginView.getCountryDetailList(countryDetailsList);
        gps = new GPStracker(activity);
        queue = VolleySingleton.getInstance(activity).getRequestQueue();
    }



    @Override
    public void clear() {
        iLoginView.onClearText();
    }

    @Override
    public void doLogin(String countryCode, String name, String passwd) {
        this.name = countryCode+name;
        this.passwd = passwd;
        System.out.println("-------doLogin  email : " + countryCode+name +
                " Password : " + passwd);
        initUser();
        Boolean isLoginSuccess = true;
        final int code = user.checkUserValidity(name,passwd);
        if (code!=0) isLoginSuccess = false;
        final Boolean result = isLoginSuccess;
        iLoginView.onLoginResult(result, code);
        validateLoginDataBaseApi();
    }


    public void validateLoginDataBaseApi(){
       /* System.out.println("-------validateLoginDataBaseApi ");
        Retrofit retrofit = new ApiClient().getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);
        System.out.println("Translator.DefaultLang : "+ Translator.DefaultLang);
        System.out.println("Debug : "+String.valueOf(AppContext.DEBUG));
        System.out.println("Api-key-android : "+ AppConfig.ANDROID_API_KEY);
        Call<UserDetails> call = webServices.loginUser2("918891778244",
                String.valueOf(GuestController.getGuest().getId()),
                ServiceHandler.getMacAddr(),
                "0.0",
                "0.0",
                "pass");
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                userDetails  = (UserDetails) response.body();
                UserRow userRow = userDetails.getUserRow();
                Errors errors = userDetails.getErrors();
                if(userRow!=null){
                     System.out.println("-------validateLoginDataBaseApi  email : " + name +
                        " Password : " + passwd +
                        " LName : " + userRow.getName()+
                        " phno : " + userRow.getMobile() +
                        " email : " + userRow.getEmail() +
                        "pswd " + userRow.getPassword()+
                        "Auth"+ userRow.getTypeAuth());
                }else if(errors!=null){
                    System.out.println("-------validateLoginDataBaseApi  email : " + name +
                            " connect : " + errors.getConnect());
                }

                Gson gson = new Gson();
                String jsonString = gson.toJson(userDetails);

                System.out.println(" ----------- getFilters OfferListMap "+jsonString);
                if(jsonString!=null) {
                    System.out.println("-----------getFilters OfferList"+jsonString);
                }

                final int code =user.validateRegisterResponseError(userDetails.getSuccess());
                System.out.println("--------- validateLoginDataBaseApi code: "+code);
                Boolean isLoginSuccess =true;
                if (code == 0) {
                    isLoginSuccess = false;
                    System.out.println("--------- validateLoginDataBaseApi isError: "+userDetails.getSuccess());
                    Toast.makeText((Context) iLoginView, errors.getConnect(), Toast.LENGTH_SHORT).show();
                    System.out.println("-----validateLoginDataBaseApi  data unSuccess ");
                } else {
                    System.out.println("----- validateLoginDataBaseApi isError: "+userDetails.getSuccess());
                    Toast.makeText((Context) iLoginView, "Login Successful", Toast.LENGTH_SHORT).show();
                    addUserGsonInSharedPrefrences(userDetails);
                    System.out.println("----- validateLoginDataBaseApi data Successful ");
                }
                Boolean result = isLoginSuccess;
                System.out.println("----- sendRegisteredDataAndValidateResponse second Data Please see, code = " + code + ", result: " + result);
                iLoginView.onLoginResponse(result, code);
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Boolean isLoginSuccess = false;
                Boolean result = isLoginSuccess;
                int code = -77;
                iLoginView.onLoginResult(result, code);
                t.printStackTrace();
                //Toast.makeText((Context) iRegisterView, "ErrorMessage"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/
        /**************************************************************************/
        int guest_id = 0;
        mPdialog = new ProgressDialog(activity);
        mPdialog.setMessage("Loading ...");
        mPdialog.show();
        final double lat= gps.getLatitude();
        final double lng= gps.getLongitude();

        if(GuestController.isStored())
            guest_id = GuestController.getGuest().getId();
        Boolean isLoginSuccess =true;

        final int finalGuest_id = guest_id;
        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_LOGIN, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    if(APP_DEBUG) { Log.e("response", response); }
                    System.out.println("response userLogin : "+response);


                    JSONObject js = new JSONObject(response);
                    UserParser mUserParser = new UserParser(js);

                    int success = Integer.parseInt(mUserParser.getStringAttr(Tags.SUCCESS));

                    if(success==1){

                        List<User> list = mUserParser.getUser();



                        if(list.size()>0){
                            User user = list.get(0);
                            UserDetails userDetails = new UserDetails();
                            userDetails.setAuth(user.getAuth());
                            userDetails.setCity(user.getCity());
                            userDetails.setCountry(user.getCountry());
                            userDetails.setDistance(user.getDistance());
                            userDetails.setEmail(user.getEmail());
                            userDetails.setId(user.getId());
                            userDetails.setLatitude(user.getLatitude());
                            userDetails.setLongitude(user.getLongitude());
                            userDetails.setPassword(user.getPassword());
                            userDetails.setName(user.getName());
                            userDetails.setTel(user.getPhone());
                            userDetails.setUsername(user.getUsername());
                            iLoginView.realMResponse(userDetails);

                            addUserGsonInSharedPrefrences(userDetails);
                            if(SessionsController.isLogged()) {
                                // startActivity(new Intent(activity, MainActivity.class ));


                            }

                        }


                    }else{


                        Map<String,String> errors = mUserParser.getErrors();

                        MessageDialog.newDialog(activity).onCancelClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MessageDialog.getInstance().hide();
                                mPdialog.dismiss();
                            }
                        }).onOkClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                MessageDialog.getInstance().hide();
                                mPdialog.dismiss();
                            }
                        }).setContent(Translator.print(activity.getString(R.string.authentification_error_msg),"Message error")).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    MessageDialog.newDialog(activity).onCancelClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MessageDialog.getInstance().hide();
                            mPdialog.dismiss();
                        }
                    }).onOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            MessageDialog.getInstance().hide();
                            mPdialog.dismiss();
                        }
                    }).setContent(Translator.print(activity.getString(R.string.authentification_error_msg),"Message error (Parser)")).show();


                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(APP_DEBUG) {   Log.e("ERROR", error.toString());}

               // signup.setEnabled(true);

                mPdialog.dismiss();

                Map<String,String> errors = new HashMap<String,String>();

                errors.put("NetworkException:", activity.getString(R.string.check_network));
                mDialogError = showErrors(errors);
                mDialogError.setTitle(activity.getString(R.string.network_error));

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("token",Utils.getToken(activity.getBaseContext()));
                params.put("mac_adr",ServiceHandler.getMacAddr());
                params.put("password",passwd);
                params.put("login",name);

                params.put("lat", String.valueOf(lat));
                params.put("lng", String.valueOf(lng));
                params.put("guest_id", String.valueOf(finalGuest_id));

                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);



    }

    public CustomDialog showErrors(Map<String,String> errors){
        final CustomDialog dialog = new CustomDialog(activity);

        dialog.setContentView(R.layout.fragment_dialog_costum);
        dialog.setCancelable(false);


        String text = "";
        for ( String key : errors.keySet() ) {
            if(!text.equals(""))
                text = text+"<br>";


            text = text+"#"+errors.get(key);
        }

        Button ok = (Button) dialog.findViewById(R.id.ok);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        TextView msgbox = (TextView) dialog.findViewById(R.id.msgbox);

        if(!text.equals("") ){
            msgbox.setText(Html.fromHtml(text));
        }
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancel.setVisibility(View.GONE);
        dialog.show();

        return dialog;

    }


    private void addUserGsonInSharedPrefrences(UserDetails UserDetails ){
        Gson gson = new Gson();
        String jsonString = gson.toJson(UserDetails);
        //UserDetails user1 = gson.fromJson(jsonString,UserDetails.class);
        if(jsonString!=null) {
            editor.putString("UserDetails", jsonString);
            editor.commit();
            System.out.println("-----------validateLoginDataBaseApi UserDetails"+jsonString);
        }

    }



    @Override
    public void onLoginSucuess() {
        iLoginView.sendPParcelableObj(userDetails);
    }

    @Override
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        /**
         * Once the Google Sign in is successful,
         * Firebase authentication is done using Google's Sign In credentials.
         * And finally the users details are stored on the Firebase Authentication console.
         *
         * @param acct Google's Sign in account.
         */
        // [START auth_with_google]
            Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
            // [START_EXCLUDE silent]
        iLoginView.setLoader(View.VISIBLE);
            // [END_EXCLUDE]
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.snack_error_acct), Snackbar.LENGTH_SHORT).show();

                                updateUI(null);
                            }
                            // [START_EXCLUDE]
                            iLoginView.setLoader(View.GONE);
                            // [END_EXCLUDE]
                        }
                    });
    }

    @Override
    public void updateUI(final FirebaseUser user) {
        System.out.println("SignInPresenter user : " + user);
        if (user != null) {
            state = true;
            userId = user.getUid();
            userName = user.getDisplayName();
            userEmailAdress = user.getEmail();
            userImageUrl = String.valueOf(user.getPhotoUrl());
            userMob = user.getPhoneNumber();
            iLoginView.setLoader(View.GONE);
            System.out.println("SignInPresenter updateUI state : "+state+" userId : "+userId+" userName : "+userName+" userImageUrl : "+userImageUrl+ " userMob : "+userMob);
        } else {
            System.out.println("Something went wrong SignInPresenter updateUI");
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = activity.getAssets().open("data/country_phones.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            //System.out.println("-------------loadJSONFromAsset "+json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public List<CountryDetails> getCountryDetailsList(){
        List<CountryDetails> countryDetailsList = new ArrayList<>();
        try {
            JSONArray m_jArry = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                CountryDetails countryDetails = new CountryDetails();
                countryDetails.setName(jo_inside.getString("name"));
                countryDetails.setDial_code(jo_inside.getString("dial_code"));
                countryDetails.setCode(jo_inside.getString("code"));
                countryDetailsList.add(countryDetails);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return countryDetailsList;
    }


    private void initUser(){
        user = new UserDetails(name,passwd);
    }

}
