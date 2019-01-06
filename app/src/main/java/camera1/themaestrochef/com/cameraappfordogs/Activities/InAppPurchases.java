package camera1.themaestrochef.com.cameraappfordogs.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import camera1.themaestrochef.com.cameraappfordogs.R;

public class InAppPurchases extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    public static BillingProcessor bp;
     public static Boolean adsDisabled = false;



    @BindView(R.id.no_ads_button)
    Button noAdsButton;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_purchases);
        activity = this;
        ButterKnife.bind(this);

        bp = new BillingProcessor(this,null,this);
        bp.initialize();


    }




    @OnClick(R.id.no_ads_button)
    public void purchaseNoAds() {
        Toast.makeText(this, "Tried to make purchase", Toast.LENGTH_SHORT ).show();
        bp.purchase(InAppPurchases.this, "android.test.purchased");
        adsDisabled = true;

    }


    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toast.makeText(this, "You've purchased something", Toast.LENGTH_SHORT ).show();
           // adsDisabled = false;

    }

    @Override
    public void onPurchaseHistoryRestored() {
        bp.isPurchased("android.test.purchased");


    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT ).show();

    }

    @Override
    public void onBillingInitialized() {

    }
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CaptureImage.class));
    }
}
