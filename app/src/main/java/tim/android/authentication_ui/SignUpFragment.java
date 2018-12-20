package tim.android.authentication_ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import tim.android.R;
import tim.android.authentication.Authentication;
import tim.android.authentication.SignUpResultListener;
import tim.android.user.User;
import tim.android.util.KeyboardUtil;


public class SignUpFragment extends Fragment implements SignUpResultListener {
    
    private TextInputEditText inputLogin;
    private TextInputEditText inputPassword;
    
    private TextInputLayout loginLayout;
    private TextInputLayout passwordLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        inputLogin = view.findViewById(R.id.register_input_login);
        inputPassword = view.findViewById(R.id.register_input_password);
        loginLayout = view.findViewById(R.id.register_login_layout);
        passwordLayout = view.findViewById(R.id.register_password_layout);

        Button registerButton = view.findViewById(R.id.btn_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtil.hideKeyboard(requireActivity());
                
                loginLayout.setError(null);
                passwordLayout.setError(null);
                if (TextUtils.isEmpty(inputLogin.getText())) {
                    loginLayout.setError(getString(R.string.empty_login_error));
                }
                if (TextUtils.isEmpty(inputPassword.getText())) {
                    passwordLayout.setError(getString(R.string.empty_password_error));
                }
                else {
                    String password = inputPassword.getText().toString();
                    Authentication.signUp(SignUpFragment.this, inputLogin.getText().toString(), Authentication.md5(password));
                }
            }
        });
    }

    @Override
    public void onSignUpSuccess(User user) {
        Navigation.findNavController(getView()).navigate(R.id.action_signUpFragment_to_signInFragment);
        Snackbar.make(getView(), getString(R.string.registration_complete_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onUserExists() {
        loginLayout.setError(getString(R.string.user_exist_error));
    }

    @Override
    public void onSignUpFail() {

    }

}
