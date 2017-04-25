package trackme.test.com.myapplication.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import ng.max.slideview.SlideView;
import trackme.test.com.myapplication.R;
import trackme.test.com.myapplication.datamanager.database.AbstractDatabaseManager;
import trackme.test.com.myapplication.datamanager.database.dao.TrackingRecordDAO;
import trackme.test.com.myapplication.datamanager.model.TrackingRecord;
import trackme.test.com.myapplication.datamanager.network.DataManager;
import trackme.test.com.myapplication.services.LocationHelper;
import trackme.test.com.myapplication.util.PreferencesHelper;

public class TrackMeFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private GoogleMap mGoogleMap;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mView;
    private SlideView mSlideView;
    private static boolean isSlided = false;
    private int ZOOM_CAMERA = 10;
    private int TILT_CAMERA = 30; //degrees
    private String TAG = TrackMeFragment.class.getSimpleName();
    private DataManager datamanager;
    private long traceCount = 0;
    public TrackMeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackMeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrackMeFragment newInstance(String param1, String param2) {
        TrackMeFragment fragment = new TrackMeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mView != null) {
            if ((ViewGroup) mView.getParent() != null)
                ((ViewGroup) mView.getParent()).removeView(mView);
            return mView;
        }else {
            mView = inflater.inflate(R.layout.fragment_track_me, container, false);
        }
        datamanager = DataManager.getInstance(getContext());
        traceCount = datamanager.readLong(PreferencesHelper.TRACE_COUNT,0);
        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        datamanager = DataManager.getInstance(getContext());
        initUIComponents();

    }

    private void initUIComponents() {
        mSlideView = (SlideView)  mView.findViewById(R.id.slideView);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        if(!isSlided){
            mSlideView.setText("Shift start");
            mSlideView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
            mSlideView.setButtonBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)));
        }else{
            mSlideView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));
            mSlideView.setButtonBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
            mSlideView.setText("Shift stop");
        }
        mSlideView.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                // vibrate the device
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);

                if(isSlided){
                    isSlided = false;
                    datamanager.writeBoolean(PreferencesHelper.TRACING_STATUS,false);
                    LocationHelper.getInstance().stopSelf();
                    traceThePath();
                    mSlideView.setText("Shift start");
                    mSlideView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
                    mSlideView.setButtonBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)));
                }else{
                    isSlided = true;
                    mGoogleMap.clear();
                    Intent startLocationService = new Intent(getContext(), LocationHelper.class);
                    getActivity().startService(startLocationService);
                    datamanager.writeBoolean(PreferencesHelper.TRACING_STATUS,true);
                    mSlideView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));
                    mSlideView.setButtonBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
                    mSlideView.setText("Shift stop");
                    traceCount ++;
                    datamanager.writeLong(PreferencesHelper.TRACE_COUNT,traceCount);
                }
                // go to a new activity
                //  startActivity(new Intent(MainActivity.this, NewActivity.class));

            }
        });
    }

    private void traceThePath() {
        mGoogleMap.clear();
        AbstractDatabaseManager trackingDAO = new TrackingRecordDAO();
        List<TrackingRecord.DataPoints> trackingrecordList = trackingDAO.loadAll(trackingDAO, new TrackingRecordDAO().getTrackingRecords(datamanager.readLong(PreferencesHelper.TRACE_COUNT,0)));
        List<LatLng>  latLngList = new ArrayList<>();
        if(trackingrecordList != null && trackingrecordList.size() > 0){
            for (int i = 0; i < trackingrecordList.size();i++){
                latLngList.add(new LatLng(trackingrecordList.get(i).getmLatitiude(),
                        trackingrecordList.get(i).getmLongitude()));
                if(i == trackingrecordList.size() - 1){
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(trackingrecordList.get(trackingrecordList.size() - 1).getmLatitiude(),
                            trackingrecordList.get(trackingrecordList.size() - 1).getmLongitude()));
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_flag_b));
                    mGoogleMap.addMarker(marker);
                } else if (i == 0) {
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(trackingrecordList.get(0).getmLatitiude(),
                            trackingrecordList.get(0).getmLongitude()));
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_flag_b));
                    mGoogleMap.addMarker(marker);
                }
            }

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(6).color(Color.parseColor("#0979ff"));
            polylineOptions.addAll(latLngList);
            mGoogleMap.addPolyline(polylineOptions);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady: " );
       mGoogleMap = googleMap;
        setUpMap();
    }

    public void setUpMap() {
        Log.e(TAG, "setUpMap: " + mGoogleMap );
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
        mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.setBuildingsEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        markOnMap();
    }

    private void markOnMap() {
        if (null != mGoogleMap)
            mGoogleMap.clear();


            if (null != mGoogleMap && LocationHelper.latitude > 0.0) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(LocationHelper.latitude, LocationHelper.longitude)).zoom(ZOOM_CAMERA)
                        .tilt(TILT_CAMERA).build();

                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
    }
}
