package camera1.themaestrochef.com.cameraapp.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import camera1.themaestrochef.com.cameraapp.Adapters.ViewPageAdapter;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Utilities.AdsUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.PermissionUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilise;

public class ImagePreviewActivity extends AppCompatActivity {

    @BindView(R.id.imageViewer)
    ViewPager pager;

    ViewPageAdapter adapter;

    @BindView(R.id.adView)
    AdView mAdView;
    private String mPath;

    int cIndex;

    public void setmPath(String mPath, int cIndex) {
        this.mPath = mPath;
        this.cIndex = cIndex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        UiUtilise.hideSystemBar(this);
        UiUtilise.hideToolBar(this);
        ButterKnife.bind(this);

        mPath = getIntent().getStringExtra("imagePath");

        //        Glide.with(this).load(mPath).into(imageView);
        AdsUtilities.initAds(mAdView);

    }

    // method to get the position of clicked image in previous screen ( all small images screen)
    private int getOpenedImageIndex() {
        for (int i = 0; i < listOfAllImages.size(); i++)
            if (listOfAllImages.get(i).equalsIgnoreCase(mPath))
                return i;
        return -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ArrayList<String> imagesPaths = getAllShownImagesPath();
        adapter = new ViewPageAdapter(getAllShownImagesPath(), this);
        int index = getOpenedImageIndex();
        pager.setAdapter(adapter);
        // in case of index != -1 that means it found the image URL (always true but for make sure)
        // move to that image
        if (index != -1)
            pager.setCurrentItem(index);

    }

    ArrayList<String> listOfAllImages = new ArrayList<>();

    // for getting images in order of newer ones at front of gallery.
    String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

    private ArrayList<String> getAllShownImagesPath() {
        if (PermissionUtilities.checkAndRequestPermissions(this))
            loadImages();
        return listOfAllImages;
    }


    private void loadImages() {
        listOfAllImages = new ArrayList<>();
        Cursor externalCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, orderBy);
        int column_index_data = externalCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (externalCursor.moveToNext()) {
            listOfAllImages.add(externalCursor.getString(column_index_data));
        }
    }

    @OnClick(R.id.share_btn)
    public void shareTwitter() {

        Intent shareIntent = new Intent();
        Uri photoURI = FileProvider.getUriForFile(this, "com.themaestrochef.camera1",
                new File(mPath));

        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Image with ..."));

    }
}
