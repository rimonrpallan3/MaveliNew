package com.mavelinetworks.mavelideals.activities.otppagesubmit.view;

/**
 * Created by User on 8/30/2017.
 */

public interface IOTPView {
    void onSubmit(Boolean result, int code);
    void onSubmitVerify();
    void resendOtp(Boolean result, int code, String session_id);
    void resendOtpRespone(String session_id);
    void moveToTermsAndConductionPage();

}
