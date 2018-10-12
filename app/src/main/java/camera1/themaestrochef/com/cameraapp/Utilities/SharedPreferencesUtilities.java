package camera1.themaestrochef.com.cameraapp.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesUtilities {
    private static SharedPreferences.Editor editor;
    private static SharedPreferences preferences;

    private static SharedPreferencesUtilities utilities;
    private static final String pinchKey = "pinch";
    private static final String flashKey = "flash";

    @SuppressLint("CommitPrefEdits")
    private SharedPreferencesUtilities(Context context) {
        if (preferences == null)
            preferences = context.getSharedPreferences("Settings", MODE_PRIVATE);
        if (editor == null)
            editor = preferences.edit();
    }

    public static void setPinch(Context context, boolean isEnabled) {
        initVars(context);
        editor.putBoolean(pinchKey, isEnabled).apply();

    }

    private static void initVars(Context context) {
        if (utilities == null) {
            utilities = new SharedPreferencesUtilities(context);
        }
    }

    public static void setFlash(Context context, int flashIndex) {
        initVars(context);
        editor.putInt(flashKey, flashIndex).apply();
    }

    public static int getFlashIndex(Context context) {
        initVars(context);
        return preferences.getInt(flashKey, 0);
    }

    public static boolean getPinchValue(Context context) {
        initVars(context);
        return preferences.getBoolean(pinchKey, true);


    }

}
