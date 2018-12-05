package com.mavelinetworks.mavelideals.activities.login.view;


import com.mavelinetworks.mavelideals.activities.firstotppage.model.CountryDetails;
import com.mavelinetworks.mavelideals.classes.UserDetails;

import java.util.List;

/**
 * Created by User on 19-Jul-18.
 */

public interface ILoginView {
    void onClearText();
    void setLoader(int visibility);
    void onLoginResult(Boolean result, int code);
    void onLoginResponse(Boolean result, int code);
    void realMResponse(UserDetails userDetails);
    void sendPParcelableObj(UserDetails userDetails);
    public void getCountryDetailList(List<CountryDetails> countryDetailsList);
}
