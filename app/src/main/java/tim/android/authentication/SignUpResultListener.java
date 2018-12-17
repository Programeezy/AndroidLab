package tim.android.authentication;

import tim.android.user.User;

public interface SignUpResultListener {
    void onSignUpSuccess(User user);
    void onUserExists();
    void onSignUpFail();
}
