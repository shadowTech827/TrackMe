package trackme.test.com.myapplication.datamanager.model;

import java.util.List;

/**
 * Created by sarthak on 23/04/17.
 */

public class TrackingRecord  {

    private long traceCount;

    private List<DataPoints> dataPointsList;

    public long getTraceCount() {
        return traceCount;
    }

    public void setTraceCount(long traceCount) {
        this.traceCount = traceCount;
    }

    public List<DataPoints> getDataPointsList() {
        return dataPointsList;
    }

    public void setDataPointsList(List<DataPoints> dataPointsList) {
        this.dataPointsList = dataPointsList;
    }

    @Override
    public String toString() {
        return "TrackingRecord{" +
                "traceCount=" + traceCount +
                ", dataPointsList=" + dataPointsList +
                '}';
    }

    public static class DataPoints {
        private double mLatitiude;
        private double mLongitude;
        private long dateRecorded;
        private long traceCount;

        @Override
        public String toString () {
        return "TrackingRecord{" +
                "mLatitiude=" + mLatitiude +
                ", mLongitude=" + mLongitude +
                ", dateRecorded=" + dateRecorded +
                '}';
    }

    public long getTraceCount() {
        return traceCount;
    }

    public void setTraceCount(long traceCount) {
        this.traceCount = traceCount;
    }

    public double getmLatitiude() {
        return mLatitiude;
    }

    public void setmLatitiude(double mLatitiude) {
        this.mLatitiude = mLatitiude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public long getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(long dateRecorded) {
        this.dateRecorded = dateRecorded;
    }
 }
}
