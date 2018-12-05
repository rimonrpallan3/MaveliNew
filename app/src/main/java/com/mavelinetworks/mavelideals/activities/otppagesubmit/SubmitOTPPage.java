package com.mavelinetworks.mavelideals.activities.otppagesubmit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.otppagesubmit.presenter.IOTPControler;
import com.mavelinetworks.mavelideals.activities.otppagesubmit.presenter.OTPPresenter;
import com.mavelinetworks.mavelideals.activities.otppagesubmit.view.IOTPView;
import com.mavelinetworks.mavelideals.activities.signuppage.SignupPage;


/**
 * Created by User on 8/30/2017.
 */

 public class SubmitOTPPage extends AppCompatActivity implements IOTPView,View.OnClickListener {

    IOTPControler iotpControler;
    TextView optSecondMsg;
    TextView txtOTPResend;
    EditText edtOPTNo;
    CheckBox checkTermsAndConductionBox;
    Button btnSubmit;
    String country ="";
    String zipCode ="";
    String PhoneNo ="";
    String session_id ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_submit_page);
        optSecondMsg = (TextView) findViewById(R.id.optSecondMsg);
        txtOTPResend = (TextView) findViewById(R.id.txtOTPResend);
        edtOPTNo = (EditText) findViewById(R.id.edtOPTNo);
        checkTermsAndConductionBox = (CheckBox) findViewById(R.id.checkTermsAndConductionBox);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        txtOTPResend.setOnClickListener(this);

        //iotpControler.setOPTSecondMsg(optSecondMsg);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            Intent i = getIntent();
            country = bundle.getString("Country");
            zipCode = bundle.getString("ZipCode");
            PhoneNo = bundle.getString("PhoneNo");
            session_id = bundle.getString("session_id");
           // loginSignUpPage = (LoginSignUpPage)i.getSerializableExtra("LoginSignUpPage");
        }
        iotpControler = new OTPPresenter(this,this,PhoneNo);

    }

    @Override
    public void onSubmit(Boolean result, int code) {
        edtOPTNo.setEnabled(true);
        btnSubmit.setEnabled(true);
        txtOTPResend.setEnabled(true);
        if (result) {
            /*Intent intent = new Intent(this, SignupPage.class);
            intent.putExtra("Country",country);
            intent.putExtra("ZipCode",zipCode);
            intent.putExtra("PhoneNo",PhoneNo);
           // intent.putExtra("LoginSignUpPage", (Serializable) loginSignUpPage);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            setResult(Activity.RESULT_OK, intent);
            startActivity(intent);
            finish();*/
        } else {
            btnSubmit.setEnabled(true);
            switch (code) {
                case -1:
                    Toast.makeText(this, "Please type in OTP, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(this, "No Session ID, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -77:
                    Toast.makeText(this, "Something went Wrong in our Side please try again later , code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Please type correct OTP, code = " + code, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSubmitVerify() {
        Intent intent = new Intent(this, SignupPage.class);
        intent.putExtra("Country",country);
        intent.putExtra("ZipCode",zipCode);
        intent.putExtra("PhoneNo",PhoneNo);
        // intent.putExtra("LoginSignUpPage", (Serializable) loginSignUpPage);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        setResult(Activity.RESULT_OK, intent);
        startActivity(intent);
        finish();
    }

    @Override
    public void resendOtp(Boolean result, int code, String session_id) {
        this.session_id = session_id;
        txtOTPResend.setEnabled(true);
        if (result) {

        } else {
            txtOTPResend.setEnabled(true);
            switch (code) {

                case -78:
                    Toast.makeText(this, "Something went Wrong in our Side please try again later , code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Please try Again Later, code = " + code, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void resendOtpRespone(String session_id) {
        this.session_id = session_id;
        txtOTPResend.setEnabled(true);
    }

    @Override
    public void moveToTermsAndConductionPage() {
        //Intent intent = new Intent(SubmitOTPPage.this, TermsAndConduction.class);
        //startActivity(intent);
    }

    public void btnSubmit(View v){
        edtOPTNo.setEnabled(false);
        btnSubmit.setEnabled(false);
        txtOTPResend.setEnabled(false);
        iotpControler.doOTPValidationAndCheck(edtOPTNo.getText().toString(),session_id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtOTPResend:
                Toast.makeText(this, "Please wait for this feather.", Toast.LENGTH_LONG).show();
                txtOTPResend.setEnabled(false);
                iotpControler.resendOtpPage(PhoneNo);
                break;

        }
    }
}
