package rutherfordit.com.instasalary.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.loader.content.CursorLoader;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.Urls;

public class BackAdharImageUpload extends AppCompatActivity {

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    PreviewView previewViewAdhar_back;
    RelativeLayout capture_image_adhar_back;
    LinearLayout layout_bottom_adhar_backupload;
    RelativeLayout layout_cancel_adhar_back;
    RelativeLayout layout_correct_adhar_back;
    ImageView adhar_back_image;
    CardView loader_adharuploadback;
    RelativeLayout AdharbackImagesubmit;
    boolean backuploaded = false;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    Uri imguri;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private int REQUEST_CODE_PERMISSIONS = 1000;
    private Executor executor = Executors.newSingleThreadExecutor();

    public static void openPermissionSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_adhar_image_upload);

        if (allPermissionsGranted()) {
            init();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                init();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                openPermissionSettings(BackAdharImageUpload.this);
            }
        }
    }

    private void init() {

        loader_adharuploadback = findViewById(R.id.loader_adharuploadback);
        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");
        previewViewAdhar_back = findViewById(R.id.previewViewAdhar_back);
        previewViewAdhar_back.setVisibility(View.VISIBLE);
        adhar_back_image = findViewById(R.id.adhar_back_image);
        adhar_back_image.setVisibility(View.GONE);
        capture_image_adhar_back = findViewById(R.id.capture_image_adhar_back);
        capture_image_adhar_back.setVisibility(View.VISIBLE);
        layout_bottom_adhar_backupload = findViewById(R.id.layout_bottom_adhar_backupload);
        layout_bottom_adhar_backupload.setVisibility(View.GONE);
        layout_cancel_adhar_back = findViewById(R.id.layout_cancel_adhar_back);
        layout_correct_adhar_back = findViewById(R.id.layout_correct_adhar_back);
        AdharbackImagesubmit = findViewById(R.id.AdharbackImagesubmit);
        startCamera();

        layout_cancel_adhar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewViewAdhar_back.setVisibility(View.VISIBLE);
                capture_image_adhar_back.setVisibility(View.VISIBLE);
                layout_bottom_adhar_backupload.setVisibility(View.GONE);
                adhar_back_image.setVisibility(View.GONE);
                backuploaded = false;
                AdharbackImagesubmit.setBackgroundColor(Color.parseColor("#36000000"));
            }
        });

        layout_correct_adhar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backuploaded = true;
                loader_adharuploadback.setVisibility(View.VISIBLE);
                uploadFile(imguri);
            }
        });

        AdharbackImagesubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (backuploaded) {
                    Intent i = new Intent(getApplicationContext(), SignUpDetails.class);
                    startActivity(i);
                }
            }
        });

    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreviewbackcam(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreviewbackcam(ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
        ImageCapture.Builder builder = new ImageCapture.Builder();
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(previewViewAdhar_back.createSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis, imageCapture);

        capture_image_adhar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loader_adharuploadback.setVisibility(View.VISIBLE);

                v.startAnimation(buttonClick);

                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date()) + ".jpg");
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                getImageUri(previewViewAdhar_back.getBitmap());
                                adhar_back_image.setImageURI(imguri);
                                previewViewAdhar_back.setVisibility(View.GONE);
                                adhar_back_image.setVisibility(View.VISIBLE);
                                capture_image_adhar_back.setVisibility(View.GONE);
                                layout_bottom_adhar_backupload.setVisibility(View.VISIBLE);
                                loader_adharuploadback.setVisibility(View.GONE);

                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        Log.e("loddd", "onError: " + error.getLocalizedMessage());
                    }
                });
            }
        });
    }

    private void uploadFile(Uri captured_image) {

        String prooftype = "4";

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        File file = new File(getRealPathFromURI(captured_image));

        try {
            builder.addFormDataPart("proof[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        builder.addFormDataPart("proof_type", prooftype);


        RequestBody requestBody = builder.build();

        okhttp3.Request request = new Request.Builder()
                .url(Urls.UPLOAD_IMAGE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", UserAccessToken)
                .post(requestBody)
                .build();


        OkHttpClient client = new OkHttpClient.Builder().build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Log.e("loddd", "onFailure: " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        backuploaded = true;

                        if (backuploaded) {
                            loader_adharuploadback.setVisibility(View.GONE);
                            AdharbackImagesubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                        } else {
                            loader_adharuploadback.setVisibility(View.GONE);
                            AdharbackImagesubmit.setBackgroundColor(Color.parseColor("#36000000"));
                        }

                    }
                });

                String jsonData = response.body().string();
                Log.e("lodddddd", "onResponse: " + jsonData);

            }
        });
    }

    private String getRealPathFromURI(Uri captured_image) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, captured_image, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public void getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), inImage, "", null);
        imguri = Uri.parse(path);
    }

    public String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/imagess/Adhar";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {
        }

        return app_folder_path;
    }

}