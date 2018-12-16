package tim.android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class MainActivity extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener {

    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
    private DrawerLayout mDrawerLayout;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.draw_layout);
        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        selectDrawerItem(menuItem);

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TextView imeiView = findViewById(R.id.textView);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                Toast.makeText(getApplicationContext(), "Give permissions to show IMEI", Toast.LENGTH_LONG).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_PERMISSION_PHONE_STATE);

            }
        } else {
            imeiView.setText("Already"); //TelephonyManager.getDeviceId()
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Создать новый фрагмент и задать фрагмент для отображения
        // на основе нажатия на элемент навигации
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_profile:
                navController.navigate(R.id.profileFragment);
                break;
            default:
                fragmentClass = ProfileFragment.class;
        }

        // Выделение существующего элемента выполнено с помощью
        // NavigationView
        menuItem.setChecked(true);
        // Установить заголовок для action bar'а
        setTitle(menuItem.getTitle());
        // Закрыть navigation drawer
        mDrawerLayout.closeDrawers();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TextView imeiView = (TextView) findViewById(R.id.textView);

        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imeiView.setText("Granted!"); //TelephonyManager.getDeviceId()
                } else {
                    imeiView.setText("Denied!");
                }
            }

        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
