package com.mavelinetworks.mavelideals.activities.signuppage.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.activities.SignupActivity;
import com.mavelinetworks.mavelideals.activities.signuppage.model.IUserDetails;
import com.mavelinetworks.mavelideals.activities.signuppage.view.ISignupView;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Errors;
import com.mavelinetworks.mavelideals.classes.User;
import com.mavelinetworks.mavelideals.classes.UserDetails;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;

/**
 * Created by User on 20-Jul-18.
 */

public class SignupPresenter implements ISignupPresenter{

    Context context;
    ISignupView iSignupView;
    String FullName;
    String email;
    String mobNo;
    String pswd;
    String confirmPswd;
    Boolean termsAndCondCheck;
    UserDetails userDetails;
    IUserDetails user;
    String auth;
    private RequestQueue queue;
    private CustomDialog mDialogError;
    private ProgressDialog mPdialog;
    private GPStracker gps;
    private String TAG = ".SignupActivity";

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;

    public SignupPresenter(Context context,
                           ISignupView iSignupView,
                           SharedPreferences sharedPrefs,
                           SharedPreferences.Editor editor) {
        this.context = context;
        this.iSignupView = iSignupView;
        this.sharedPrefs = sharedPrefs;
        this.editor = editor;
        gps = new GPStracker(context);
        queue = VolleySingleton.getInstance(context).getRequestQueue();
    }

    @Override
    public void setTermCondMsg(TextView v) {
        SpannableString ss = new SpannableString(context.getString(R.string.signup_term_cond));
        final ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(final View textView) {
                iSignupView.moveToTermsAndConductionPage();
            }

            @Override
            public void updateDrawState(final TextPaint textPaint) {
                textPaint.setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.red));
                textPaint.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan,13,30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        v.setText(ss);
        v.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onRegisteredSucuess() {
        iSignupView.sendPParcelableObj(userDetails);
    }

    @Override
    public void doRegister(String FullName, String email, String mobNo, String pswd, String confirmPswd, Boolean termsAndCondCheck) {
        System.out.println("-------------------RegisterPresenter doRegister FullName : "+FullName+" Password : "+pswd+" RetypePassword : "+confirmPswd+" email : "+email+" phno : "+mobNo+" termsAndCondCheck : "+termsAndCondCheck);
        this.FullName = FullName;
        this.pswd = pswd;
        this.confirmPswd = confirmPswd;
        this.email = email;
        this.mobNo = mobNo;
        this.termsAndCondCheck = termsAndCondCheck;
        initUser();
        Boolean isLoginSuccess = true;
        final int code = user.validateUserDetails(FullName,email,mobNo,pswd,confirmPswd,termsAndCondCheck);
        if (code != 0) {
            isLoginSuccess = false;
        } else {
            sendRegisteredDataAndValidateResponse();
        }
        Boolean result = isLoginSuccess;
        iSignupView.onRegister(result, code);
    }

    private void initUser(){
        user = new UserDetails(FullName,pswd,email,auth,mobNo);
    }

    private void addUserGsonInSharedPrefrences(UserDetails userDetails){
        Gson gson = new Gson();
        String jsonString = gson.toJson(userDetails);
        //UserModel user1 = gson.fromJson(jsonString,UserModel.class);
        if(jsonString!=null) {
            editor.putString("UserDetails", jsonString);
            editor.commit();
            System.out.println("-----------sendRegisteredDataAndValidateResponse  UserDetails"+jsonString);

        }

    }


