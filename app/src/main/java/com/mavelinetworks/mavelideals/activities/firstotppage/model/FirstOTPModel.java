package com.mavelinetworks.mavelideals.activities.firstotppage.model;


/**
 * Created by User on 15-Sep-17.
 */

public class FirstOTPModel implements IFirstOTPModel {
    String contry;
    String contryCode;
    String phno;
    int success = 0;
    String session_id;

    public FirstOTPModel(){

    }


    public FirstOTPModel(String contry, String zipCode, String phno) {
        this.contry = contry;
        this.contryCode = zipCode;
        this.phno = phno;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getContry() {
        return contry;
    }

    public void setContry(String contry) {
        this.contry = contry;
    }

    public String getContryCode() {
        return contryCode;
    }

    public void setContryCode(String contryCode) {
        this.contryCode = contryCode;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
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
    public int validateFirstOTPpage(String contry, String zipCode, String phno) {
        if (contry.trim().length()==0||zipCode.trim().length()==0||phno.trim().length()==0){
            {
                return -1;
            }
        }else {
            for (int i = 0; i < contry.trim().length(); i++) {
                char charAt2 = contry.trim().charAt(i);
                if (!Character.isLetter(charAt2)) {
                    return -2;
                }
            }
            for (int i = 0; i < zipCode.trim().length(); i++) {
                String charAt2 = zipCode.trim().toString();
                if (charAt2==null) {
                    return -3;
                }
            }
            for (int i = 0; i < phno.trim().length(); i++) {
                char charAt2 = phno.trim().charAt(i);
                if (!Character.isDigit(charAt2)) {
                    return -4;
                }else if(phno.trim().length()<4){
                    return -5;
                }else if(phno.trim().length()>11){
                    return -6;
                }
            }

        }
        return 0;
    }
}