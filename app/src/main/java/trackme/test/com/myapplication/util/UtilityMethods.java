package trackme.test.com.myapplication.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by sarthak on 23/04/17.
 */

public class UtilityMethods {
    public static void isGpsEnabled(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage("GPS is disabled in your device. Please enable it...")
                    .setCancelable(false)
                    .setPositiveButton("Enable",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    context.startActivity(callGPSSettingIntent);
                                }
                            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }
    public final static String DATE_PATTERN_WITH_SECONDS = "yyyy-MM-dd HH:mm:ss";
    public static String getMilliToDate(long mSec, String desiredFormat) {
        DateFormat dateFormat = new SimpleDateFormat(desiredFormat, Locale.US);
        //Convert Milliseconds to Date in particular format
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mSec);
        System.out.println(dateFormat.format(cal.getTime()) + "**************************");
        return dateFormat.format(cal.getTime());
    }
}
