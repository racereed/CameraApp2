package camera1.themaestrochef.com.cameraapp.Adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import camera1.themaestrochef.com.cameraapp.Activities.ImagePreviewActivity;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Activities.ShowAppImages;

/**
 * Created by ELSaghier on 7/25/2018.
 */

public class AppImagesAdapter extends RecyclerView.Adapter<AppImagesAdapter.viewHolder> {

    private ShowAppImages mContext;
    private ArrayList<String> mPath;

    public AppImagesAdapter(ShowAppImages mContext, ArrayList<String> mPath) {
        this.mContext = mContext;
        this.mPath = mPath;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Glide.with(mContext).load(mPath.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (mPath == null)
            return 0;
        return mPath.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        viewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.app_image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ImagePreviewActivity.class);
                    intent.putExtra("imagePath", mPath.get(getAdapterPosition()));
                    mContext.startActivity(intent);
                }
            });
        }
    }

}

