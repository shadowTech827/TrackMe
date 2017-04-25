package trackme.test.com.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import trackme.test.com.myapplication.R;
import trackme.test.com.myapplication.datamanager.database.dao.TrackingRecordDAO;
import trackme.test.com.myapplication.datamanager.model.TrackingRecord;
import trackme.test.com.myapplication.util.PreferencesHelper;
import trackme.test.com.myapplication.util.UtilityMethods;

/**
 * Created by sarthak on 23/04/17.
 */

//Adapter for Maps Layout
public class TraceHistoryAdapter extends RecyclerView.Adapter<TraceHistoryAdapter.ViewHolder> {

    private final HashSet<MapView> mMaps = new HashSet<>();
    private List<TrackingRecord> mLocations;
    private Context context;
    public TraceHistoryAdapter(List<TrackingRecord> locations, Context context) {
        this.mLocations = locations;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new MapViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewholder, int position) {
        final int tempPosition = position;

            final MapViewHolder holder = (MapViewHolder) viewholder;

            holder.initializeMapView();
            mMaps.add(holder.lite_listrow_map);

            holder.lite_listrow_map.setTag(mLocations.get(tempPosition));

            if (holder.map != null) {
                setMapLocation(holder.map, mLocations.get(tempPosition));
            }
            List<TrackingRecord.DataPoints> dataPoints = mLocations.get(tempPosition).getDataPointsList();
        if(null!= dataPoints && dataPoints.size() > 0) {
            holder.tvStartTime.setText("Start time: " + UtilityMethods.getMilliToDate(dataPoints.get(dataPoints.size() - 1).getDateRecorded(),UtilityMethods.DATE_PATTERN_WITH_SECONDS));
            holder.tv_EndTime.setText("End time: " + UtilityMethods.getMilliToDate(dataPoints.get(0).getDateRecorded()
                    ,UtilityMethods.DATE_PATTERN_WITH_SECONDS));

        }

    }

    @Override
    public int getItemCount() {
        return mLocations.size() ;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    //Custom ViewHolder class
    class MapViewHolder extends ViewHolder implements OnMapReadyCallback {

        private LinearLayout TraceCard;
        private MapView lite_listrow_map;
        private TextView tvStartTime;
        private TextView tv_EndTime;
        private GoogleMap map;

        MapViewHolder(View v) {
            super(v);
            this.TraceCard = (LinearLayout) v.findViewById(R.id.TraceCard);
            this.lite_listrow_map = (MapView) v.findViewById(R.id.lite_listrow_map);
            this.tvStartTime = (TextView) v.findViewById(R.id.tv_startTime);
            this.tv_EndTime = (TextView) v.findViewById(R.id.tv_endTime);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            this.map = googleMap;
            TrackingRecord data = (TrackingRecord) lite_listrow_map.getTag();
            if (data != null && map != null) {
                setMapLocation(map, data);
            }
        }

        private void initializeMapView() {
            if (lite_listrow_map != null) {
                lite_listrow_map.onCreate(null);
                lite_listrow_map.getMapAsync(this);
                lite_listrow_map.setOnClickListener(null);
                lite_listrow_map.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
            }
        }
    }


    //SetUp Map Location
    private void setMapLocation(GoogleMap map, TrackingRecord data) {
        if (data.getDataPointsList() != null && data.getDataPointsList().size() > 0) {

            List<TrackingRecord.DataPoints> trackingrecordList = data.getDataPointsList();
            List<LatLng> latLngList = new ArrayList<>();
            if (trackingrecordList != null && trackingrecordList.size() > 0) {
                for (int i = 0; i < trackingrecordList.size(); i++) {
                    latLngList.add(new LatLng(trackingrecordList.get(i).getmLatitiude(),
                            trackingrecordList.get(i).getmLongitude()));
                    if(i == trackingrecordList.size() - 1){
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(trackingrecordList.get(trackingrecordList.size() - 1).getmLatitiude(),
                                trackingrecordList.get(trackingrecordList.size() - 1).getmLongitude()));
                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_flag_b));
                        map.addMarker(marker);
                    } else if (i == 0) {
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(trackingrecordList.get(0).getmLatitiude(),
                                trackingrecordList.get(0).getmLongitude()));
                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_flag_b));
                        map.addMarker(marker);
                    }
                }

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.width(6).color(Color.parseColor("#0979ff"));
                polylineOptions.addAll(latLngList);
                map.addPolyline(polylineOptions);

                LatLng destination = new LatLng(data.getDataPointsList().get(0).getmLatitiude(),
                        data.getDataPointsList().get(0).getmLongitude());
                LatLng origin = new LatLng(data.getDataPointsList().get(data.getDataPointsList().size() - 1).getmLatitiude(),
                        data.getDataPointsList().get(data.getDataPointsList().size() - 1).getmLongitude());

                LatLngBounds.Builder boundsBuilder = LatLngBounds.builder()
                        .include(origin)
                        .include(destination);
                map.setPadding(0, ContextCompat.getDrawable(context, R.drawable.start_flag).getIntrinsicHeight(), 0, 0);
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 20));
/*
            map.addMarker(new MarkerOptions().position(origin).icon(BitmapDescriptorFactory.fromResource(R.drawable.start_flag)));
            map.addMarker(new MarkerOptions().position(destination).icon(BitmapDescriptorFactory.fromResource(R.drawable.end_flag)));*/

                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 13f));
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    }
}