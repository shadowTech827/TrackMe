package trackme.test.com.myapplication.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import java.util.List;

import trackme.test.com.myapplication.R;
import trackme.test.com.myapplication.datamanager.model.FlickrPhotoModel;
import trackme.test.com.myapplication.fragments.FlickrPhotoFragment;

/**
 * Created by sarthak on 24/04/17.
 */

public class FlickrPhotoAdapter extends RecyclerView.Adapter<FlickrPhotoAdapter.PhotoViewHolder> {

    private static final String TAG = FlickrPhotoAdapter.class.getSimpleName();
    private List<FlickrPhotoModel.PhotosBean.PhotoBean> photoBeanList;
    private Context mContext;

    public FlickrPhotoAdapter(List<FlickrPhotoModel.PhotosBean.PhotoBean> photoBeanList, Context mContext) {
        this.photoBeanList = photoBeanList;
        this.mContext = mContext;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_row, parent, false);
        return new FlickrPhotoAdapter.PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {
        FlickrPhotoModel.PhotosBean.PhotoBean photoBean = photoBeanList.get(position);
        String url = "https://farm" + photoBean.getFarm() + ".staticflickr.com/" + photoBean.getServer() +
                "/"+ photoBean.getId() +"_"+ photoBean.getSecret()+".jpg";
        Picasso.with(mContext).load(url).placeholder(R.drawable.progress_animation).into(holder.imageView);
        if(!TextUtils.isEmpty(photoBean.getTitle()))
            holder.tvDescription.setText(photoBean.getTitle());
        else
            holder.tvDescription.setText("No Title");
    }

    @Override
    public int getItemCount() {
        return photoBeanList.size();
    }


    class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private   ViewFlipper card_front ;
        private LinearLayout mlinearLayout;
        private TextView tvDescription;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            card_front = (ViewFlipper) itemView.findViewById(R.id.card_front);
            mlinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_main_container);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        mlinearLayout.callOnClick();
                    }
                }
            });
            tvDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        mlinearLayout.callOnClick();
                    }
                }
            });
           mlinearLayout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   FlickrPhotoModel.PhotosBean.PhotoBean photo = photoBeanList.get(getAdapterPosition());
                   Log.e(TAG, "onClick: " + photo );

                   if(photo!=null && !photo.isFlipped()) {
                       card_front.setInAnimation(mContext, R.anim.fade_in);
                       card_front.setOutAnimation(mContext, R.anim.fade_out);
                       photoBeanList.get(getAdapterPosition()).setFlipped(true);
                       tvDescription.setText(photo.getTitle());
                       card_front.showNext();
                   }else{
                       photoBeanList.get(getAdapterPosition()).setFlipped(false);
                       card_front.setInAnimation(mContext, R.anim.fade_in);
                       card_front.setOutAnimation(mContext, R.anim.fade_out);
                       card_front.showPrevious();
                   }
               }
           });
        }

    }
}
