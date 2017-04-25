package trackme.test.com.myapplication.services;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import trackme.test.com.myapplication.activity.MainActivity;
import trackme.test.com.myapplication.datamanager.database.AbstractDatabaseManager;
import trackme.test.com.myapplication.datamanager.database.dao.TrackingRecordDAO;
import trackme.test.com.myapplication.datamanager.model.TrackingRecord;
import trackme.test.com.myapplication.datamanager.network.DataManager;
import trackme.test.com.myapplication.util.PreferencesHelper;

public class LocationHelper extends Service implements
        com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    protected static final String TAG = "LocationHelper";
    private GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;
    private LocationRequest mLocationRequest, immediateLocationRequest;
    private static int UPDATE_INTERVAL = 10 * 1000; //
    private static int FASTEST_INTERVAL = 20 * 1000; //
    public static double latitude, longitude;
    private DataManager dataManager;
    public static LocationHelper locationHelper;

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");

        dataManager = DataManager.getInstance(getApplicationContext());
        locationHelper = this;
        buildGoogleApiClient();

        createLocationRequest();
    }

    public static LocationHelper getInstance(){
        return locationHelper;
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    protected void makeImmediateRequest() {
        Log.e(TAG, "makeImmediateRequest: ");
        immediateLocationRequest = new LocationRequest();
        immediateLocationRequest.setInterval(0);
        immediateLocationRequest.setFastestInterval(0);
        immediateLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient =   new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        //Log.d(TAG, "inside buildGoogleApi");
    }

    public void immediateRequest() {
        try {
            Log.e(TAG, "immediateRequest: ");
            if (latitude != 0.0 && longitude != 0.0) {
                Log.e(TAG, "immediateRequest: inside !0 ");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                                this);
            } else {
                Log.e(TAG, "immediateRequest: else part ");
                makeImmediateRequest();
                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient, immediateLocationRequest,
                                this);
            }
        } catch (Exception e) {
            Log.e(TAG, "catch immedita" + e.getMessage());

            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                            this);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(TAG, " start of run");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        //Log.d(TAG, " end of run");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopLocationUpdates();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
        Log.e(TAG, "lat:" + latitude + " longitude: " + longitude);
        startLocationUpdates();

    }


    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);    //throws error if googleapi is not connected
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            immediateRequest();
            //            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, immediateLocationRequest, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        checkForSignedUser();
        mLastLocation = location;
        Log.e(TAG, "onLocationChanged: ");
        if (latitude == 0.0 || longitude == 0.0) {
            Log.e(TAG, "onLocationChanged Lat & Long is 0.0 " + latitude + ":" + longitude);//remove location updates so that it resets
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);//change the time of location updates
            mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(UPDATE_INTERVAL).setFastestInterval(FASTEST_INTERVAL);//restart location updates with the new interval
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }

        if(dataManager.readBoolean(PreferencesHelper.TRACING_STATUS)){
            saveRecord();
        }else{
            stopSelf();
        }
    }

    private void saveRecord() {
        TrackingRecord.DataPoints trackingRecord = new TrackingRecord.DataPoints();
        trackingRecord.setmLatitiude(latitude);
        trackingRecord.setmLongitude(longitude);
        trackingRecord.setDateRecorded(System.currentTimeMillis());
        trackingRecord.setTraceCount(dataManager.readLong(PreferencesHelper.TRACE_COUNT,0));
        TrackingRecordDAO trackinManger = new TrackingRecordDAO();
        trackinManger.save(trackinManger.TABLE_NAME,new TrackingRecordDAO().generateContentValuesFromObject(trackingRecord));
    }

    private void checkForSignedUser() {

//        UserData user = database.where(UserData.class).findFirst();
//        if (user == null) {
//            Log.e(TAG, "checkForSignedUser: stop self");
//            stopSelf();
//        }
    }

  /*  public static double getHaversineDistanceInMeters(double lat1, double lon1, double lat2,
                                                      double lon2) {

        final double RADIUS_OF_EARTH = 6372800; // Radius of earth in meters

        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);
        double temp = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.sin(deltaLon / 2)
                * Math.sin(deltaLon / 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double temp2 = 2 * Math.asin(Math.sqrt(temp));

        // Haversine distance in kms between two geo points
        return RADIUS_OF_EARTH * temp2 / 1000;
    }
*/

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}