package rutherfordit.com.instasalary.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import rutherfordit.com.instasalary.extras.IntroSliderActivity;
import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.GpsTracker;

public class SplashScreenActivity extends AppCompatActivity {

    Handler handler;
    SharedPreferences sharedPreferences;
    String status;
    private int Request_location_permission = 100;
    ImageView logo;
    LinearLayout errorlayout;
    SharedPreferences sharedpreferences;
    String mypreference = "mySharedPreference";

    @Override
    protected void onRestart() {

        try
        {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                buildAlertMessageNoGps();
            }
            else
            {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_location_permission);
                }
                else
                {
                    if (isNetworkAvailable())
                    {
                        init();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please Connect To Internet..",Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        super.onRestart();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alert.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.instapink));
            alert.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.instapink));
        }
        else
        {
            alert.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            alert.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo);
        errorlayout = findViewById(R.id.errorlayout);

        try
        {

            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                buildAlertMessageNoGps();
            }
            else
            {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_location_permission);
                }
                else
                {
                    if (isNetworkAvailable())
                    {
                        init();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please Connect To Internet..",Toast.LENGTH_SHORT).show();
                    }
                }
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

        GpsTracker gpsTracker = new GpsTracker(SplashScreenActivity.this);

        if (gpsTracker.canGetLocation())
        {
            String latitude = String.valueOf(gpsTracker.getLatitude());
            String longitude = String.valueOf(gpsTracker.getLongitude());

            Log.e("TAG", "getLocation: " + latitude + "  " + longitude);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lat", latitude);
            editor.putString("longi", longitude);
            editor.apply();

        }
        else
        {
            gpsTracker.showSettingsAlert();
        }

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
                if (isNetworkAvailable())
                {
                    init();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please Connect To Internet..",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                // permission was not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_location_permission);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
