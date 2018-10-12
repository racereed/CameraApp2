package camera1.themaestrochef.com.cameraapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import camera1.themaestrochef.com.cameraapp.Activities.ImagePreviewActivity;
import camera1.themaestrochef.com.cameraapp.R;

public class ViewPageAdapter extends PagerAdapter {

    private ArrayList<String> mPaths;
    private AppCompatActivity activity;

    // init the adapter with some values
    /*
     * @mPaths for path for all images
     * @activity for using it as context for Glide
     * */
    public ViewPageAdapter(ArrayList<String> mPaths, AppCompatActivity activity) {
        this.mPaths = mPaths;
        this.activity = activity;
    }


    // get number of images to swipe through
    @Override
    public int getCount() {
        if (mPaths == null)
            return 0;
        return mPaths.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.pager_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);

        Glide.with(activity).load(mPaths.get(position)).into(imageView);
        ((ImagePreviewActivity) activity).setmPath(mPaths.get(position == 0 ? 0 : position - 1), position);
        container.addView(itemView);
        return itemView;
    }

    // remove unused items
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
