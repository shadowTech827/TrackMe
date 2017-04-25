package trackme.test.com.myapplication.datamanager.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import trackme.test.com.myapplication.datamanager.database.dao.TrackingRecordDAO;


/**
 * Created by sarthak on 11/03/17.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static DataBaseHelper sInstance;
    //Database
    private static final String DATABASE_NAME = "TrackMe";
    private static final int DATABASE_VERSION = 1;

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e(DataBaseHelper.class.getSimpleName(), "DataBaseHelper: " + context );

    }

    public static synchronized DataBaseHelper getInstance(Context context) {
        Log.e("DataBaseHelper", "getInstance: " );
        if (sInstance == null) {
            synchronized (DataBaseHelper.class) {
                if (sInstance == null) {
                    Log.e("DataBaseHelper", "getInstance: in" );
                    sInstance = new DataBaseHelper(context);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e("SQLiteDatabase", "onCreate: ");
        sqLiteDatabase.execSQL(new TrackingRecordDAO().getCreateTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
