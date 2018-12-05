package com.mavelinetworks.mavelideals.activities.loginsignuppage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.activities.firstotppage.FirstOTPPage;
import com.mavelinetworks.mavelideals.activities.login.LoginPage;
import com.mavelinetworks.mavelideals.classes.UserDetails;
import com.mavelinetworks.mavelideals.classes.UserRow;
import com.mavelinetworks.mavelideals.common.Helper;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.mavelinetworks.mavelideals.common.Helper.RC_LOCATION_PERM_SIGIN;
import static com.mavelinetworks.mavelideals.common.Helper.RC_LOCATION_PERM_SIGUP;


public class LoginSignUpPage extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    String phoneNo="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_page);
        Intent intent = getIntent();
        String logout = intent.getStringExtra("logout");
        sharedPrefs = getSharedPreferences(Helper.UserDetails,
                Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        if(logout!=null){
            editor.clear();
            editor.commit();
            System.out.println("LoginSignUpPage has ben called ");
        }

    }

    public void btnSignIn(View v){
        Intent intent = new Intent(this, LoginPage.class);
        startActivityForResult(intent, Helper.REQUEST_LOGEDIN);
       /* if (EasyPermissions.hasPermissions(this, Helper.PERMISSIONS_LOCATION_COARSE,Helper.PERMISSIONS_LOCATION_FINE)) {
            Intent intent = new Intent(this, LoginPage.class);
            startActivityForResult(intent, Helper.REQUEST_LOGEDIN);
            System.out.println("btnSignIn has ben called ");
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.permission_location_check),
                    RC_LOCATION_PERM_SIGIN, Helper.PERMISSIONS_LOCATION_COARSE,Helper.PERMISSIONS_LOCATION_FINE);
        }*/
    }

    public  void btnSignUp(View v){
        /*if (EasyPermissions.hasPermissions(this, Helper.PERMISSIONS_LOCATION_COARSE,Helper.PERMISSIONS_LOCATION_FINE)) {
            Intent intent = new Intent(this, FirstOTPPage.class);
            startActivityForResult(intent,Helper.REQUEST_REGISTERED);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.permission_location_check),
                    RC_LOCATION_PERM_SIGUP, Helper.PERMISSIONS_LOCATION_COARSE,Helper.PERMISSIONS_LOCATION_FINE);
        }*/
        Intent intent = new Intent(this, FirstOTPPage.class);
        startActivityForResult(intent,Helper.REQUEST_REGISTERED);

    }

    public  void btnHiddenBtn(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("btnHiddenBtn", "Done");
        startActivity(intent);
    }

    public String getUserGsonInSharedPrefrences(){
        String phoneNo ="";
        Gson gson = new Gson();
        String json = sharedPrefs.getString("UserDetails", null);
        if(json!=null){
            UserDetails userDetails = gson.fromJson(json, UserDetails.class);
            UserRow userRow = userDetails.getUserRow();
            phoneNo = userRow.getMobile();
            System.out.println("--------- SplashPresenter getUserGsonInSharedPrefrences"+json);
        }
        return phoneNo;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Helper.REQUEST_LOGEDIN) {
            try{
                if(data!=null) {
                    String LoginDone = (String) data.getExtras().getString("LoginDone");
                    if (LoginDone != null) {
                        System.out.println("onActivityResult REQUEST_LOGEDIN has ben called ");
                        finish();
                    }
                }else {
                    phoneNo = getUserGsonInSharedPrefrences();
                    if(phoneNo!=null&&phoneNo.length()>0){
                       finish();
                    }else{
                        System.out.println("LoginSignUpPage  onActivityResult null ");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }else if (requestCode == Helper.REQUEST_REGISTERED){
            System.out.println("onActivityResult REQUEST_REGISTERED has ben called ");
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // EasyPermissions handles the request result.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch (requestCode) {
            case RC_LOCATION_PERM_SIGIN:
                Intent intent = new Intent(this, LoginPage.class);
                startActivityForResult(intent, Helper.REQUEST_LOGEDIN);
                System.out.println("btnSignIn has ben called ");
                break;
            case RC_LOCATION_PERM_SIGUP:
                intent = new Intent(this, FirstOTPPage.class);
                startActivityForResult(intent,Helper.REQUEST_LOGEDIN);
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

}
