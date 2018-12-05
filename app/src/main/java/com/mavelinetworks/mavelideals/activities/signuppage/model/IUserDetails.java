package com.mavelinetworks.mavelideals.activities.signuppage.model;

/**
 * Created by User on 23-Jul-18.
 */

public interface IUserDetails {
    int checkUserValidity(String name, String passwd);
    int validateUserDetails(String FullName, String email, String mobNo, String pswd, String confirmPswd, Boolean termsAndCondCheck);
    int validateRegisterResponseError(int success);
}
