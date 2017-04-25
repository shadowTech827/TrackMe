package trackme.test.com.myapplication.datamanager.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class DatabaseManager {
    private static final String TAG = "DatabaseManager";


    /* thread safe way of using sqlite instance

    // In your application class
    DatabaseManager.initializeInstance(new MySQLiteOpenHelper());

    SQLiteDatabase database = DatabaseManager.getInstance().openDatabase();
    database.insert(...);
    // database.close(); Don't close it directly!
    DatabaseManager.getInstance().closeDatabase(); // correct way
    */
    private static DatabaseManager instance;
    private static DataBaseHelper mDatabaseHelper;
    private int mOpenCounter;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(DataBaseHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter++;
        if (mOpenCounter == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        //LoggerUtility.d(TAG, "openDatabase: " + mOpenCounter);
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        mOpenCounter--;
        if (mOpenCounter == 0) {
            // Closing database
            mDatabase.close();

        }
        //LoggerUtility.d(TAG, "closeDatabase: " + mOpenCounter);
    }

}