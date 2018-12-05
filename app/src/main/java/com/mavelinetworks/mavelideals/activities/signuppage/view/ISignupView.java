package com.mavelinetworks.mavelideals.activities.signuppage.view;


import com.mavelinetworks.mavelideals.classes.UserDetails;

/**
 * Created by User on 20-Jul-18.
 */

public interface ISignupView {
    public void moveToTermsAndConductionPage();
    void onRegister(Boolean result, int code);
    void onRegistered(Boolean result, int code);
    void sendPParcelableObj(UserDetails userDetails);
    void realMResponse(UserDetails userDetails);
    void resetButton(Boolean setValue);
}
