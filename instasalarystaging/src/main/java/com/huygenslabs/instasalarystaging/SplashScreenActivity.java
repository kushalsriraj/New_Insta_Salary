package com.huygenslabs.instasalarystaging;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.huygenslabs.instasalarystaging.activities.LoginActivity;
import com.huygenslabs.instasalarystaging.activities.MainActivity;
import com.huygenslabs.instasalarystaging.activities.ReadSMS;
import com.huygenslabs.instasalarystaging.extras.GpsTracker;

public class SplashScreenActivity extends AppCompatActivity {

    Handler handler;
    SharedPreferences sharedPreferences;
    String status;
    private int Request_location_permission = 100;
    ImageView logo;
    LinearLayout errorlayout;

    public void getLocation()
    {

        GpsTracker gpsTracker = new GpsTracker(SplashScreenActivity.this);

        if (gpsTracker.canGetLocation())
        {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            Log.e("TAG", "getLocation: " + latitude + "  " + longitude);
        }
        else
        {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    protected void onRestart() {

        try
        {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Request_location_permission);
            }
            else
            {
                init();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        super.onRestart();
    }

    @Override
    protected void onResume() {


        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo);
        errorlayout = findViewById(R.id.errorlayout);

        try
        {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Request_location_permission);
            }
            else
            {
                init();
            }

        } catch (Exception e){
            e.printStackTrace();
        }

      //  Log.e("TAG", "onCreate: " + lat +"  "+ longi );

    }

    private void init()
    {

        errorlayout.setVisibility(View.GONE);
        logo.setVisibility(View.VISIBLE);
        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        status = sharedPreferences.getString("loggedin", "");

        getLocation();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (status.equals("true"))
                {
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(SplashScreenActivity.this, IntroSliderActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        if (requestCode == Request_location_permission){

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    init();
                }
                else
                {
                    // permission was not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
                    {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Request_location_permission);
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.message_no_storage_permission_snackbar), Snackbar.LENGTH_LONG);
                        snackbar.setAction(getResources().getString(R.string.settings), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
                        snackbar.show();
                    }
                }
        }
    }

    public static void openPermissionSettings(Activity activity)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public void openpermission(View view) {

        openPermissionSettings(SplashScreenActivity.this);
    }
}
