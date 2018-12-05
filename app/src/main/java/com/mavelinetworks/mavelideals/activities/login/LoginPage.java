package com.mavelinetworks.mavelideals.activities.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.activities.firstotppage.Adapter.SpinAdapter;
import com.mavelinetworks.mavelideals.activities.firstotppage.model.CountryDetails;
import com.mavelinetworks.mavelideals.activities.login.presenter.ILoginPresenter;
import com.mavelinetworks.mavelideals.activities.login.presenter.LoginPresenter;
import com.mavelinetworks.mavelideals.activities.login.view.ILoginView;
import com.mavelinetworks.mavelideals.activities.signuppage.SignupPage;
import com.mavelinetworks.mavelideals.classes.UserDetails;
import com.mavelinetworks.mavelideals.common.Helper;
import com.mavelinetworks.mavelideals.common.NetworkDetector;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 19-Jul-18.
 */

public class LoginPage extends AppCompatActivity implements ILoginView,View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,AdapterView.OnItemSelectedListener{

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    ILoginPresenter iLoginPresenter;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @BindView(R.id.tvSkip)
    TextView tvSkip;
    @BindView(R.id.edtZipCodeLogin)
    TextView edtZipCodeLogin;
    @BindView(R.id.etRegPhoneNo)
    EditText etRegPhoneNo;
    @BindView(R.id.etPswd)
    EditText etPswd;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.flGoogleSignBtn)
    FrameLayout flGoogleSignBtn;
    @BindView(R.id.flLoadingLayout)
    FrameLayout flLoadingLayout;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginPage";
    private FirebaseAuth.AuthStateListener mAuthListener;
    Spinner spinnerSelectContry;
    //ArrayAdapter<CharSequence> adapter;
    private SpinAdapter adapter;
    String countryName = "";
    Boolean intialPhase= true;


    TextView tvSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_page);
        ButterKnife.bind(this);
        sharedPrefs = getSharedPreferences(Helper.UserDetails,
                Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        tvSignup = (TextView) findViewById(R.id.tvSignup);
        spinnerSelectContry = (Spinner) findViewById(R.id.spinnerSelectCountry);
        spinnerSelectContry.setOnItemSelectedListener(this);
        tvSkip.setOnClickListener(this);
        tvSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        flGoogleSignBtn.setOnClickListener(this);
        flLoadingLayout.setOnClickListener(this);
        //find view
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mAuth = FirebaseAuth.getInstance();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //init
        iLoginPresenter = new LoginPresenter(this,sharedPrefs,editor,this,mGoogleSignInClient,mAuth);
    }

    @Override
    public void setLoader(int visibility){
        flLoadingLayout.setVisibility(visibility);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUserFireBase = mAuth.getCurrentUser();
        iLoginPresenter.updateUI(currentUserFireBase);
        /*if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }*/
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSkip:
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnLogin:
                Helper.hideKeyboard(this);
                if(NetworkDetector.haveNetworkConnection(this)){
                    //Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.snack_error_network_available), Snackbar.LENGTH_SHORT).show();
                    btnLogin.setEnabled(false);
                    iLoginPresenter.doLogin(edtZipCodeLogin.getText().toString(),etRegPhoneNo.getText().toString(), etPswd.getText().toString());
                }else {
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.snack_error_network), Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.flGoogleSignBtn:
                System.out.println("-----------GoogleSignInAccount flGoogleSignBtn : " );
                //
                if(NetworkDetector.haveNetworkConnection(this)){
                    //Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.snack_error_network_available), Snackbar.LENGTH_SHORT).show();
                    flGoogleSignBtn.setEnabled(false);
                    signIn();
                }else {
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.snack_error_network), Snackbar.LENGTH_LONG).show();

                }
                break;
            case R.id.tvSignup:
                intent = new Intent(LoginPage.this, SignupPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                setResult(Activity.RESULT_OK, intent);
                startActivity(intent);

                break;

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * This method starts the google sign in process.
     * It opens the dialog box for choosing google account.
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                iLoginPresenter.firebaseAuthWithGoogle(account);
                System.out.println("-----------GoogleSignInAccount onActivityResult");
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                flGoogleSignBtn.setEnabled(true);
                Log.w(TAG, "Google sign in failed", e);
                System.out.println("-----------GoogleSignInAccount onActivityResult error : " +e.getMessage());
                // ...
            }
        }
    }


    @Override
    public void onClearText() {
        etRegPhoneNo.setText("");
        etPswd.setText("");
    }

    @Override
    public void onLoginResult(Boolean result, int code) {

        etRegPhoneNo.setEnabled(true);
        etPswd.setEnabled(true);
        if (result){
        }
        else {
            Toast.makeText(this, "Please input Values, code = " + code, Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
            iLoginPresenter.clear();
        }
    }

    @Override
    public void onLoginResponse(Boolean result, int code) {
        etRegPhoneNo.setEnabled(true);
        etPswd.setEnabled(true);
        if (result){
            iLoginPresenter.onLoginSucuess();
        }
        else {
            Toast.makeText(this, "Please input correct UserName and Password, code = " + code, Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
            iLoginPresenter.clear();
        }
    }

    @Override
    public void realMResponse(UserDetails userDetails) {
        etRegPhoneNo.setEnabled(true);
        etPswd.setEnabled(true);
        System.out.println("UserDetails ----- FName: "+userDetails.getName());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("LoginDone", "Done");
        setResult(Helper.REQUEST_LOGEDIN,intent);
        intent.putExtra("UserDetails", userDetails);
        startActivity(intent);
        finish();
    }

    @Override
    public void sendPParcelableObj(UserDetails userDetails) {
        System.out.println("UserDetails ----- FName: "+userDetails.getName());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("LoginDone", "Done");
        setResult(Helper.REQUEST_LOGEDIN,intent);
        intent.putExtra("UserDetails", userDetails);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
        spinnerSelectContry.setPrompt(getString(R.string.otp_spinner_country));
       /* arg0.setAdapter(new NothingSelectedSpinnerAdapter(
                adapter,
                R.layout.contact_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this));*/
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
                    edtZipCodeLogin.setText("");
                    spinnerSelectContry.setPrompt(getString(R.string.otp_spinner_country));
                }else {
                    System.out.println("ID: " + countryDetails.getCode() + "\nName: " + countryDetails.getName());
                    countryName = countryDetails.getName();
                    edtZipCodeLogin.setText(countryDetails.getDial_code());
                }
                // Here you can do the action you want to...
                //Toast.makeText(this, "ID: " + countryDetails.getCode() + "\nName: " + countryDetails.getName(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
                countryName = "Please Select a Country";
                edtZipCodeLogin.setText("");
            }

        });

    }
}
