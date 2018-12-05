package com.mavelinetworks.mavelideals.activities.otppagesubmit.model;


import com.mavelinetworks.mavelideals.classes.Errors;

/**
 * Created by User on 15-Sep-17.
 */

public class OTPModel implements IOTPModel {
    String optNo;
    Boolean checkTermsAndConductionBox;
    int success = 0;
    String session_id = "";
    Errors errors;

    public OTPModel(String optNo, Boolean checkTermsAndConductionBox) {
        this.optNo = optNo;
        this.checkTermsAndConductionBox = checkTermsAndConductionBox;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public int getSuccess() {
        return success;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getOptNo() {
        return optNo;
    }

    public void setOptNo(String optNo) {
        this.optNo = optNo;
    }

    public Boolean getCheckTermsAndConductionBox() {
        return checkTermsAndConductionBox;
    }

    public void setCheckTermsAndConductionBox(Boolean checkTermsAndConductionBox) {
        this.checkTermsAndConductionBox = checkTermsAndConductionBox;
    }

    @Override
    public int validateRegisterResponseError(int success) {
        if(success==1){
            //if there is no error message then it means that data response is correct.
            return -9;
        }
        return 0;
    }

    @Override
    public int validatesSessionKeyAndOtp(String optNumber, String sessionKey) {
        if (optNumber.trim().length()==0){
            return -1;
        }
        if(sessionKey.trim().length()==0){
            return -2;
        }
        return 0;
    }
    @Override
    public int validatePhoneNo(String phno) {
        if (phno.trim().length()==0){
            return -1;
        }
        /*if(checkTermsAndConductionBox==false){
            return -2;
        }*/
        return 0;
    }

    @Override
    public int validateSessionOtpResponse(int success) {
        if(success==1){
            //if there is no error message then it means that data response is correct.
            return -9;
        }
        return 0;
    }
}