package com.mavelinetworks.mavelideals.activities.login.presenter;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by User on 19-Jul-18.
 */

public interface ILoginPresenter {
    void clear();
    void doLogin(String countryCode, String emailPhno, String passwd);
    void onLoginSucuess();
    void firebaseAuthWithGoogle(GoogleSignInAccount acct);
    void updateUI(FirebaseUser user);
}
