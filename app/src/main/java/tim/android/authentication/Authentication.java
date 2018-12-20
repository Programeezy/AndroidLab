package tim.android.authentication;

import android.os.AsyncTask;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import tim.android.App;
import tim.android.user.User;
import tim.android.user.UserRepository;

public class Authentication {
    
    private static User currentUser;
    
    private static UserRepository userRepository = new UserRepository(App.getApp());
    
    private static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isAuthenticated() {
        return currentUser != null;
    }
    
    public static void signIn(tim.android.authentication.SignInResultListener signInResultListener,
                              String login,
                              String password,
                              SignInResultListener mainListener) {
        new signInAsyncTask(signInResultListener, userRepository, mainListener).execute(login, password);
    }
    
    public static void signUp(SignUpResultListener signUpResultListener, String login, String password) {
        new signUpAsyncTask(signUpResultListener, userRepository).execute(login, password);
    }
    
    public static void logOut() {
        currentUser = null;
    }
    
    private static class signInAsyncTask extends AsyncTask<String, Void, User> {
        
        private UserRepository userRepository;
        
        private SignInResultListener resultListener;

        private SignInResultListener mainActivityListener;

        signInAsyncTask(SignInResultListener signInResultListener, UserRepository repository, SignInResultListener mainListener) {
            userRepository = repository;
            resultListener = signInResultListener;
            mainActivityListener = mainListener;
        }

        @Override
        protected User doInBackground(String... params) {
            return userRepository.getUser(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(User user) {
            if (user == null) {
                resultListener.onSignInFail();
            }
            else {
                setCurrentUser(user);
                resultListener.onSignInComplete(user);
                mainActivityListener.onSignInComplete(user);
            }
        }
    }

    private static class signUpAsyncTask extends AsyncTask<String, Void, User> {

        private UserRepository userRepository;

        private SignUpResultListener resultListener;

        signUpAsyncTask(SignUpResultListener signUpResultListener, UserRepository repository) {
            userRepository = repository;
            resultListener = signUpResultListener;
        }

        @Override
        protected User doInBackground(String... params) {
            User user = userRepository.getUser(params[0]);
            if (user == null) {
                user = new User(params[0], params[1]);
                userRepository.insertUser(user);
            }
            else {
                user = null;
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user == null) {
                resultListener.onUserExists();
            }
            else {
                resultListener.onSignUpSuccess(user);
            }
        }
    }
}
