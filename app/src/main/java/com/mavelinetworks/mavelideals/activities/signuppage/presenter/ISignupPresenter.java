package com.mavelinetworks.mavelideals.activities.signuppage.presenter;

import android.widget.TextView;

/**
 * Created by User on 20-Jul-18.
 */

public interface ISignupPresenter {
    void setTermCondMsg(TextView v);
    void onRegisteredSucuess();
    void doRegister(String FullName, String email, String mobNo, String pswd, String confirmPswd, Boolean termsAndCondCheck);
}
