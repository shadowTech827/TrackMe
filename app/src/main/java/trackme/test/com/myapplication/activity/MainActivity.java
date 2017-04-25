package trackme.test.com.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import trackme.test.com.myapplication.R;
import trackme.test.com.myapplication.datamanager.network.DataManager;
import trackme.test.com.myapplication.fragments.FlickrPhotoFragment;
import trackme.test.com.myapplication.fragments.TrackMeFragment;
import trackme.test.com.myapplication.fragments.TrackMeHistory;
import trackme.test.com.myapplication.services.LocationHelper;
import trackme.test.com.myapplication.util.PreferencesHelper;
import trackme.test.com.myapplication.util.UtilityMethods;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private static final int PERMISSION_LOCATION_COARSE = 101;
    private static final int PERMISSION_LOCATION_FINE = 102;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private DataManager dataManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_track_me:
                    TrackMeFragment trackMeFragment = new TrackMeFragment();
                    fragmentManager = getSupportFragmentManager();
                    transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.main_container, trackMeFragment, null)
                            .addToBackStack(trackMeFragment.getClass().getName())
                            .commit();
                    return true;
                case R.id.navigation_history:

                    TrackMeHistory trackMeHistory = new TrackMeHistory();
                    fragmentManager = getSupportFragmentManager();
                    transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.main_container, trackMeHistory, null)
                            .addToBackStack(trackMeHistory.getClass().getName())
                            .commit();

                    return true;

                case R.id.flickr_photo:

                    FlickrPhotoFragment flickrPhotoFragment = new FlickrPhotoFragment();
                    fragmentManager = getSupportFragmentManager();
                    transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.main_container, flickrPhotoFragment, null)
                            .addToBackStack(flickrPhotoFragment.getClass().getName())
                            .commit();

                    return true;

            }
            return false;
        }

    };
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForMarshmallowPermission();
        dataManager = DataManager.getInstance(this);
        dataManager.writeBoolean(PreferencesHelper.TRACING_STATUS,false);
        Intent startLocationService = new Intent(this, LocationHelper.class);
        startService(startLocationService);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        TrackMeFragment trackMeFragment = new TrackMeFragment();
        fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.main_container, trackMeFragment, null)
                .addToBackStack(trackMeFragment.getClass().getName())
                .commit();
    }

    //Check ALl The Marshmallow Permission Necessary For First Use.
    public void checkForMarshmallowPermission() {

        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    isPermissionGiven(Manifest.permission.ACCESS_COARSE_LOCATION, PERMISSION_LOCATION_COARSE);
                    break;

                case 1:
                    isPermissionGiven(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_LOCATION_FINE);
                    break;

            }
        }

    }


    //Check If Permission Given To The Required Permission
    private boolean isPermissionGiven(String permissionName, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                permissionName)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{permissionName},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION_FINE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    isPermissionGiven(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_LOCATION_FINE);


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;

            case PERMISSION_LOCATION_COARSE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    isPermissionGiven(Manifest.permission.ACCESS_COARSE_LOCATION, PERMISSION_LOCATION_COARSE);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        UtilityMethods.isGpsEnabled(this);
    }
}
