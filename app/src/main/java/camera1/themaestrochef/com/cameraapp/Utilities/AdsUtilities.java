package camera1.themaestrochef.com.cameraapp.Utilities;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AdsUtilities {

    public static void initAds(final AdView mAdView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                System.out.println("Error");
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("AdMob", "Code : " + errorCode);
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                System.out.println("Error");
                Log.i("AdMob", "ad opened");
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                System.out.println("Error");
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                System.out.println("Error");
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }
}
