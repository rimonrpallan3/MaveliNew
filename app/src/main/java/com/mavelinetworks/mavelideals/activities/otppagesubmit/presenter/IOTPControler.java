package com.mavelinetworks.mavelideals.activities.otppagesubmit.presenter;

import android.widget.TextView;

/**
 * Created by User on 8/30/2017.
 */

public interface IOTPControler {
    void resendOtpPage(String PhoneNo);
    void setOPTSecondMsg(TextView v);
    void doOTPValidationAndCheck(String edtOPTNo, String session_id);
}
