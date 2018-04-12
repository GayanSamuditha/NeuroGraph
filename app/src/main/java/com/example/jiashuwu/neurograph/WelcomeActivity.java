package com.example.jiashuwu.neurograph;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    private long exitTime;

    private String language = "";

    private int displayWidth;

    private int displayHeight;

    private int getStatusBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.d("heightheoght", String.valueOf(height));
        return height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TextScaleUtils.scaleTextSize(WelcomeActivity.this, Sharing.isScale);
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_welcome);

        Sharing.colour = "blue";

        Sharing.device_system_version_code = Build.VERSION.RELEASE;

        Sharing.device_model = Build.MODEL;

        Sharing.device_brand = Build.BRAND;

        Sharing.device_manufacturer = Build.MANUFACTURER;

        Sharing.device_product_name = Build.PRODUCT;

        Log.d("BUILDBUILD", Build.PRODUCT);

        WindowManager windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;

        Sharing.device_navigation_bar_height = getStatusBarHeight();

        Sharing.device_width_in_pixels = displayWidth;

        Sharing.device_height_in_pixels = displayHeight + Sharing.device_navigation_bar_height;

        // get the system language
        language = Locale.getDefault().toString();
        Log.d("language_code", language);
        switch (language.toLowerCase())
        {
            case "zh_CN": language = "Simplified Chinese";break;
            case "zh_TW": language = "Traditional Chinese";break;
            case "zh_HK": language = "Traditional Chinese";break;
            case "nl_NL": language = "Dutch";break;
            case "en_AU": language = "English";break;
            case "en_CA": language = "English";break;
            case "en_NZ": language = "English";break;
            case "en_GB": language = "English";break;
            case "en_US": language = "English";break;
            case "fr_FR": language = "French";break;
            case "de_DE": language = "German";break;
            case "it_IT": language = "Italian";break;
            case "ja_JP": language = "Japanese";break;
            case "pt_PT": language = "Portuguese";break;
            case "ru_RU": language = "Russian";break;
            case "es_ES": language = "Spanish";break;
            default: language = "English";break;
        }
        Sharing.language = language;
        Log.d("language", Sharing.language);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Sharing.isScale = false;
                Intent intent = new Intent(WelcomeActivity.this , SettingPageActivity.class);
                WelcomeActivity.this.startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 5000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else
            {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }





    @Override
    public void onStart ()
    {
        super.onStart();
    }

    @Override
    public void onRestart ()
    {
        super.onRestart();
    }

    @Override
    public void onResume ()
    {
        super.onResume();
    }

    @Override
    public void onPause ()
    {
        super.onPause();
    }

    @Override
    public void onStop ()
    {
        super.onStop();
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy();
    }






}
