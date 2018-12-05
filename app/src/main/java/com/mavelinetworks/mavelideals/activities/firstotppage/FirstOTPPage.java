package com.mavelinetworks.mavelideals.activities.firstotppage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.firstotppage.Adapter.SpinAdapter;
import com.mavelinetworks.mavelideals.activities.firstotppage.model.CountryDetails;
import com.mavelinetworks.mavelideals.activities.firstotppage.presenter.FirstOTPPresenter;
import com.mavelinetworks.mavelideals.activities.firstotppage.view.IFirstOTPView;
import com.mavelinetworks.mavelideals.activities.otppagesubmit.SubmitOTPPage;

import java.util.List;


/**
 * Created by User on 8/30/2017.
 */

 public class   FirstOTPPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener,IFirstOTPView {

    FirstOTPPresenter otpPresenter;
    TextView edtZipCode;
    EditText edtPhNo;
    Button btnGetOtp;
    Spinner spinnerSelectContry;
    //ArrayAdapter<CharSequence> adapter;
    private SpinAdapter adapter;
    String countryName = "";
    Boolean intialPhase= true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_page);
        edtZipCode = (TextView) findViewById(R.id.edtZipCode);
        edtPhNo = (EditText) findViewById(R.id.edtPhNo);
        btnGetOtp = (Button) findViewById(R.id.btnGetOtp);


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            Intent i = getIntent();
          //  loginSignUpPage = (LoginSignUpPage)i.getSerializableExtra("LoginSignUpPage");
        }

        spinnerSelectContry = (Spinner) findViewById(R.id.spinnerSelectCountry);
        spinnerSelectContry.setOnItemSelectedListener(this);

        otpPresenter = new FirstOTPPresenter(this,this);
       // adapter =new SimpleAdapter(this,android.R.layout.simple_spinner_item,)
       /* adapter = ArrayAdapter.createFromResource(this,
                R.array.country_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSelectContry.setAdapter(adapter);
        spinnerSelectContry.setPrompt(getString(R.string.otp_spinner_country));*/

        countryName = "Please Select a Country";
        edtZipCode.setText("");
        spinnerSelectContry.setPrompt(getString(R.string.otp_spinner_country));

    }



    @Override
    public void onItemSelected(AdapterView parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    public void onNothingSelected(AdapterView arg0) {
        // TODO Auto-generated method stub
        spinnerSelectContry.setPrompt(getString(R.string.otp_spinner_country));
       /* arg0.setAdapter(new NothingSelectedSpinnerAdapter(
                adapter,
                R.layout.contact_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this));*/
    }

    public void btnGetOtp(View v){
        btnGetOtp.setEnabled(false);
        otpPresenter.doGetData(countryName,
                edtZipCode.getText().toString(),
                edtPhNo.getText().toString());

    }

    @Override
    public void validatedSendData(Boolean result, int code, String session_id) {
        edtZipCode.setEnabled(true);
        edtPhNo.setEnabled(true);
        btnGetOtp.setEnabled(true);
        if (result) {
            Intent intent = new Intent(FirstOTPPage.this, SubmitOTPPage.class);
            intent.putExtra("Country",spinnerSelectContry.getSelectedItem().toString());
            intent.putExtra("ZipCode",edtZipCode.getText().toString());
            intent.putExtra("PhoneNo",edtPhNo.getText().toString());
            intent.putExtra("session_id",session_id);
            //intent.putExtra("LoginSignUpPage", (Serializable) loginSignUpPage);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            setResult(Activity.RESULT_OK, intent);
            startActivity(intent);
            finish();
        } else {
            btnGetOtp.setEnabled(true);
            switch (code) {
                case -1:
                    Toast.makeText(this, "Please fill all the fields, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(this, "Please fill a valid Country Name, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -3:
                    Toast.makeText(this, "Please fill a valid Zip Code, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -4:
                    Toast.makeText(this, "Please fill a valid Phone No, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -5:
                    Toast.makeText(this, "Phone number should be more that 4 digit, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -6:
                    Toast.makeText(this, "Phone number should be less that 11 digit, code = " + code, Toast.LENGTH_SHORT).show();
                    break;
                case -77:
                    Toast.makeText(this, "Something went Wrong in our Side please try again later , code = " + code, Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(this, "Please fill the correct OTP, ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void validatedRespone(String session_id) {
        Intent intent = new Intent(FirstOTPPage.this, SubmitOTPPage.class);
        intent.putExtra("Country",spinnerSelectContry.getSelectedItem().toString());
        intent.putExtra("ZipCode",edtZipCode.getText().toString());
        intent.putExtra("PhoneNo",edtPhNo.getText().toString());
        intent.putExtra("session_id",session_id);
        //intent.putExtra("LoginSignUpPage", (Serializable) loginSignUpPage);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        setResult(Activity.RESULT_OK, intent);
        startActivity(intent);
        finish();
    }

    @Override
    public void getCountryDetailList(List<CountryDetails> countryDetailsList) {
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter =new SpinAdapter(this,android.R.layout.simple_spinner_item,countryDetailsList);
        spinnerSelectContry.setAdapter(adapter); // Set the custom adapter to the spinner
        spinnerSelectContry.setPrompt(getString(R.string.otp_spinner_country));
        // You can create an anonymous listener to handle the event when is selected an spinner item
        spinnerSelectContry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                CountryDetails countryDetails = adapter.getItem(position);
                if(intialPhase){
                    intialPhase = false;
                    countryName = "Please Select a Country";
                    edtZipCode.setText("");
                    spinnerSelectContry.setPrompt(getString(R.string.otp_spinner_country));
                }else {
                    System.out.println("ID: " + countryDetails.getCode() + "\nName: " + countryDetails.getName());
                    countryName = countryDetails.getName();
                    edtZipCode.setText(countryDetails.getDial_code());
                }
                // Here you can do the action you want to...
                //Toast.makeText(this, "ID: " + countryDetails.getCode() + "\nName: " + countryDetails.getName(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
                countryName = "Please Select a Country";
                edtZipCode.setText("");
            }

        });

    }
}
