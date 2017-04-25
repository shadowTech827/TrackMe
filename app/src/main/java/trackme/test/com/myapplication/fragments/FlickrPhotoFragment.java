package trackme.test.com.myapplication.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import trackme.test.com.myapplication.R;
import trackme.test.com.myapplication.adapter.FlickrPhotoAdapter;
import trackme.test.com.myapplication.datamanager.model.FlickrPhotoModel;
import trackme.test.com.myapplication.datamanager.network.DataManager;
import trackme.test.com.myapplication.util.HelperConstants;

public class FlickrPhotoFragment extends Fragment {

    private static final String TAG = FlickrPhotoFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DataManager dataManager;
    private Gson gson;
    private GsonBuilder gsonBuilder;
    private List<FlickrPhotoModel.PhotosBean.PhotoBean> flickrPhotoModelList;
    private GridLayoutManager gridLayoutManager;
    private View mView;
    @BindView(R.id.rv_image_list)  RecyclerView recyclerView;
    private FlickrPhotoAdapter flickrPhotoAdapter;
    @BindView(R.id.progress_bar)  ProgressBar progressBar;
    public FlickrPhotoFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_flickr_photo, container, false);
        ButterKnife.bind(this,mView);

        // Inflate the layout for this fragment
        return mView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataManager = DataManager.getInstance(getContext());

        flickrPhotoModelList = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        retrievePhotoData();
    }

    private void retrievePhotoData() {

        Log.e(TAG, HelperConstants.FLICKR_PHOTO_API);
        dataManager.jsonObjectRequest(Request.Method.GET, HelperConstants.FLICKR_PHOTO_API, null, new DataManager.VolleyJsonCallBack() {
            @Override
            public void onSuccess(JSONObject object) {
                gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();

                FlickrPhotoModel flickrModel = gson.fromJson(object.toString(),FlickrPhotoModel.class);
                flickrPhotoModelList = flickrModel.getPhotos().getPhoto();
                flickrPhotoAdapter = new FlickrPhotoAdapter(flickrPhotoModelList, getContext());
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(flickrPhotoAdapter);
                flickrPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


}
