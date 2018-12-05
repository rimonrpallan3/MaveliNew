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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.mavelinetworks.mavelideals.classes.CountriesModel;
import com.mavelinetworks.mavelideals.classes.User;
import com.mavelinetworks.mavelideals.controllers.CountriesController;
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

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, ImageUtils.PrepareImagesData.OnCompressListner {

    private EditText login,password,cpassword,email,phone,name;
    private Button save,getImage;

    private CircularImageView userimage;
    //init request http
    private RequestQueue queue;
    private CustomDialog mDialogError;

    private TextView codeCountry;
    private MaterialAutoCompleteTextView countries;
    private static String codeCountryString = "";
    private ProgressDialog mPdialog;

    @Override
    protected void onResume() {
        super.onResume();
    }
    private GPStracker gps;


    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        TypefaceHelper.typeface(this);

        initToolbar();
        APP_TITLE_VIEW.setText(getString(R.string.editProfile));

        if(SessionsController.isLogged()){
            mUser = SessionsController.getSession().getUser();
            //APP_TITLE_VIEW.setText(mUser.getUsername());
        }else
            finish();

        userimage = (CircularImageView) findViewById(R.id.userimage);

        gps = new GPStracker(this);



        AppController application = (AppController) getApplication();

        queue = VolleySingleton.getInstance(this).getRequestQueue();

        countries = (MaterialAutoCompleteTextView) findViewById(R.id.country);
        codeCountry = (TextView) findViewById(R.id.codeCountry);

        name = (EditText) findViewById(R.id.name);
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        cpassword = (EditText) findViewById(R.id.cpassword);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.mobile);
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);

        Utils.setFont(this, login, "");
        Utils.setFont(this, password, "");
        Utils.setFont(this, cpassword, "");
        Utils.setFont(this, email, "");


        getImage = (Button) findViewById(R.id.getImage);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFromGallery();
            }
        });


        checkPermission();

        putDataIntoViews();

    }

    private void checkPermission(){

        ActivityCompat.requestPermissions(ProfileActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(android.R.id.home==item.getItemId()){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void putDataIntoViews(){

        String phoneNumber = mUser.getPhone();

        if(phoneNumber!=null)
        if(!phoneNumber.equals("")){
            try {
                String[] k = phoneNumber.split("-");
                phone.setText(k[1]);
                codeCountry.setText(k[0]);
                CountriesModel c  = CountriesController.findByDialCode(k[0]);
                codeCountryString = c.getDial_code();
                if(c!=null){
                    countries.setText(c.getName());
                }
            }catch (Exception e){

            }
        }

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCountries();
            }
        },2000);


        if(mUser.getImages()!=null){
            Picasso.with(this).load(mUser.getImages().getUrl200_200()).fit().centerCrop().into(userimage);
        }


        name.setText(mUser.getName());
        login.setText(mUser.getUsername());
        email.setText(!mUser.getEmail().equals("null") ? mUser.getEmail() : ""  );
        //job.setText(mUser.getJob());

    }


    private boolean checkAllProfileFields(){

        if(!login.getText().toString().equals("")
                && !email.getText().toString().equals("")
                && !name.getText().toString().equals("")){
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.save){

            if(checkAllProfileFields()) {

                checkAndLoad();

//                if ( !password.getText().equals("")) {
//
//                    MessageDialog.newDialog(ProfileActivity.this)
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
//
//                }else {
//
//                }

            }else
                Toast.makeText(this,getString(R.string.fill_all_fields),Toast.LENGTH_LONG).show();

        }else
        {
            Toast.makeText(this,"Please Check your confirm passowrd",Toast.LENGTH_LONG).show();
        }

    }

    private void doSave(){


        mPdialog = new ProgressDialog(this);
        mPdialog.setMessage("Loading ...");
        mPdialog.setCancelable(false);
        mPdialog.show();

        final String oldUsername = mUser.getUsername();
        final String userId = String.valueOf(mUser.getId());

        final double lat= gps.getLatitude();
        final double lng= gps.getLongitude();

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_UPDATE_ACCOUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                mPdialog.dismiss();

                try {

                    if(APP_DEBUG) {   Log.e("response", response); }

                    JSONObject js = new JSONObject(response);

                    UserParser mUserParser = new UserParser(js);

                    int success = Integer.parseInt(mUserParser.getStringAttr(Tags.SUCCESS));

                    if(success==1){

                        final List<User> list = mUserParser.getUser();
                        if(list.size()>0){

                            if(imageToUpload!=null)
                                uploadImage(list.get(0).getId());

                            if(APP_DEBUG)
                                Log.e("__","logged "+list.get(0).getUsername());

                            SessionsController.logOut();
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SessionsController.createSession(list.get(0));
                                    if(!SessionsController.isLogged()){
                                        ActivityCompat.finishAffinity(ProfileActivity.this);
                                        startActivity(new Intent(ProfileActivity.this,SplashActivity.class));
                                    }
                                }
                            },2000);

                            finish();

                        }

                      /*  startActivity(new Intent(SignupActivity.this,CreateStoreActivity.class));
                        overridePendingTransition(R.anim.lefttoright_enter, R.anim.lefttoright_exit);
                        finish();*/
                    }else{


                        Map<String,String> errors = mUserParser.getErrors();


                        MessageDialog.newDialog(ProfileActivity.this).onCancelClick(new View.OnClickListener() {
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

                    MessageDialog.newDialog(ProfileActivity.this).onCancelClick(new View.OnClickListener() {
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

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("password",password.getText().toString().trim());
                params.put("username",login.getText().toString().trim());
                params.put("email", email.getText().toString().trim());
                params.put("oldUsername", oldUsername);

                params.put("name", name.getText().toString());
                params.put("phone", codeCountryString+"-"+phone.getText().toString().trim());

                params.put("user_id", userId);

                params.put("image", loadedImageId);
                params.put("lat", String.valueOf(lat));
                params.put("lng", String.valueOf(lng));


                params.put("token",Utils.getToken(getBaseContext()));
                params.put("mac_adr",  ServiceHandler.getMacAddr()  );
                params.put("auth_type","mobile");

                return params;

            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);



    }


    private void checkAndLoad(){

        doSave();

    }

    private void uploadImage(){

        final int uid = mUser.getId();

      final ProgressDialog pdialog = new ProgressDialog(ProfileActivity.this);

            pdialog.setMessage("Uploading image ...");
            pdialog.setCancelable(false);

            if(!pdialog.isShowing())
                pdialog.show();


            SimpleRequest request = new SimpleRequest(Request.Method.POST,
                    Constances.API.API_USER_UPLOAD64, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    pdialog.dismiss();

                    try {


                        if(AppConfig.APP_DEBUG)
                            Log.e("uploadImage", response);

                        JSONObject js = new JSONObject(response);

                        UserParser mUserParser = new UserParser(js);
                        int success = Integer.parseInt(mUserParser.getStringAttr(Tags.SUCCESS));
                        if(success==1) {

                            final List<User> list = mUserParser.getUser();
                            if (list.size() > 0) {
                                SessionsController.updateSession(list.get(0));
                                doSave();
                            }
                        }


                    } catch (JSONException e) {
                        if(AppConfig.APP_DEBUG){
                            Log.e("uploadImage", response);
                            e.printStackTrace();
                        }

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(APP_DEBUG) {
                        Log.e("ERROR", error.toString());
                        Toast.makeText(ProfileActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                    pdialog.dismiss();

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


                        if(AppConfig.APP_DEBUG)
                            Log.e("__",params.toString());

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
                        Log.e("EditProfile", response);
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
                    Toast.makeText(ProfileActivity.this,error.toString(),Toast.LENGTH_LONG).show();
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

        super.onBackPressed();
//        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
//        startActivity(i);
//        overridePendingTransition(R.anim.righttoleft_enter, R.anim.righttoleft_exit);

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

    private Toolbar toolbar;
    private TextView APP_TITLE_VIEW;
    private TextView APP_DESC_VIEW;



    public void initToolbar(){

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setSubtitle("E-shop");
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_36dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        APP_TITLE_VIEW = (TextView) toolbar.findViewById(R.id.toolbar_title);
        APP_DESC_VIEW = (TextView) toolbar.findViewById(R.id.toolbar_description);

        APP_DESC_VIEW.setVisibility(View.GONE);
        Utils.setFont(this, APP_DESC_VIEW, "SourceSansPro-Black.otf");
        Utils.setFont(this, APP_TITLE_VIEW , "SourceSansPro-Black.otf");
        APP_DESC_VIEW.setVisibility(View.GONE);

    }


}
