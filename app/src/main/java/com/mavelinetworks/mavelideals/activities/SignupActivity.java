package com.mavelinetworks.mavelideals.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mavelinetworks.mavelideals.AppController;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.adapter.CountriesAutoCompleteAdapter;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.AppContext;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.User;
import com.mavelinetworks.mavelideals.controllers.sessions.GuestController;
import com.mavelinetworks.mavelideals.controllers.sessions.SessionsController;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.UserParser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;
import com.mavelinetworks.mavelideals.utils.ImageUtils;
import com.mavelinetworks.mavelideals.utils.MessageDialog;
import com.mavelinetworks.mavelideals.utils.Translator;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.mavelinetworks.mavelideals.views.CustomDialog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, ImageUtils.PrepareImagesData.OnCompressListner {

    private MaterialEditText login,password,cpassword,email,phone,name;
    private Button continueBtn,signup,back,getImage;
    private LinearLayout infos_layout,connect_layout;

    private CircularImageView userimage;
    //init request http
    private RequestQueue queue;
    private CustomDialog mDialogError;

    private TextView codeCountry;
    private MaterialAutoCompleteTextView countries;
    private static String codeCountryString = "";


    private String TAG = ".SignupActivity";
    private ProgressDialog mPdialog;

    @Override
    protected void onResume() {
        super.onResume();
    }

    private GPStracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TypefaceHelper.typeface(this);


        infos_layout  = (LinearLayout) findViewById(R.id.infos_layout);
        connect_layout  = (LinearLayout) findViewById(R.id.connect_layout);

        userimage = (CircularImageView) findViewById(R.id.userimage);


        /*connect_layout.setVisibility(View.GONE);
        infos_layout.setVisibility(View.VISIBLE);*/

        gps = new GPStracker(this);

        if(SessionsController.isLogged()){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

        AppController application = (AppController) getApplication();

        queue = VolleySingleton.getInstance(this).getRequestQueue();

        name = (MaterialEditText) findViewById(R.id.name);
        countries = (MaterialAutoCompleteTextView) findViewById(R.id.country);
        //countries.setHintTextColor(getResources().getColor(R.color.gray));
        codeCountry = (TextView) findViewById(R.id.codeCountry);

        login = (MaterialEditText) findViewById(R.id.login);
        password = (MaterialEditText) findViewById(R.id.password);
        cpassword = (MaterialEditText) findViewById(R.id.cpassword);
        email = (MaterialEditText) findViewById(R.id.email);
        phone = (MaterialEditText) findViewById(R.id.mobile);
        back = (Button) findViewById(R.id.back);

        signup = (Button) findViewById(R.id.signup);
        continueBtn = (Button) findViewById(R.id.next);
        signup.setOnClickListener(this);
        continueBtn.setOnClickListener(this);

        Utils.setFont(this, login, "");
        Utils.setFont(this, password, "");
        Utils.setFont(this, cpassword, "");
        Utils.setFont(this, email, "");
        Utils.setFont(this, signup, "");

        loadCountries();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect_layout.setVisibility(View.GONE);
                infos_layout.setVisibility(View.VISIBLE);
            }
        });

        getImage = (Button) findViewById(R.id.getImage);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFromGallery();
            }
        });

        ActivityCompat.requestPermissions(SignupActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

    }

    private boolean checkUserInfos(){

        if(!codeCountryString.equals("")){
            return true;
        }
        return false;
    }

    private boolean checkUserConnect(){

        if(!login.getText().toString().equals("") &&
                !password.getText().toString().equals("") ){
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.next) {

//            if(checkUserInfos()){
//                connect_layout.setVisibility(View.VISIBLE);
//                infos_layout.setVisibility(View.GONE);
//            }else{
//                Toast.makeText(this,getString(R.string.fill_all_fields),Toast.LENGTH_LONG).show();
//            }

        }else if(v.getId()==R.id.signup) {

//            if(!checkUserInfos()) {
//
//                connect_layout.setVisibility(View.VISIBLE);
//                infos_layout.setVisibility(View.VISIBLE);
//
//                Toast.makeText(this,getString(R.string.fill_all_fields),Toast.LENGTH_LONG).show();
//                return;
//            }

            if(checkUserConnect()) {

                if (ServiceHandler.isNetworkAvailable(this)) {

                    doSignup();

                } else {
                    ServiceHandler.showSettingsAlert(this);
                }

//                if (cpassword.getText().toString().equals(password.getText().toString())) {
//
//
//                } else {
//                    //show message error
//                    MessageDialog.newDialog(SignupActivity.this)
//                            .onCancelClick(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    MessageDialog.getInstance().hide();
//                                }
//                            }).onOkClick(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            MessageDialog.getInstance().hide();
//                        }
//                    }).setContent(Translator.print("Passwords do not match!", "Message error")).show();
//                }
            }else
                Toast.makeText(this,getString(R.string.fill_all_fields),Toast.LENGTH_LONG).show();

        }

    }

    private void doSignup(){

        signup.setEnabled(false);

        mPdialog = new ProgressDialog(this);
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
                Constances.API.API_USER_SIGNUP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                signup.setEnabled(true);

                mPdialog.dismiss();

                try {

                    if(APP_DEBUG) {   Log.e("response", response); }

                    JSONObject js = new JSONObject(response);
                    UserParser mUserParser = new UserParser(js);
                    int success = Integer.parseInt(mUserParser.getStringAttr(Tags.SUCCESS));

                    if(success==1){

                        List<User> list = mUserParser.getUser();
                        if(list.size()>0){

                            if(APP_DEBUG)
                                Log.e("__","logged "+list.get(0).getUsername());

                            if(imageToUpload!=null)
                                uploadImage(list.get(0).getId());


                            SessionsController.createSession(list.get(0));

                            startActivity(new Intent(SignupActivity.this,MainActivity.class));
                        }

                      /*  startActivity(new Intent(SignupActivity.this,CreateStoreActivity.class));
                        overridePendingTransition(R.anim.lefttoright_enter, R.anim.lefttoright_exit);
                        finish();*/
                    }else{


                        Map<String,String> errors = mUserParser.getErrors();


                        MessageDialog.newDialog(SignupActivity.this).onCancelClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MessageDialog.getInstance().hide();
                            }
                        }).onOkClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                MessageDialog.getInstance().hide();
                            }
                        }).setContent(Translator.print(convertMessages(errors),"Message error")).show();

                    }



                } catch (JSONException e) {
                    e.printStackTrace();

                    Map<String,String> errors = new HashMap<String,String>();
                    errors.put("JSONException:", "Try later \"Json parser\"");

                    MessageDialog.newDialog(SignupActivity.this).onCancelClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MessageDialog.getInstance().hide();
                        }
                    }).onOkClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            MessageDialog.getInstance().hide();
                        }
                    }).setContent(Translator.print(convertMessages(errors),"Message error")).show();


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(APP_DEBUG) {Log.e("ERROR", error.toString());}

                mPdialog.dismiss();
                Map<String,String> errors = new HashMap<String,String>();

                errors.put("NetworkException:", getString(R.string.check_nework));
                mDialogError = showErrors(errors);
                mDialogError.setTitle(R.string.network_error);

                signup.setEnabled(true);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("password",password.getText().toString().trim());
                params.put("username",name.getText().toString().trim());
                //params.put("email", email.getText().toString().trim());


                // send "Neighbour" as default value
