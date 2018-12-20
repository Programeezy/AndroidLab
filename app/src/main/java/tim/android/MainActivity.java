package tim.android;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import de.hdodenhof.circleimageview.CircleImageView;
import tim.android.authentication.Authentication;
import tim.android.authentication.SignInResultListener;
import tim.android.rss.FeedCacheManager;
import tim.android.user.User;
import tim.android.util.ImageUtil;

public class MainActivity extends AppCompatActivity implements SignInResultListener, ToolbarTitleListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem profileFragment;
    private MenuItem rssFragment;
    private MenuItem emptyFragment;
    private boolean isSignedIn;

    public interface BackPressedListener {
        void onBackPressed();
    }

    @Override
    public void updateTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        profileFragment = menu.findItem(R.id.profileFragment);
        rssFragment = menu.findItem(R.id.rssFeedFragment);
        emptyFragment = menu.findItem(R.id.secondEmptyFragment);

        if (Authentication.isAuthenticated()) {
            setupMenuItems(true);
        }

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setupDrawerHeader();
            }
        });

        setupNavigation();
    }

    @Override
    public void onSignInComplete(User user) {
        setupMenuItems(true);
        isSignedIn = true;
    }

    @Override
    public void onSignInFail() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(drawerLayout, Navigation.findNavController(this, R.id.nav_host_fragment));
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment))
                .getChildFragmentManager().getFragments().get(0);
        switch (Objects.requireNonNull(Navigation.findNavController(this, R.id.nav_host_fragment)
                .getCurrentDestination()).getId()) {
            case R.id.editProfileFragment:
                BackPressedListener backPressedListener = (BackPressedListener) fragment; 
                backPressedListener.onBackPressed();
                return;
        }
        super.onBackPressed();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupNavigation() {
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        if (!Authentication.isAuthenticated()) {
                            Snackbar.make(findViewById(android.R.id.content), "Sign in, please", Snackbar.LENGTH_SHORT).show();
                            return false;
                        } else {
                            menuItem.setChecked(true);
                            switch (menuItem.getItemId()) {
                                case R.id.profileFragment:
                                    navController.navigate(R.id.profileFragment);
                                    break;
                                case R.id.rssFeedFragment:
                                    navController.navigate(R.id.rssFeedFragment);
                                    break;
                                case R.id.secondEmptyFragment:
                                    navController.navigate(R.id.secondEmptyFragment);
                                    break;
                            }
                            drawerLayout.closeDrawers();
                            return true;
                        }
                    }
                });

        //NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setupDrawerHeader() {
        User currentUser = Authentication.getCurrentUser();
        if (currentUser != null) {
            setupUserDrawerHeader(currentUser);
        }
        else {
            setupNoUserDrawerHeader();
        }
        
    }
    
    private void setupNoUserDrawerHeader() {
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        navigationView.inflateHeaderView(R.layout.no_user_drawer_header);

        Button signInButton = navigationView.getHeaderView(0).findViewById(R.id.sign_in);
        Button signUpButton = navigationView.getHeaderView(0).findViewById(R.id.sign_up);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(R.id.signInFragment);
                drawerLayout.closeDrawers();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(R.id.signUpFragment);
                drawerLayout.closeDrawers();
            }
        });
    }

    private void setupMenuItems(boolean state)
    {
        emptyFragment.setVisible(state);
        profileFragment.setVisible(state);
        rssFragment.setVisible(state);
        emptyFragment.setEnabled(state);
        profileFragment.setEnabled(state);
        rssFragment.setEnabled(state);
        drawerLayout.closeDrawers();
    }
    private void setupUserDrawerHeader(User currentUser) {
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        navigationView.inflateHeaderView(R.layout.drawer_header);

        TextView headerLoginView = navigationView.getHeaderView(0).findViewById(R.id.headerNameView);
        TextView headerEmailView = navigationView.getHeaderView(0).findViewById(R.id.headerEmailView);
        CircleImageView headerCircleView = navigationView.getHeaderView(0).findViewById(R.id.headerCircleView);
        Button logOutButton = navigationView.getHeaderView(0).findViewById(R.id.log_out);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Authentication.logOut();
                FeedCacheManager.clearCache(MainActivity.this);
                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(R.id.startFragment);
                Snackbar.make(v, "You have logged out succesfully", Snackbar.LENGTH_LONG).show();
                setupMenuItems(false);
                isSignedIn = false;
                drawerLayout.closeDrawers();
            }
        });

        String login = currentUser.getLogin();
        String email = currentUser.getEmail();
        String avatar = currentUser.getPicture();

        headerLoginView.setText(login);
        headerEmailView.setText(email);

        ImageUtil.loadImage(this, avatar, headerCircleView);
    }
}
