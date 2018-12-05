package com.mavelinetworks.mavelideals.activities.otppagesubmit.presenter;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.mavelinetworks.mavelideals.activities.firstotppage.model.FirstOTPModel;
import com.mavelinetworks.mavelideals.activities.otppagesubmit.model.IOTPModel;
import com.mavelinetworks.mavelideals.activities.otppagesubmit.model.OTPModel;
import com.mavelinetworks.mavelideals.activities.otppagesubmit.view.IOTPView;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Errors;
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
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;


/**
 * Created by User on 8/30/2017.
 */

public class OTPPresenter implements IOTPControler {

    IOTPView iotpView;
    Context context;
    IOTPModel user;
    String edtOPTNo;
    Boolean checkTermsAndConductionBox;
    String PhoneNo;
    String session_id =  "";

    private ProgressDialog mPdialog;
    private GPStracker gps;
    private CustomDialog mDialogError;
    private RequestQueue queue;


    public OTPPresenter(IOTPView iotpView, Context context, String PhoneNo) {
        this.iotpView = iotpView;
        initUser();
        this.context = context;
        this.PhoneNo = PhoneNo;
        gps = new GPStracker(context);
        queue = VolleySingleton.getInstance(context).getRequestQueue();
    }


    @Override
    public void setOPTSecondMsg(TextView v) {
        SpannableString ss = new SpannableString(context.getString(R.string.signup_term_cond));
        final ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(final View textView) {
                iotpView.moveToTermsAndConductionPage();
            }

            @Override
            public void updateDrawState(final TextPaint textPaint) {
                textPaint.setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorHeighLight));
                textPaint.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan,59,76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        v.setText(ss);
        v.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public void resendOtpPage(final Boolean result, int code){
       /* Retrofit retrofit = new ApiClient().getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);
        Call<OTPModel> call = webServices.resendOtp(PhoneNo);
        call.enqueue(new Callback<OTPModel>() {
            @Override
            public void onResponse(Call<OTPModel> call, Response<OTPModel> response) {
                OTPModel otpModel  = (OTPModel) response.body();

                Gson gson = new Gson();
                String jsonString = gson.toJson(otpModel);

                System.out.println(" ----------- getFilters OfferListMap "+jsonString);
                if(jsonString!=null) {
                    System.out.println("-----------getFilters OfferList"+jsonString+" userDetails.getSuccess() : "+otpModel.getSuccess());
                }

                final int code =user.validateRegisterResponseError(otpModel.getSuccess());
                System.out.println("--------- validateLoginDataBaseApi code: "+code);
                Boolean isLoginSuccess =true;
                if (code == 0) {
                    isLoginSuccess = false;
                    Errors errors = otpModel.getErrors();
                    System.out.println("--------- validateLoginDataBaseApi isError: "+errors.getConnect());
                    Toast.makeText((Context) iotpView, errors.getConnect(), Toast.LENGTH_SHORT).show();
                    System.out.println("-----validateLoginDataBaseApi  data unSuccess ");
                } else {
                    System.out.println("----- validateLoginDataBaseApi isError: "+otpModel.getSuccess());
                    Toast.makeText((Context) iotpView, "OTP Verified", Toast.LENGTH_SHORT).show();
                    iotpView.resendOtp(result, code,session_id);
                    System.out.println("----- validateLoginDataBaseApi data Successful ");
                }
                Boolean result = isLoginSuccess;
                System.out.println("----- sendRegisteredDataAndValidateResponse second Data Please see, code = " + code + ", result: " + result);
                iotpView.resendOtp(result, code,session_id);
            }

            @Override
            public void onFailure(Call<OTPModel> call, Throwable t) {
                Boolean isLoginSuccess = false;
                Boolean result = isLoginSuccess;
                int code = -77;
                iotpView.onSubmit(result, code);
                System.out.println("----- onFailure second Data Please see, printStackTrace = "+t.getMessage());

                t.printStackTrace();
                //Toast.makeText((Context) iRegisterView, "ErrorMessage"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/

        int guest_id = 0;
        mPdialog = new ProgressDialog(context);
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


                    int success = Integer.parseInt(mUserParser.getStringAttr(Tags.SUCCESS));


                    if( success==1){

                        String sessionId=js.getString("session_id");
                        firstOTPModel.setSession_id(sessionId);
                        iotpView.resendOtpRespone(sessionId);
                        mPdialog.dismiss();

                    }else{


                        Map<String,String> errors = mUserParser.getErrors();

                        MessageDialog.newDialog(context).onCancelClick(new View.OnClickListener() {
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
                        }).setContent(Translator.print(context.getString(R.string.authentification_error_msg),"Message error")).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    MessageDialog.newDialog(context).onCancelClick(new View.OnClickListener() {
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
                    }).setContent(Translator.print(context.getString(R.string.authentification_error_msg),"Message error (Parser)")).show();


                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(APP_DEBUG) {   Log.e("ERROR", error.toString());}

                // signup.setEnabled(true);

                mPdialog.dismiss();

                Map<String,String> errors = new HashMap<String,String>();

                errors.put("NetworkException:", context.getString(R.string.check_network));
                mDialogError = showErrors(errors);
                mDialogError.setTitle(context.getString(R.string.network_error));

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("token", Utils.getToken(context));
                params.put("mac_adr", ServiceHandler.getMacAddr());
                params.put("mobile",PhoneNo);

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

    @Override
    public void resendOtpPage(String PhoneNo) {
        this.PhoneNo = PhoneNo;
        initUser();
        Boolean isLoginSuccess = true;
        final int code = user.validatePhoneNo(PhoneNo);
        if (code!=0) isLoginSuccess = false;
        final Boolean result = isLoginSuccess;
        resendOtpPage(result, code);

    }

    @Override
    public void doOTPValidationAndCheck(String edtOPTNo, String session_id) {
        this.edtOPTNo= edtOPTNo;
        this.session_id = session_id;
        System.out.println("edtOPTNo : "+edtOPTNo);
        System.out.println("session_id : "+session_id);
        Boolean isLoginSuccess = true;
        final int code = user.validatesSessionKeyAndOtp(edtOPTNo,session_id);
        if (code!=0) {
            isLoginSuccess = false;
            final Boolean result = isLoginSuccess;
            iotpView.onSubmit(result, code);
        }else {
            final Boolean result = isLoginSuccess;
            chkOtpWithServer(result,code);
        }

    }

    public void chkOtpWithServer(final Boolean result, int code){
       /* Retrofit retrofit = new ApiClient().getRetrofitClient();
        WebServices webServices = retrofit.create(WebServices.class);
        Call<OTPModel> call = webServices.verifyOtp(edtOPTNo,session_id);
        call.enqueue(new Callback<OTPModel>() {
            @Override
            public void onResponse(Call<OTPModel> call, Response<OTPModel> response) {
                OTPModel otpModel  = (OTPModel) response.body();

                Gson gson = new Gson();
                String jsonString = gson.toJson(otpModel);

                System.out.println(" ----------- getFilters OfferListMap "+jsonString);
                if(jsonString!=null) {
                    System.out.println("-----------getFilters OfferList"+jsonString+" userDetails.getSuccess() : "+otpModel.getSuccess());
                }

                final int code =user.validateSessionOtpResponse(otpModel.getSuccess());
                System.out.println("--------- validateLoginDataBaseApi code: "+code);
                Boolean isLoginSuccess =true;
                if (code == 0) {
                    isLoginSuccess = false;
                    Errors errors = otpModel.getErrors();
                    System.out.println("--------- validateLoginDataBaseApi isError: "+errors.getConnect());
                    Toast.makeText((Context) iotpView, errors.getOtp(), Toast.LENGTH_SHORT).show();
                    System.out.println("-----validateLoginDataBaseApi  data unSuccess ");
                } else {
                    System.out.println("----- validateLoginDataBaseApi isError: "+otpModel.getSuccess());
                    Toast.makeText((Context) iotpView, "Otp Verified", Toast.LENGTH_SHORT).show();
                    iotpView.onSubmit(result, code);
                    System.out.println("----- validateLoginDataBaseApi data Successful ");
                }
                Boolean result = isLoginSuccess;
                System.out.println("----- sendRegisteredDataAndValidateResponse second Data Please see, code = " + code + ", result: " + result);
                iotpView.onSubmit(result, code);
            }

            @Override
            public void onFailure(Call<OTPModel> call, Throwable t) {
                Boolean isLoginSuccess = false;
                Boolean result = isLoginSuccess;
                int code = -78;
                iotpView.onSubmit(result, code);
                System.out.println("----- onFailure second Data Please see, printStackTrace = "+t.getMessage());

                t.printStackTrace();
                //Toast.makeText((Context) iRegisterView, "ErrorMessage"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/


        int guest_id = 0;
        mPdialog = new ProgressDialog(context);
        mPdialog.setMessage("Loading ...");
        mPdialog.show();
        final double lat= gps.getLatitude();
        final double lng= gps.getLongitude();

        final int finalGuest_id = guest_id;
        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_VERIFY_OTP, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    if(APP_DEBUG) { Log.e("response", response); }
                    System.out.println("response chkOtpWithServer : "+response);


                    JSONObject js = new JSONObject(response);
                    UserParser mUserParser = new UserParser(js);
                    FirstOTPModel firstOTPModel = new FirstOTPModel();


                    //Integer success = Integer.valueOf(js.getString(js.getString("success")));
                    int success = Integer.parseInt(mUserParser.getStringAttr(Tags.SUCCESS));

                    if(success==1){
                        iotpView.onSubmitVerify();
                        mPdialog.dismiss();

                    }else{


                        Map<String,String> errors = mUserParser.getErrors();

                        MessageDialog.newDialog(context).onCancelClick(new View.OnClickListener() {
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
                        }).setContent(Translator.print(context.getString(R.string.authentification_error_msg),"Message error")).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    MessageDialog.newDialog(context).onCancelClick(new View.OnClickListener() {
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
                    }).setContent(Translator.print(context.getString(R.string.authentification_error_msg),"Message error (Parser)")).show();


                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(APP_DEBUG) {   Log.e("ERROR", error.toString());}

                // signup.setEnabled(true);

                mPdialog.dismiss();

                Map<String,String> errors = new HashMap<String,String>();

                errors.put("NetworkException:", context.getString(R.string.check_network));
                mDialogError = showErrors(errors);
                mDialogError.setTitle(context.getString(R.string.network_error));

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("token", Utils.getToken(context));
                params.put("mac_adr", ServiceHandler.getMacAddr());
                params.put("otp",edtOPTNo);
                params.put("session_id",session_id);

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

    private void initUser(){
        user = new OTPModel(edtOPTNo,checkTermsAndConductionBox);
    }

}