//                String Job = job.getText().toString().trim().length() >0 ? job.getText().toString() : "Neighour";
//                params.put("job", Job);


                params.put("name", login.getText().toString().trim());
                params.put("phone", codeCountryString+"-"+phone.getText().toString().trim());
                params.put("email", email.getText().toString());

                params.put("image", loadedImageId);
                params.put("lat", String.valueOf(lat));
                params.put("lng", String.valueOf(lng));


                params.put("token",Utils.getToken(getBaseContext()));
                params.put("mac_adr",ServiceHandler.getMacAddr());
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

    private void uploadImage(final int uid){

      Toast.makeText(this,getString(R.string.fileUploading),Toast.LENGTH_LONG).show();

            SimpleRequest request = new SimpleRequest(Request.Method.POST,
                    Constances.API.API_USER_UPLOAD64, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                   // pdialog.dismiss();
                    try {

                        if(AppConfig.APP_DEBUG)
                            Log.e("SignUpUload", response);
                        JSONObject js = new JSONObject(response);

                        UserParser mUserParser = new UserParser(js);
                        int success = Integer.parseInt(mUserParser.getStringAttr(Tags.SUCCESS));
                        if(success==1) {

                            final List<User> list = mUserParser.getUser();
                            if (list.size() > 0) {
                                SessionsController.updateSession(list.get(0));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(APP_DEBUG) {
                        Log.e("ERROR", error.toString());
                        Toast.makeText(SignupActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                    //pdialog.dismiss();

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                        Bitmap bm = BitmapFactory.decodeFile(imageToUpload.getPath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                        params.put("image", encodedImage);

                        params.put("int_id", String.valueOf(uid));
                        params.put("type", "user");


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

    CountriesAutoCompleteAdapter countriesAdapter;
    private void loadCountries(){

        countriesAdapter = new CountriesAutoCompleteAdapter(this,R.layout.list_item);
        countries.setAdapter(countriesAdapter);
        countries.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        if(countriesAdapter.getCodeCountries().size()>0){

            //Show code country
            codeCountry.setVisibility(View.VISIBLE);
            try {
                codeCountryString = countriesAdapter.getCodeCountries().get(position);
                codeCountry.setText(codeCountryString);
                phone.setEnabled(true);

                if(AppConfig.APP_DEBUG)
                    Toast.makeText(this,"Selected "+countriesAdapter.getCodeCountries().size(),Toast.LENGTH_LONG).show();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {

        if(connect_layout.isShown() && !infos_layout.isShown()){
            connect_layout.setVisibility(View.GONE);
            infos_layout.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.righttoleft_enter, R.anim.righttoleft_exit);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode==GALLERY_REQUEST) {

            try{

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                try {

                    String  createNewFileDest= createImageFile(this);

                    new ImageUtils.PrepareImagesData(
                            this,
                            picturePath.toString(),
                            createNewFileDest,
                            this).execute();

                } catch (IOException e) {

                    if(AppContext.DEBUG)
                        e.printStackTrace();

                }

            }catch (Exception e)
            {
                if(AppContext.DEBUG)
                    e.printStackTrace();
            }



        }
    }

    private int GALLERY_REQUEST = 103;
    protected void getFromGallery(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_REQUEST);
    }


    public static String createImageFile(Context contxt) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = contxt.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents

        return image.getAbsolutePath();
    }

    private Uri imageToUpload=null;
    private String loadedImageId="";

    @Override
    public void onCompressed(String newPath, String oldPath) {

        if(APP_DEBUG)
            Toast.makeText(this,"PATH:"+newPath.toString(),Toast.LENGTH_SHORT).show();

        File mFile =  new File(newPath);

        Picasso.with(this).load(mFile).fit().centerCrop()
                    .placeholder(R.drawable.profile_placeholder).into(userimage);

        imageToUpload  = Uri.parse(newPath);
    }

}
