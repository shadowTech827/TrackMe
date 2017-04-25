package trackme.test.com.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import trackme.test.com.myapplication.R;
import trackme.test.com.myapplication.adapter.TraceHistoryAdapter;
import trackme.test.com.myapplication.datamanager.database.AbstractDatabaseManager;
import trackme.test.com.myapplication.datamanager.database.dao.TrackingRecordDAO;
import trackme.test.com.myapplication.datamanager.model.TrackingRecord;
import trackme.test.com.myapplication.datamanager.network.DataManager;

public class TrackMeHistory extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = TrackMeHistory.class.getSimpleName();
    private View mView;
    private List<TrackingRecord> trackingRecordList;
    private TraceHistoryAdapter traceHistoryadapter;
    private LinearLayoutManager llManger;
    private RecyclerView rv;
    private DataManager dataManger;
    public TrackMeHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackMeHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static TrackMeHistory newInstance(String param1, String param2) {
        TrackMeHistory fragment = new TrackMeHistory();
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
        mView = inflater.inflate(R.layout.fragment_track_me_history, container, false);
        rv = (RecyclerView) mView.findViewById(R.id.rv_history_list);
        trackingRecordList = new ArrayList<>();
        traceHistoryadapter = new TraceHistoryAdapter(trackingRecordList,getContext());
        llManger = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(llManger);
        rv.setAdapter(traceHistoryadapter);
        // Inflate the layout for this fragment
        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        retrieveData();
    }

    private void retrieveData() {
        AbstractDatabaseManager abstractDatabaseManager = new TrackingRecordDAO();

        List<TrackingRecord.DataPoints> trackingDataList = abstractDatabaseManager.loadAll(abstractDatabaseManager,
                new TrackingRecordDAO().getAllTrackingRecords());

        if (trackingDataList != null && trackingDataList.size() > 0) {
            Collections.sort(trackingDataList, new Comparator<TrackingRecord.DataPoints>() {
                @Override
                public int compare(TrackingRecord.DataPoints point1, TrackingRecord.DataPoints point2) {
                    return (point1.getTraceCount() > point2.getTraceCount() ? 1 : -1);
                }
            });
            Log.e(TAG, "retrieveData: " + trackingDataList.toString() );
            int traceCount = 1;
            List<TrackingRecord.DataPoints> trackinPoints = new ArrayList<>();
            for (TrackingRecord.DataPoints trackingPoint : trackingDataList) {

                if (traceCount == trackingPoint.getTraceCount()) {
                    trackinPoints.add(trackingPoint);
                } else {
                    TrackingRecord trackingRecord = new TrackingRecord();
                    trackingRecord.setTraceCount(traceCount);
                    trackingRecord.setDataPointsList(trackinPoints);
                    Log.e(TAG, "retrieveData: " + trackingRecord.toString() );
                    trackingRecordList.add(trackingRecord);
                    trackinPoints = new ArrayList<>();
                    traceCount ++;
                }
            }
            TrackingRecord trackingRecord = new TrackingRecord();
            trackingRecord.setTraceCount(traceCount);
            trackingRecord.setDataPointsList(trackinPoints);
            Log.e(TAG, "retrieveData: " + trackingRecord.toString() );
            trackingRecordList.add(trackingRecord);
        }
        traceHistoryadapter.notifyDataSetChanged();
        Log.e(TAG, "retrieveData: " + trackingRecordList.size()  + "/n List" + trackingDataList.toString());


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
