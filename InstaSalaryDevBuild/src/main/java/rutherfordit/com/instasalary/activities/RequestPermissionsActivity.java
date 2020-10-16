package rutherfordit.com.instasalary.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.suke.widget.SwitchButton;

import rutherfordit.com.instasalary.R;

public class RequestPermissionsActivity extends AppCompatActivity {

    CardView loader_perms;
    SwitchButton cameraSwitch, contactsSwitch, smsSwitch, storageSwitch, locationSwitch;
    ImageView settings_camera, settings_contact, settings_sms, settings_storage, settings_location;
    boolean camera_granted = false;
    boolean contact_granted = false;
    boolean sms_granted = false;
    boolean storage_granted = false;
    boolean location_granted = false;
    RelativeLayout proceed_permissions;

    public void cameraPermission() {
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(getApplicationContext(), "Camera Permissions Granted..", Toast.LENGTH_SHORT).show();
                        cameraSwitch.setEnabled(false);
                        camera_granted = true;
                        if (location_granted && sms_granted && contact_granted && storage_granted) {
                            proceed_permissions.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(), "Camera Permissions Denied..", Toast.LENGTH_SHORT).show();
                        settings_camera.setVisibility(View.VISIBLE);
                        cameraSwitch.setVisibility(View.GONE);
                        camera_granted = false;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void contactPermission() {
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(getApplicationContext(), "Contact Permissions Granted..", Toast.LENGTH_SHORT).show();
                        contactsSwitch.setEnabled(false);
                        contact_granted = true;
                        if (camera_granted && location_granted && sms_granted && storage_granted) {
                            proceed_permissions.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(), "Contact Permissions Denied..", Toast.LENGTH_SHORT).show();
                        settings_contact.setVisibility(View.VISIBLE);
                        contactsSwitch.setVisibility(View.GONE);
                        contact_granted = false;

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void smsPermission() {
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.READ_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(getApplicationContext(), "SMS Permissions Granted..", Toast.LENGTH_SHORT).show();
                        smsSwitch.setEnabled(false);
                        sms_granted = true;
                        if (camera_granted && location_granted && contact_granted && storage_granted) {
                            proceed_permissions.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(), "SMS Permissions Denied..", Toast.LENGTH_SHORT).show();
                        settings_sms.setVisibility(View.VISIBLE);
                        smsSwitch.setVisibility(View.GONE);
                        sms_granted = false;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void storagePermission() {
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(getApplicationContext(), "Storage Permissions Granted..", Toast.LENGTH_SHORT).show();
                        storageSwitch.setEnabled(false);
                        storage_granted = true;
                        if (camera_granted && location_granted && sms_granted && contact_granted) {
                            proceed_permissions.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(), "Storage Permissions Denied..", Toast.LENGTH_SHORT).show();
                        settings_storage.setVisibility(View.VISIBLE);
                        storageSwitch.setVisibility(View.GONE);
                        storage_granted = false;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void locationPermission() {
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(getApplicationContext(), "Location Permissions Granted..", Toast.LENGTH_SHORT).show();
                        locationSwitch.setEnabled(false);
                        location_granted = true;
                        if (camera_granted && sms_granted && contact_granted && storage_granted) {
                            proceed_permissions.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(), "Location Permissions Denied..", Toast.LENGTH_SHORT).show();
                        settings_location.setVisibility(View.VISIBLE);
                        locationSwitch.setVisibility(View.GONE);
                        location_granted = false;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permissions);

        proceed_permissions = findViewById(R.id.proceed_permissions);
        proceed_permissions.setVisibility(View.GONE);
        cameraSwitch = findViewById(R.id.cameraSwitch);
        contactsSwitch = findViewById(R.id.contactsSwitch);
        smsSwitch = findViewById(R.id.smsSwitch);
        storageSwitch = findViewById(R.id.storageSwitch);
        locationSwitch = findViewById(R.id.locationSwitch);
        cameraSwitch.toggle();
        cameraSwitch.setShadowEffect(true);
        cameraSwitch.setEnableEffect(true);
        loader_perms = findViewById(R.id.loader_perms);
        loader_perms.setVisibility(View.GONE);
        settings_camera = findViewById(R.id.settings_camera);
        settings_camera.setVisibility(View.GONE);

        settings_contact = findViewById(R.id.settings_contact);
        settings_contact.setVisibility(View.GONE);


        settings_sms = findViewById(R.id.settings_sms);
        settings_sms.setVisibility(View.GONE);

        settings_storage = findViewById(R.id.settings_storage);
        settings_storage.setVisibility(View.GONE);


        settings_location = findViewById(R.id.settings_location);
        settings_location.setVisibility(View.GONE);

        settings_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please Grant Permission for Camera in Settings", Toast.LENGTH_SHORT).show();
                openPermissions();
            }
        });
        settings_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please Grant Permission for Contact in Settings", Toast.LENGTH_SHORT).show();
                openPermissions();
            }
        });
        settings_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please Grant Permission for SMS in Settings", Toast.LENGTH_SHORT).show();
                openPermissions();
            }
        });
        settings_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please Grant Permission for Storage in Settings", Toast.LENGTH_SHORT).show();
                openPermissions();
            }
        });
        settings_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please Grant Permission for Location in Settings", Toast.LENGTH_SHORT).show();
                openPermissions();
            }
        });

        checkForPermissions();

        cameraSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                cameraPermission();
            }
        });

        contactsSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                contactPermission();
            }
        });

        smsSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                smsPermission();
            }
        });

        storageSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                storagePermission();
            }
        });

        locationSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                locationPermission();
            }
        });

        proceed_permissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loader_perms.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                if (location_granted && sms_granted && contact_granted && storage_granted&& camera_granted)
                {
                    Intent i = new Intent(getApplicationContext(),ReadContacts.class);
                   // i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    loader_perms.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                else {
                  Toast.makeText(getApplicationContext(),"Please Grant All Permissions..",Toast.LENGTH_SHORT).show();
                    loader_perms.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });

    }

    private void checkForPermissions() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraSwitch.setChecked(false);
            cameraSwitch.setEnabled(true);
            camera_granted = false;
        } else {
            cameraSwitch.setChecked(true);
            cameraSwitch.setEnabled(false);
            cameraSwitch.setVisibility(View.VISIBLE);
            settings_camera.setVisibility(View.GONE);
            camera_granted = true;
            if (location_granted && sms_granted && contact_granted && storage_granted) {
                proceed_permissions.setVisibility(View.VISIBLE);
            }
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            contactsSwitch.setChecked(false);
            contactsSwitch.setEnabled(true);
            contact_granted = false;
        } else {
            contactsSwitch.setChecked(true);
            contactsSwitch.setEnabled(false);
            contactsSwitch.setVisibility(View.VISIBLE);
            settings_contact.setVisibility(View.GONE);
            contact_granted = true;
            if (camera_granted && location_granted && sms_granted && storage_granted) {
                proceed_permissions.setVisibility(View.VISIBLE);
            }
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            smsSwitch.setChecked(false);
            smsSwitch.setEnabled(true);
            sms_granted = false;
        } else {
            smsSwitch.setChecked(true);
            smsSwitch.setEnabled(false);
            smsSwitch.setVisibility(View.VISIBLE);
            settings_sms.setVisibility(View.GONE);
            sms_granted = true;
            if (camera_granted && location_granted && contact_granted && storage_granted) {
                proceed_permissions.setVisibility(View.VISIBLE);
            }
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            storageSwitch.setChecked(false);
            storageSwitch.setEnabled(true);
            storage_granted = false;
        } else {
            storageSwitch.setChecked(true);
            storageSwitch.setEnabled(false);
            storageSwitch.setVisibility(View.VISIBLE);
            settings_storage.setVisibility(View.GONE);
            storage_granted = true;
            if (camera_granted && location_granted && sms_granted && contact_granted) {
                proceed_permissions.setVisibility(View.VISIBLE);
            }
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationSwitch.setChecked(false);
            locationSwitch.setEnabled(true);
            location_granted = false;
        } else {
            locationSwitch.setChecked(true);
            locationSwitch.setEnabled(false);
            locationSwitch.setVisibility(View.VISIBLE);
            settings_location.setVisibility(View.GONE);
            location_granted = true;
            if (camera_granted && sms_granted && contact_granted && storage_granted) {
                proceed_permissions.setVisibility(View.VISIBLE);
            }
        }
    }

    private void openPermissions() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            checkForPermissions();
        }
    }
}