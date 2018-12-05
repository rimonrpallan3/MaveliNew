package com.mavelinetworks.mavelideals.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Store;
import com.mavelinetworks.mavelideals.classes.User;
import com.mavelinetworks.mavelideals.controllers.sessions.SessionsController;
import com.mavelinetworks.mavelideals.load_manager.ViewManager;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.ImagesParser;
import com.mavelinetworks.mavelideals.parser.api_parser.StoreParser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.mavelinetworks.mavelideals.views.CustomDialog;
import com.rey.material.widget.CheckBox;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;

public class MyStoreActivity extends AppCompatActivity  implements View.OnClickListener {


    public ViewManager mViewManager;
    /////////////////
    LinearLayout __type;
    TextView type_status;
    CheckBox select_type;
    int type=0;
    int COUNT = 0;
    private LinearLayout cont_no_added;
    private LinearLayout cont_added;
    private Button addnew;
    private GPStracker mGps;
    private CustomDialog mDialogError;
    private Uri imageUri;
    private EditText name;
    private EditText address;
    private ImageView imageView;
    private Button save;
    private Button makeImage,getImage;
    private User user;
    private Store store;
    private EditText phone;
    private String TAG = ".MyStoreActivity";
    private boolean loaded = false;
    //init request http
    private RequestQueue queue;
    private int success=0;
    private JSONObject imageObjects;
    private ProgressDialog pdialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_store);

        __type = (LinearLayout) findViewById(R.id.__type);
        type_status = (TextView) findViewById(R.id.type_status);
        select_type = (CheckBox) findViewById(R.id.select_type);
        
        __type.setVisibility(View.INVISIBLE);

        select_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type_status.setText("Oui");
                    type = 2;
                } else {
                    type = 1;
                    type_status.setText("Non");
                }
            }
        });



        cont_no_added = (LinearLayout) findViewById(R.id.container_no_added);
        cont_added = (LinearLayout) findViewById(R.id.container_added);

        user = SessionsController.getSession().getUser();

        mViewManager = new ViewManager(this);
        mViewManager.setLoading(findViewById(R.id.loading));
        mViewManager.setNoLoading(findViewById(R.id.content_my_store));
        mViewManager.setError(findViewById(R.id.error));
        mViewManager.setEmpty(findViewById(R.id.empty));
        mViewManager.loading();


        mGps = new GPStracker(this);

        queue = VolleySingleton.getInstance(this).getRequestQueue();



        save = (Button) findViewById(R.id.save);
        makeImage = (Button) findViewById(R.id.makeimage);
        name = (EditText) findViewById(R.id.name);
        address = (EditText) findViewById(R.id.address);

        phone = (EditText) findViewById(R.id.phone);


        save.setOnClickListener(this);

        imageView  = (ImageView) findViewById(R.id.image);


        Utils.setFont(this, save, "");
        Utils.setFont(this,makeImage,"");

        Utils.setFont(this, name, "");
        Utils.setFont(this, address, "");

        Utils.setFont(this, phone, "");

        addnew = (Button) findViewById(R.id.createbtn);

       /* addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyStoreActivity.this, LoginActivity.class);
                i.putExtra("direction",CreateStoreActivity.class);
                startActivity(i);
                finish();
            }
        });*/

        if(user!=null){

            //initCamera();
            getStore();

        }else{
            /*Intent i = new Intent(MyStoreActivity.this, LoginActivity.class);
            i.putExtra("direction",CreateStoreActivity.class);
            startActivity(i);
            finish();*/

        }

    }

    @Override
    public void onClick(View v) {


        if(v.getId()==R.id.save){

            if(ServiceHandler.isNetworkAvailable(this)){


                    if(imageObjects==null && loaded==true) {

                        uploadImage();

                    }else{
                        syncToServer();
                    }

            }else{
                ServiceHandler.showSettingsAlert(this);
            }

        }

    }

    private void uploadImage(){


        pdialog = new ProgressDialog(this);
        pdialog.setMessage("Loading ...");
        pdialog.setCancelable(false);
        pdialog.show();

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_UPLOAD64, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    loaded = false;

                    JSONObject js = new JSONObject(response);

                    ImagesParser mImageParser = new ImagesParser(js);
                    success = Integer.parseInt( mImageParser.getStringAttr(Tags.SUCCESS) );

                    if(success==1){

                        mImageParser = new ImagesParser(js.getJSONObject("data"));
                        imageObjects = new JSONObject();
                        imageObjects.put("0", mImageParser.getStringAttr("image"));


                        if(APP_DEBUG) {  Log.e("response", imageObjects.toString()); }

                        makeImage.setEnabled(false);

                        syncToServer();

                    }else{
                        //show error

                        imageObjects = null;
                        Map<String,String> errors = mImageParser.getErrors();

                        mDialogError = showErrors(errors);
                        mDialogError.setTitle("transfer error ");

                        pdialog.dismiss();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    pdialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(APP_DEBUG) {  Log.e("ERROR", error.toString()); }

                pdialog.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                if(imageUri!=null) {
                    Bitmap bm = BitmapFactory.decodeFile(imageUri.getPath());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                    params.put("image",encodedImage);
                }



                params.put("token",Utils.getToken(getBaseContext()));
                params.put("mac_adr",ServiceHandler.getMacAddr());



                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);



    }

    private void syncToServer(){

        if(pdialog==null){

            pdialog = new ProgressDialog(this);
            pdialog.setMessage("Loading...");
            pdialog.setCancelable(false);
            pdialog.show();

        }else{
            pdialog.show();
        }


        mGps = new GPStracker(this);

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_UPDATE_STORE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                pdialog.dismiss();

                try {



                    JSONObject js = new JSONObject(response);


                    StoreParser mStoreParser = new StoreParser(js);

                    int success =Integer.parseInt(mStoreParser.getStringAttr(Tags.SUCCESS));

                    if(success==1) {

                        startActivity(new Intent(MyStoreActivity.this, MainActivity.class));
                        overridePendingTransition(R.anim.righttoleft_enter, R.anim.righttoleft_exit);
                        finish();

                    }else if(success==-1){

                        Map<String,String> errors = mStoreParser.getErrors();

                        mDialogError = showErrors(errors);
                        mDialogError.setTitle("Error adding ");

                        mDialogError.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                                overridePendingTransition(R.anim.righttoleft_enter, R.anim.righttoleft_exit);
                                startActivity(new Intent(MyStoreActivity.this,MainActivity.class));
                            }
                        });


                    }else{
                        Map<String,String> errors = mStoreParser.getErrors();

                        mDialogError = showErrors(errors);
                        mDialogError.setTitle("Error adding ");

                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                    Map<String,String> errors = new HashMap<String,String>();
                    errors.put("JSONException:", "Try later  \"Json parser\"");
                    mDialogError = showErrors(errors);
                    mDialogError.setTitle("Exception parser error");

                    mDialogError.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogError.dismiss();
                            finish();
                        }
                    });

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(APP_DEBUG) { Log.e("ERROR", error.toString()); }


                pdialog.dismiss();

                Map<String,String> errors = new HashMap<String,String>();

                errors.put("NetworkException:", "Check your network connexion ");
                mDialogError = showErrors(errors);
                if(APP_DEBUG) { mDialogError.setTitle("Network error "); }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                if(imageObjects!=null)
                    params.put("images",imageObjects.toString());


                User user = SessionsController.getSession().getUser();
                if(user!=null)
                    params.put("user_id",user.getId()+"");
                else
                    params.put("user_id","0");

                if(store==null)
                    params.put("store_id","0");
                else
                    params.put("store_id",store.getId()+"");

                params.put("name",name.getText().toString());
                params.put("description",address.getText().toString());



                params.put("type",type+"");
                params.put("phone",phone.getText().toString()+"");
                params.put("detail","");


                params.put("token",Utils.getToken(getBaseContext()));
                params.put("mac_adr",ServiceHandler.getMacAddr() );



                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }

    private void getStore(){

        mViewManager.loading();

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_GET_STORES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{


                    mViewManager.showResult();

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("response", jsonObject.toString());


                   // Log.e("response",response);

                    final StoreParser mStoreParser = new StoreParser(jsonObject);

                    COUNT = mStoreParser.getIntArg(Tags.COUNT);


                    int success = Integer.parseInt(mStoreParser.getStringAttr(Tags.SUCCESS));

                    if(success==1){
                        List<Store> list = mStoreParser.getStore();


                        if(list.size()==0){

                            cont_no_added.setVisibility(View.VISIBLE);
                            cont_added.setVisibility(View.GONE);
                        }else{


                            store = list.get(0);

                            name.setText(store.getName());
                            address.setText(store.getAddress());

                            phone.setText(store.getPhone());



                            //imageObjects = new JSONObject();
                            //imageObjects.put("0", store.getImageJson());

                            if(store.getImages()!=null)
                            if(store.getImages().getUrl200_200()!=null)

                            Picasso.with(getBaseContext())
                                    .load(store.getImages()
                                            .getUrl200_200())
                                    .fit().centerCrop().placeholder(R.drawable.def_logo)
                                    .into(imageView);
                            else
                                loaded=true;

                            if(store.getType()==1){
                                type =1;
                                __type.setVisibility(View.VISIBLE);

                                select_type.setChecked(false);
                            }else{

                                if(store.getType()==2){
                                    select_type.setChecked(true);
                                    __type.setVisibility(View.VISIBLE);
                                }else{
                                    __type.setVisibility(View.GONE);
                                }
                                type =store.getType();

                            }


                            cont_no_added.setVisibility(View.GONE);
                            cont_added.setVisibility(View.VISIBLE);
                        }

                    }else{

                        cont_no_added.setVisibility(View.VISIBLE);
                        cont_added.setVisibility(View.GONE);
                    }





                }catch (JSONException e){
                    //send a rapport to support

                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(APP_DEBUG) {  Log.e("ERROR", error.toString()); }

                mViewManager.error();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                User user = SessionsController.getSession().getUser();
                params.put("limit", "1");

                if(user!=null) {
                    params.put("user_id", user.getId() + "");
                    params.put("type","0");
                }else{
                    params.put("type","-1");
                }

                params.put("token",Utils.getToken(getBaseContext()));
                params.put("mac_adr", ServiceHandler.getMacAddr() );


                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);



    }




    public CustomDialog showErrors(Map<String,String> errors){
        final CustomDialog dialog = new CustomDialog(this);

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
    public void onBackPressed() {

        if(mDialogError!=null){
            if(mDialogError.isShowing())
                mDialogError.dismiss();

        } else {

            startActivity(new Intent(MyStoreActivity.this,MainActivity.class));
            overridePendingTransition(R.anim.righttoleft_enter, R.anim.righttoleft_exit);
            super.onBackPressed();
        }






    }

}
