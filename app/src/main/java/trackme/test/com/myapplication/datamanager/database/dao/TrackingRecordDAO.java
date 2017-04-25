package trackme.test.com.myapplication.datamanager.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import trackme.test.com.myapplication.datamanager.database.AbstractDatabaseManager;
import trackme.test.com.myapplication.datamanager.model.TrackingRecord;

/**
 * Created by sarthak on 23/04/17.
 */

public class TrackingRecordDAO extends AbstractDatabaseManager<TrackingRecord.DataPoints> {
    public static String TABLE_NAME = "TRACKING_RECORDS";
    private String LATITUDE = "LATITUDE";
    private String LONGITUDE = "LONGITUDE";
    private String DATE_RECORDED = "DATE_RECORDED";
    private String TRACE_COUNT = "TRACE_COUNT";
    private String TAG = TrackingRecordDAO.class.getSimpleName();

    public String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE_NAME + "("

                + KEY_ID + " INTEGER PRIMARY KEY,"

                + LATITUDE + " INTEGER, "

                + LONGITUDE + " INTEGER, "

                + TRACE_COUNT +" INTEGER, "

                + DATE_RECORDED + " INTEGER "

                + ")";
    }
    @Override
    public ContentValues generateContentValuesFromObject(TrackingRecord.DataPoints trackingRecord) {

        if (trackingRecord == null)
            return null;

        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(LATITUDE, trackingRecord.getmLatitiude());
            contentValues.put(LONGITUDE, trackingRecord.getmLongitude());
            contentValues.put(DATE_RECORDED, trackingRecord.getDateRecorded());
            contentValues.put(TRACE_COUNT, trackingRecord.getTraceCount());
            return contentValues;
        }

    }

    @Override
    public TrackingRecord.DataPoints generateObjectFromCursor(Cursor c) {
        if (c == null)
        return null;
        else{
            TrackingRecord.DataPoints trackingRecord = new TrackingRecord.DataPoints();
            trackingRecord.setmLatitiude(c.getDouble(c.getColumnIndex(LATITUDE)));
            trackingRecord.setmLongitude(c.getDouble(c.getColumnIndex(LONGITUDE)));
            trackingRecord.setDateRecorded(c.getLong(c.getColumnIndex(DATE_RECORDED)));
            trackingRecord.setTraceCount(c.getLong(c.getColumnIndex(TRACE_COUNT)));

            return trackingRecord;
        }
    }

    public String getTrackingRecords(long traceCount){
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + TRACE_COUNT + "= '" + traceCount  + "'";
        Log.e(TAG, "getTrackingRecords: " + query );
        return query;
    }

    public String getAllTrackingRecords(){
        String query = "SELECT * FROM " + TABLE_NAME ;
        Log.e(TAG, "getTrackingRecords: " + query );
        return query;
    }

}
