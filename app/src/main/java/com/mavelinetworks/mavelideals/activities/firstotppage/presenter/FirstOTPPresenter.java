package com.mavelinetworks.mavelideals.activities.firstotppage.presenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.firstotppage.model.CountryDetails;
import com.mavelinetworks.mavelideals.activities.firstotppage.model.FirstOTPModel;
import com.mavelinetworks.mavelideals.activities.firstotppage.model.IFirstOTPModel;
import com.mavelinetworks.mavelideals.activities.firstotppage.view.IFirstOTPView;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.User;
import com.mavelinetworks.mavelideals.classes.UserDetails;
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


/**
 * Created by User on 8/30/2017.
 */

public class FirstOTPPresenter implements IFirstOTPControler {

    IFirstOTPView iotpView;
    IFirstOTPModel user;
    String contry;
    String zipCode;
    String phno;
    Activity activity;
    String countryJson = "";
    List<CountryDetails> countryDetailsList;
    String session_id = "";
    private ProgressDialog mPdialog;
    private GPStracker gps;
    private CustomDialog mDialogError;
    private RequestQueue queue;


    Gson gson;

    public FirstOTPPresenter(IFirstOTPView iotpView, Activity activity) {
        this.iotpView = iotpView;
        this.activity = activity;
        countryJson = loadJSONFromAsset();
        countryDetailsList = getCountryDetailsList();
        Gson gson = new Gson();
        String jsonString = gson.toJson(countryDetailsList);
        System.out.println("FirstOTPPresenter const countryDetailsList json : " + jsonString);
        iotpView.getCountryDetailList(countryDetailsList);
        gps = new GPStracker(activity);
        queue = VolleySingleton.getInstance(activity).getRequestQueue();
    }




    @Override
    public void doGetData(String contry, String zipCode, String phno) {
        System.out.println("contry : "+contry+" zipCode : "+zipCode+" phno : "+phno);
        this.contry = contry;
        this.zipCode = zipCode;
        this.phno = phno;
        initUser();
        Boolean isLoginSuccess = true;
        final int code = user.validateFirstOTPpage(contry,zipCode,phno);
        if (code!=0) isLoginSuccess = false;
        final Boolean result = isLoginSuccess;
        getOtpPage(result, code);

    }


    public void getOtpPage(final Boolean result, int code){
    /*    Retrofit retrofit = new ApiClient().getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);
        Call<FirstOTPModel> call = webServices.getOtp(phno);
        call.enqueue(new Callback<FirstOTPModel>() {
            @Override
            public void onResponse(Call<FirstOTPModel> call, Response<FirstOTPModel> response) {
                FirstOTPModel firstOTPModel  = (FirstOTPModel) response.body();

                Gson gson = new Gson();
                String jsonString = gson.toJson(firstOTPModel);

                System.out.println(" ----------- getFilters OfferListMap "+jsonString);
                if(jsonString!=null) {
                    System.out.println("-----------getFilters OfferList"+jsonString+" userDetails.getSuccess() : "+firstOTPModel.getSuccess());
                }

                final int code =user.validateRegisterResponseError(firstOTPModel.getSuccess());
                System.out.println("--------- validateLoginDataBaseApi code: "+code);
                Boolean isLoginSuccess =true;
                if (code == 0) {
                    isLoginSuccess = false;
                    System.out.println("--------- validateLoginDataBaseApi isError: "+firstOTPModel.getSuccess());
                    //Toast.makeText((Context) iLoginView, userDetails.getSuccess(), Toast.LENGTH_SHORT).show();
                    System.out.println("-----validateLoginDataBaseApi  data unSuccess ");
                } else {
                    session_id = firstOTPModel.getSession_id();
                    System.out.println("----- validateLoginDataBaseApi isError: "+firstOTPModel.getSuccess());
                    //Toast.makeText((Context) iotpView, "Login Successful", Toast.LENGTH_SHORT).show();
                    iotpView.validatedSendData(result, code,session_id);
                    System.out.println("----- validateLoginDataBaseApi data Successful ");
                }
                Boolean result = isLoginSuccess;
                System.out.println("----- sendRegisteredDataAndValidateResponse second Data Please see, code = " + code + ", result: " + result);
                //iotpView.validatedSendData(result, code,session_id);
            }

            @Override
            public void onFailure(Call<FirstOTPModel> call, Throwable t) {
                Boolean isLoginSuccess = false;
                Boolean result = isLoginSuccess;
                int code = -77;
                iotpView.validatedSendData(result, code,session_id);
                System.out.println("----- onFailure second Data Please see, printStackTrace = "+t.getMessage());

                t.printStackTrace();
                //Toast.makeText((Context) iRegisterView, "ErrorMessage"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/
        int guest_id = 0;
        mPdialog = new ProgressDialog(activity);
        mPdialog.setMessage("Loading ...");
        mPdialog.show();
        final double lat= gps.getLatitude();
        final double lng= gps.getLongitude();

        final int finalGuest_id = guest_id;
        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_OTP, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    if(APP_DEBUG) { Log.e("response", response); }
                    System.out.println("response userLogin : "+response);


                    JSONObject js = new JSONObject(response);
                    UserParser mUserParser = new UserParser(js);
                    FirstOTPModel firstOTPModel = new FirstOTPModel();



                    //Integer success = Integer.valueOf(js.getString(js.getString("success")));

                    int success = Integer.parseInt(mUserParser.getStringAttr(Tags.SUCCESS));

                    if(success==1){

                        String sessionId=js.getString("session_id");
                        firstOTPModel.setSession_id(sessionId);
                        iotpView.validatedRespone(sessionId);
                        mPdialog.dismiss();



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

                params.put("token", Utils.getToken(activity.getBaseContext()));
                params.put("mac_adr", ServiceHandler.getMacAddr());
                params.put("mobile",phno);

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
        user = new FirstOTPModel(contry,zipCode,phno);
    }

}
