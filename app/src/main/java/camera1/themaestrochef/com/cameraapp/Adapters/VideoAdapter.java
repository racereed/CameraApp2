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

import camera1.themaestrochef.com.cameraapp.Activities.ShowAppVideos;
import camera1.themaestrochef.com.cameraapp.Activities.VideoPreviewActivity;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Models.Model_Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.viewHolder> {

    private ShowAppVideos mContext;
    private ArrayList<Model_Video> mVideos;

    public VideoAdapter(ShowAppVideos mContext, ArrayList<Model_Video> mVideos) {
        this.mContext = mContext;
        this.mVideos = mVideos;
    }

    @NonNull
    @Override
    public VideoAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoAdapter.viewHolder(LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.viewHolder holder, int position) {
        Glide.with(mContext).load(mVideos.get(position).getStr_thumb()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (mVideos == null)
            return 0;
        return mVideos.size();
    }

    class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private ImageView imageView1;

        public viewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.app_video);
            imageView1 = itemView.findViewById(R.id.imageView2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, VideoPreviewActivity.class);
            intent.putExtra("video", mVideos.get(getAdapterPosition()).getStr_path());
            mContext.startActivity(intent);
        }
    }
}
