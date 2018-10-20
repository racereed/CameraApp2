package camera1.themaestrochef.com.cameraapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Utilities.AdsUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.CapturePhotoUtils;
import camera1.themaestrochef.com.cameraapp.Utilities.ImageHelper;
import camera1.themaestrochef.com.cameraapp.Utilities.PermissionUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.SharedPreferencesUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilise;

public class CaptureImage extends AppCompatActivity {

    private static final String CAMERA_FACING_MODE = "camera_facing_mode";
    private static final String CAMERA_MODE_FRONT = "FRONT";

    @BindView(R.id.settings_wheel)
    ImageView mSettingswheel;



    @Nullable
    @BindView(R.id.adView)
    AdView mAdView;

    @BindView(R.id.camera)
    CameraView mCameraView;

    @BindView(R.id.switch_flash)
    ImageView flashIcon;


    @BindView(R.id.last_captured_image)
    ImageView lastImage;

    public static Activity activity;
    boolean isPunchable =true;

    private static final Flash[] FLASH_OPTIONS = {
            Flash.OFF,
            Flash.ON,
            Flash.AUTO
    };



    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
            R.drawable.ic_flash_auto
    };

    private int mCurrentFlash;


    @Override
    public void onDestroy() {

        mCameraView.destroy();
        super.onDestroy();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (InAppPurchases.adsDisabled){
            setContentView(R.layout.content_main_no_ad);
        } else{
            setContentView(R.layout.content_main);
        }
        activity = this;
        ButterKnife.bind(this);


        //Hide notificationBar
        UiUtilise.hideSystemBar(this);
        UiUtilise.hideToolBar(this);
        initIcons();

        if (mCameraView != null) {
            mCameraView.addCameraListener(new CameraListener() {
                @Override
                public void onPictureTaken(final byte[] jpeg) {
                    super.onPictureTaken(jpeg);
                    saveImg(jpeg);
                }
            });
        }

        if (savedInstanceState != null) {
            String mode = savedInstanceState.getString(CAMERA_FACING_MODE);
            if (mode != null)
                if (mode.equals(CAMERA_MODE_FRONT))
                    mCameraView.setFacing(Facing.FRONT);

        } if (!InAppPurchases.adsDisabled)
        AdsUtilities.initAds(mAdView);



    }

    private void initIcons() {
        mCurrentFlash = SharedPreferencesUtilities.getFlashIndex(this);
        flashIcon.setImageResource(FLASH_ICONS[mCurrentFlash]);
        mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);

        isPunchable = SharedPreferencesUtilities.getPinchValue(this);

                mCameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);

        }


    private void saveImg(final byte[] jpeg) {

        CameraUtils.decodeBitmap(jpeg, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(final Bitmap bitmap) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (PermissionUtilities.checkAndRequestPermissions(CaptureImage.this)) {

                                Bitmap waterMarkedImage = ImageHelper.addWatermark
                                        (getResources(),
                                                mCameraView.getFacing() == Facing.FRONT
                                                        ? ImageHelper.flipImage(bitmap)
                                                        : bitmap,getApplicationContext());

                                final String imgPath = CapturePhotoUtils.insertImage
                                        (getContentResolver(),
                                                waterMarkedImage,
                                                "Captured Image", "Image Description");
                                if (imgPath != null)
                                    lastImage.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Glide.with(CaptureImage.this).load(imgPath).into(lastImage);
                                        }
                                    });
                            }
                        } else {
                            final String imgPath = CapturePhotoUtils.insertImage(getContentResolver(), bitmap, "Captured Image", "Image Description");
                            if (imgPath != null)
                                lastImage.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(CaptureImage.this).load(imgPath).into(lastImage);
                                    }
                                });
                        }
                    }
                }).start();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mCameraView.start();
        if (PermissionUtilities.checkAndRequestPermissions(this)) {
            Bitmap bitmap = ImageHelper.getLastTakenImage(CaptureImage.this);
            if (bitmap != null)
                lastImage.setImageBitmap(bitmap);
            else
                lastImage.setVisibility(View.GONE);
        }
    }


    public void makeVolumeToast(){

        // Get the AudioManager instance
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        // Get the music current volume level
        int music_volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        // Get the device music maximum volume level
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        if (music_volume_level < 2){
            Toast.makeText(getApplicationContext(),"Volume Might Be Too Low",Toast.LENGTH_LONG).show();// Set your own toast  message
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtilities.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    MediaPlayer mPlayer;



    @OnClick(R.id.sound_toy)
    public void playToySound() {
        Log.i("hellow", "toy sound works");
        // mPlayer.release();
        mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.squeakytoy2);
        mPlayer.start();//Start playing the music
        makeVolumeToast();

    }

    @OnClick(R.id.puppy_button)
    public void playPuppySound () {
        mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.puppy_sound);
        mPlayer.start();//Start playing the music
        makeVolumeToast();
    }
    @OnClick(R.id.dog_button)
    public void playDogSound () {
        mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.dog_sound);
        mPlayer.start();//Start playing the music
        makeVolumeToast();
    }

    @OnClick(R.id.bell_button)
    public void playBellSound () {
        mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.bell_sound);
        mPlayer.start();//Start playing the music
        makeVolumeToast();
    }

    @OnClick(R.id.baby_button)
    public void playBabySound () {
        mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.baby_sound);
        mPlayer.start();//Start playing the music
        makeVolumeToast();
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @OnClick(R.id.take_picture)
    public void capturePic() {
        if (mCameraView != null)
            mCameraView.capturePicture();
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



    @OnClick(R.id.last_captured_image)
    public void showImages() {
        Intent intent = new Intent(this, ShowAppImages.class);
        startActivity(intent);
    }

    @OnClick(R.id.imageView)
    public void openVideo() {
        Intent intent = new Intent(this, CaptureVideo.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.settings_wheel)
    public void openInAppPurchasesActivity(){
      //  bp.purchase(CaptureImage.this, "android.test.purchased");

        Intent intent = new Intent(this, InAppPurchases.class);
        startActivity(intent);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCameraView.getFacing() == Facing.FRONT) {
            outState.putString(CAMERA_FACING_MODE, CAMERA_MODE_FRONT);
        }
    }

}
