package trackme.test.com.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesHelper {



    private static PreferencesHelper instance = null;
    private static SharedPreferences.Editor editor;
    private final SharedPreferences mPref;
    public static String TRACING_STATUS = "TRACING_STATUS";
    public static String TRACE_COUNT = "TRACE_COUNT";

    private PreferencesHelper(Context context) {

        mPref = context.getSharedPreferences(HelperConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static PreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesHelper(context);
        }
        return instance;
    }


    public void writeString(String key, String value) {
        if (mPref != null) {
            editor = mPref.edit();
            editor.putString(key, value);
            editor.apply();
            editor.commit();
        }

    }

    public String readString(String key) {
        return (mPref != null) ? mPref.getString(key, null) : null;
    }

    public void writeBoolean(String key, boolean value) {
        if (mPref != null) {
            editor = mPref.edit();
            editor.putBoolean(key, value);
            editor.apply();
            editor.commit();
        }

    }

    public boolean readBoolean(String key) {
        return ((mPref != null) && (mPref.getBoolean(key, false)));
    }

    public void writeInt(String key, int value) {
        if (mPref != null) {
            editor = mPref.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

    public int readInt(String key) {
        return (mPref != null) ? mPref.getInt(key, -1) : -1;
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

    public void writeLong(String key, long value) {
        if (mPref != null) {
            editor = mPref.edit();
            editor.putLong(key, value);
            editor.apply();
        }
    }

    public Long readLong(String key, long defaultValue) {
        return (mPref != null) ? mPref.getLong(key, defaultValue) : -1;
    }

    public void removePref(String key) {

        if (mPref != null && key != null) {
            editor = mPref.edit();
            editor.remove(key);
            editor.commit();
        }

    }
}
