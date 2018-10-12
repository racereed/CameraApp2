package camera1.themaestrochef.com.cameraapp.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Utilities.AdsUtilities;
import camera1.themaestrochef.com.cameraapp.Models.Model_Video;
import camera1.themaestrochef.com.cameraapp.Utilities.PermissionUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.SharedPreferencesUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilise;

public class CaptureVideo extends AppCompatActivity {

    private static final String CAMERA_FACING_MODE = "camera_facing_mode";
    private static final String CAMERA_MODE_FRONT = "FRONT";
    @BindView(R.id.camera)
    CameraView mCameraView;

    @BindView(R.id.switch_flash)
    ImageView flashIcon;

    @BindView(R.id.pinch_image)
    ImageView pinchIcon;

    @BindView(R.id.last_captured_video)
    ImageView lastImage;

    @BindView(R.id.pause_video)
    ImageView pauseVideo;

    @BindView(R.id.take_video)
    ImageView takeVideo;

    @BindView(R.id.adView)
    AdView mAdView;


    private static final Flash[] FLASH_OPTIONS = {
            Flash.OFF,
            Flash.TORCH
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private int mCurrentFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_video);
        ButterKnife.bind(this);

        //Hide notificationBar
        UiUtilise.hideSystemBar(this);
        UiUtilise.hideToolBar(this);
        initIcons();
        updateLastVideo(0);
        if (mCameraView != null) {
            mCameraView.addCameraListener(new CameraListener() {
                @Override
                public void onVideoTaken(final File video) {
                    super.onVideoTaken(video);
                    Uri x = Uri.fromFile(video);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, x));
                    updateLastVideo(1000);
                }
            });
        }

        if (savedInstanceState != null) {
            String mode = savedInstanceState.getString(CAMERA_FACING_MODE);
            if (mode != null)
                if (mode.equals(CAMERA_MODE_FRONT))
                    mCameraView.setFacing(Facing.FRONT);

        }

        AdsUtilities.initAds(mAdView);

    }

    private void initIcons() {
        mCurrentFlash = SharedPreferencesUtilities.getFlashIndex(this) % 2;
        flashIcon.setImageResource(FLASH_ICONS[mCurrentFlash]);
        mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);

        isPunchable = SharedPreferencesUtilities.getPinchValue(this);
        if (mCameraView != null) {
            if (isPunchable) {
                mCameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
                pinchIcon.setImageResource(android.R.drawable.star_big_on);
            } else {
                mCameraView.mapGesture(Gesture.PINCH, GestureAction.NONE);
                pinchIcon.setImageResource(android.R.drawable.star_big_off);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtilities.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
        mCurrentFlash = SharedPreferencesUtilities.getFlashIndex(this) % 2;
        flashIcon.setImageResource(FLASH_ICONS[mCurrentFlash]);
        mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);

        if (PermissionUtilities.checkAndRequestPermissions(this)) {
            Model_Video modelVideo = fn_video();
            if (modelVideo != null)
                Glide.with(this).load(modelVideo.getStr_thumb()).into(lastImage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    public Model_Video fn_video() {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name, column_id, thum;

        String absolutePathOfImage;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        if (cursor.moveToFirst()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
            Log.e("column_id", cursor.getString(column_id));
            Log.e("thum", cursor.getString(thum));

            Model_Video obj_model = new Model_Video();
            obj_model.setBoolean_selected(false);
            obj_model.setStr_path(absolutePathOfImage);
            obj_model.setStr_thumb(cursor.getString(thum));

            return obj_model;

        }

        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraView.destroy();
    }


    @OnClick(R.id.take_video)
    public void captureVideo() {
        takeVideo.setVisibility(View.INVISIBLE);
        pauseVideo.setVisibility(View.VISIBLE);

        if (mCameraView != null) {
            File f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/VID_" + System.currentTimeMillis() / 1000 + "_.mp4");
            f.setWritable(true);
            f.setReadable(true);
            mCameraView.startCapturingVideo(f);
        }
    }

    @OnClick(R.id.pause_video)
    public void stopVideo() {
        takeVideo.setVisibility(View.VISIBLE);
        pauseVideo.setVisibility(View.INVISIBLE);
        if (mCameraView != null) {
            mCameraView.stopCapturingVideo();

        }

    }

    @OnClick(R.id.switch_flash)
    public void switchFlash() {
        if (mCameraView != null) {
            mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
            flashIcon.setImageResource(FLASH_ICONS[mCurrentFlash]);
            mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
            SharedPreferencesUtilities.setFlash(this, mCurrentFlash);
        }
    }

    @OnClick(R.id.switch_camera)
    public void switchCamera() {
        mCameraView.toggleFacing();
    }

    boolean isPunchable;

    @OnClick(R.id.pinch_image)
    public void switchPinch() {
        if (isPunchable) {
            pinchIcon.setImageResource(android.R.drawable.star_big_off);

            mCameraView.mapGesture(Gesture.PINCH, GestureAction.NONE); // Pinch to zoom!
            isPunchable = false;
        } else {
            mCameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
            pinchIcon.setImageResource(android.R.drawable.star_big_on);
            isPunchable = true;
        }
        SharedPreferencesUtilities.setPinch(this, isPunchable);
    }

    @OnClick(R.id.last_captured_video)
    public void showImages() {
        Intent intent = new Intent(this, ShowAppVideos.class);
        startActivity(intent);
    }

    @OnClick(R.id.imageView)
    public void openCamera(View view) {
        Intent intent = new Intent(this, CaptureImage.class);
        startActivity(intent);
        finish();
    }

    private void updateLastVideo(final int time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                    final Model_Video modelVideo = fn_video();

                    if (modelVideo != null)
                        lastImage.post(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(CaptureVideo.this).load(modelVideo.getStr_thumb()).into(lastImage);
                            }
                        });
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCameraView.getFacing() == Facing.FRONT) {
            outState.putString(CAMERA_FACING_MODE, CAMERA_MODE_FRONT);
        }
    }
}
