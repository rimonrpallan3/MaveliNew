package com.mavelinetworks.mavelideals.activities.signuppage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.activities.signuppage.presenter.ISignupPresenter;
import com.mavelinetworks.mavelideals.activities.signuppage.presenter.SignupPresenter;
import com.mavelinetworks.mavelideals.activities.signuppage.view.ISignupView;
import com.mavelinetworks.mavelideals.classes.UserDetails;
import com.mavelinetworks.mavelideals.common.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mavelinetworks.mavelideals.common.Helper.REQUEST_REGISTERED;


/**
 * Created by User on 19-Jul-18.
 */

public class SignupPage extends AppCompatActivity implements View.OnClickListener,ISignupView {

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    @BindView(R.id.ibClose)
    ImageButton ibClose;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.tvMobileNo)
    TextView tvMobileNo;
    @BindView(R.id.etPswd)
    EditText etPswd;
    @BindView(R.id.etConfirmPswd)
    EditText etConfirmPswd;
    @BindView(R.id.checkTermsAndConductionBox)
    CheckBox checkTermsAndConductionBox;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.tvTermsAndCond)
    TextView tvTermsAndCond;
    ISignupPresenter iSignupPresenter;
    String country ="";
    String zipCode ="";
    String PhoneNo ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        ButterKnife.bind(this);
        sharedPrefs = getSharedPreferences(Helper.UserDetails,
                Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        btnRegister.setOnClickListener(this);
        ibClose.setOnClickListener(this);
        //init
        sharedPrefs = getSharedPreferences(Helper.UserDetails,
                Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            country = bundle.getString("Country");
            zipCode = bundle.getString("ZipCode");
            PhoneNo = bundle.getString("PhoneNo");
            tvMobileNo.setText(zipCode + PhoneNo);
        }


        iSignupPresenter = new SignupPresenter(this,this,sharedPrefs,editor);
        iSignupPresenter.setTermCondMsg(tvTermsAndCond);
        //find view


        //init

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                //Toast.makeText(this, "Please wait for this feather.", Toast.LENGTH_LONG).show();
                btnRegister.setEnabled(false);
                iSignupPresenter.doRegister(etName.getText().toString(),
                        etEmail.getText().toString(),
                        tvMobileNo.getText().toString(),
                        etPswd.getText().toString(),
                        etConfirmPswd.getText().toString(),
                        checkTermsAndConductionBox.isChecked());
                System.out.println("----------------- etName : "+etName.getText().toString()+" etEmail : "+etEmail.getText().toString()
                +" tvMobileNo : "+tvMobileNo.getText().toString()+" etPswd : "+etPswd.getText().toString()+
                        " etConfirmPswd : "+etConfirmPswd.getText().toString()+" checkTermsAndConductionBox : "+checkTermsAndConductionBox.isChecked());
                break;
            case R.id.ibClose:
                finish();
                break;

        }
    }

    @Override
    public void moveToTermsAndConductionPage() {
        Toast.makeText(this, "Document is on processing, so please continue to use our service with out any rules.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRegister(Boolean result, int code) {
        etName.setEnabled(true);
        etEmail.setEnabled(true);
        tvMobileNo.setEnabled(true);
        etPswd.setEnabled(true);
        etConfirmPswd.setEnabled(true);
        //edtCPR.setEnabled(true);
        if (result) {
        } else {
            btnRegister.setEnabled(true);
            switch (code) {
                case -1:
                    Toast.makeText(this, "Please fill all the fields, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(this, "Please fill a valid Full Name, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -3:
                    Toast.makeText(this, "Please fill a valid Password, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -4:
                    Toast.makeText(this, "Please type the Same Password, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -5:
                    Toast.makeText(this, "Please fill a valid email Address, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -6:
                    Toast.makeText(this, "Please fill a valid Phone No, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -8:
                    Toast.makeText(this, "Please Approve the terms and conduction, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Please try Again Later, code = " + code, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRegistered(Boolean result, int code) {
        System.out.println("-----onRegistered second Please see, code = " + code + ", result: " + result);
        if (result) {
            System.out.println("------- inside onRegistered first Please see, code = " + code + ", result: " + result);
            //Toast.makeText(this, "-----onRegistered second Please see, code = " + code + ", result: " + result, Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //re-enable the button
                    btnRegister.setEnabled(true);
                }
            }, 4000);
            iSignupPresenter.onRegisteredSucuess();
        } else {
            etName.setEnabled(true);
            etEmail.setEnabled(true);
            tvMobileNo.setEnabled(true);
            etPswd.setEnabled(true);
            etConfirmPswd.setEnabled(true);
            btnRegister.setEnabled(true);
            switch (code) {
                case -9:
                    Toast.makeText(this, "Please Correct the Required fields, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -77:
                    Toast.makeText(this, "SomeThing went Wrong on our end Please try after some time , code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Please try Again Later, code = " + code, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void sendPParcelableObj(UserDetails userDetails) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("UserDetails", userDetails);
        intent.putExtra("LoginDone", "done");
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        setResult(REQUEST_REGISTERED);
        finish();
    }

    @Override
    public void realMResponse(UserDetails userDetails) {
        etName.setEnabled(true);
        etEmail.setEnabled(true);
        tvMobileNo.setEnabled(true);
        etPswd.setEnabled(true);
        etConfirmPswd.setEnabled(true);
        btnRegister.setEnabled(true);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("UserDetails", userDetails);
        intent.putExtra("LoginDone", "done");
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        setResult(REQUEST_REGISTERED);
        finish();
    }

    @Override
    public void resetButton(Boolean setValue) {
        etName.setEnabled(setValue);
        etEmail.setEnabled(setValue);
        tvMobileNo.setEnabled(setValue);
        etPswd.setEnabled(setValue);
        etConfirmPswd.setEnabled(setValue);
        btnRegister.setEnabled(setValue);
    }
}
