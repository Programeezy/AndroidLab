package tim.android.authentication;

import tim.android.user.User;

public interface SignInResultListener {
    void onSignInComplete(User user);
    void onSignInFail();
}