    public void sendRegisteredDataAndValidateResponse(){
        /*Retrofit retrofit = new ApiClient().getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);
        Call<UserDetails> call = webServices.registerUser(FullName,pswd,email,mobNo,"customer");
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                userDetails  = (UserDetails) response.body();
                System.out.println("------- sendRegisteredDataAndValidateResponse  FullName : " + FullName +
                        " Password : " + pswd +
                        " email Address : " + email +
                        " phno : " + mobNo);
               *//* userDetails.setName(FullName);
                userDetails.setPassword(pswd);
                userDetails.setEmail(email);
                userDetails.setTel(mobNo);*//*
                //userDetails.setUserName();

                final int code =user.validateRegisterResponseError(userDetails.getSuccess());
                System.out.println("-----sendRegisteredDataAndValidateResponse  data code :"+code);
                Boolean isLoginSuccess =true;
                if (code == 0) {
                    isLoginSuccess = false;
                    Errors errors = userDetails.getErrors();
                    Toast.makeText((Context) iSignupView, errors.getMobile(), Toast.LENGTH_SHORT).show();
                    System.out.println("-----sendRegisteredDataAndValidateResponse  data unSuccess ");
                } else {
                    Toast.makeText((Context) iSignupView, "Register Successful", Toast.LENGTH_SHORT).show();
                    addUserGsonInSharedPrefrences(userDetails);
                    System.out.println("----- sendRegisteredDataAndValidateResponse data Successful ");
                }
                Boolean result = isLoginSuccess;
                System.out.println("----- sendRegisteredDataAndValidateResponse second Data Please see, code = " + code + ", result: " + result);
                iSignupView.onRegistered(result, code);
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Boolean isLoginSuccess =false;
                Boolean result = isLoginSuccess;
                int code = -77;
                iSignupView.onRegistered(result, code);
                t.printStackTrace();
                System.out.println("----- sendRegisteredDataAndValidateResponse error = " + t.getMessage());

                //Toast.makeText((Context) iRegisterView, "ErrorMessage"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
*/




        mPdialog = new ProgressDialog(context);
        mPdialog.setMessage("Loading ...");
        mPdialog.setCancelable(false);
        mPdialog.show();

        int guest_id = 0;

        if(GuestController.isStored())
            guest_id = GuestController.getGuest().getId();


        final double lat= gps.getLatitude();
        final double lng= gps.getLongitude();

        final int finalGuest_id = guest_id;
        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_SIGNUP, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                mPdialog.dismiss();

                try {

                    if(APP_DEBUG) {   Log.e("response", response); }
                    System.out.println("Sign Up Response : "+response);

                    JSONObject js = new JSONObject(response);
                    UserParser mUserParser = new UserParser(js);
                    int success = Integer.parseInt(mUserParser.getStringAttr(Tags.SUCCESS));

                    if(success==1){

                        List<User> list = mUserParser.getUser();
                        if(list.size()>0){

                            User user = list.get(0);
                            UserDetails userDetails = new UserDetails();
                            userDetails.setUsername(user.getUsername());
                            userDetails.setTel(user.getPhone());
                            userDetails.setName(user.getName());
                            userDetails.setPassword(user.getPassword());
                            userDetails.setLongitude(user.getLongitude());
                            userDetails.setLatitude(user.getLatitude());
                            userDetails.setDistance(user.getDistance());
                            userDetails.setEmail(user.getEmail());
                            userDetails.setId(user.getId());
                            userDetails.setCountry(user.getCountry());
                            userDetails.setToken(user.getToken());
                            userDetails.setTokenGCM(user.getTokenGCM());
                            userDetails.setCity(user.getCity());

                            iSignupView.realMResponse(userDetails);

                            addUserGsonInSharedPrefrences(userDetails);
/*
                            if(APP_DEBUG)
                                Log.e("__","logged "+list.get(0).getUsername());

                            if(imageToUpload!=null)
                                uploadImage(list.get(0).getId());


                            SessionsController.createSession(list.get(0));

                           // startActivity(new Intent(SignupActivity.this,MainActivity.class));*/
                        }

                      /*  startActivity(new Intent(SignupActivity.this,CreateStoreActivity.class));
                        overridePendingTransition(R.anim.lefttoright_enter, R.anim.lefttoright_exit);
                        finish();*/
                    }else{


                        Map<String,String> errors = mUserParser.getErrors();


                        MessageDialog.newDialog(context).onCancelClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MessageDialog.getInstance().hide();
                                iSignupView.resetButton(true);
                            }
                        }).onOkClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MessageDialog.getInstance().hide();
                                iSignupView.resetButton(true);
                            }
                        }).setContent(Translator.print(convertMessages(errors),"Message error")).show();

                    }



                } catch (JSONException e) {
                    e.printStackTrace();

                    Map<String,String> errors = new HashMap<String,String>();
                    errors.put("JSONException:", "Try later \"Json parser\"");

                    MessageDialog.newDialog(context).onCancelClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MessageDialog.getInstance().hide();
                            iSignupView.resetButton(true);
                        }
                    }).onOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            MessageDialog.getInstance().hide();
                            iSignupView.resetButton(true);
                        }
                    }).setContent(Translator.print(convertMessages(errors),"Message error")).show();


                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(APP_DEBUG) {Log.e("ERROR", error.toString());}
                iSignupView.resetButton(true);
                mPdialog.dismiss();
                Map<String,String> errors = new HashMap<String,String>();

                errors.put("NetworkException:", context.getString(R.string.check_nework));
                mDialogError = showErrors(errors);
                mDialogError.setTitle(R.string.network_error);

                //signup.setEnabled(true);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("password",pswd);
                params.put("username",mobNo);
                //params.put("email", email.getText().toString().trim());


                // send "Neighbour" as default value
//                String Job = job.getText().toString().trim().length() >0 ? job.getText().toString() : "Neighour";
//                params.put("job", Job);


                params.put("name", FullName);
                params.put("phone",mobNo);
                params.put("email", email);

                //params.put("image", loadedImageId);
                params.put("lat", String.valueOf(lat));
                params.put("lng", String.valueOf(lng));


                params.put("token", Utils.getToken(context));
                params.put("mac_adr", ServiceHandler.getMacAddr());
                params.put("auth_type","mobile");


                params.put("guest_id", String.valueOf(finalGuest_id));

                if(AppConfig.APP_DEBUG){
                    Log.e("__params__",params.toString());
                }

                return params;

            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);



    }

    public String convertMessages(Map<String,String> errors){
        String text = "";
        for ( String key : errors.keySet() ) {
            if(!text.equals(""))
                text = text+"<br>";


            text = text+"#"+errors.get(key);
        }

        return  text;
    }

    public CustomDialog showErrors(Map<String,String> errors){
        final CustomDialog dialog = new CustomDialog(context);

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




}
