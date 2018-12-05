package com.mavelinetworks.mavelideals.activities.otppagesubmit.model;

/**
 * Created by User on 15-Sep-17.
 */

public interface IOTPModel {
    int validatesSessionKeyAndOtp(String optNumber, String sessionKey);
    int validatePhoneNo(String phno);
    int validateRegisterResponseError(int success);
    int validateSessionOtpResponse(int success);
}
