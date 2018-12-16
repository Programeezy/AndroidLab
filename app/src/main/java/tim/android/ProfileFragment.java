package tim.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import tim.android.Util.ImageUtil;

public class ProfileFragment extends Fragment {

    private TextView nameView;
    private TextView surnameView;
    private TextView phoneNumberView;
    private TextView emailView;
    private ImageView avatarView;

    private View.OnClickListener editProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Navigation.findNavController(view).navigate(R.id.editProfileFragment);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.profile_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameView = view.findViewById(R.id.nameView);
        surnameView = view.findViewById(R.id.surnameView);
        phoneNumberView = view.findViewById(R.id.phoneNumberView);
        emailView = view.findViewById(R.id.emailView);
        avatarView = view.findViewById(R.id.avatarView);

        FloatingActionButton editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(editProfileListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        fillProfileData();
    }

    private void fillProfileData() {
        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        String name = sharedPref.getString(getString(R.string.name_field_key), "");
        String surname = sharedPref.getString(getString(R.string.surname_field_key), "");
        String phone = sharedPref.getString(getString(R.string.phone_field_key), "");
        String email = sharedPref.getString(getString(R.string.email_field_key), "");
        String avatar = sharedPref.getString(getString(R.string.avatar_field_key), ImageUtil.DEFAULT_IMAGE_PATH);

        nameView.setText(name);
        surnameView.setText(surname);
        phoneNumberView.setText(phone);
        emailView.setText(email);

        ImageUtil.loadImage(getContext(), avatar, avatarView);
    }
}
