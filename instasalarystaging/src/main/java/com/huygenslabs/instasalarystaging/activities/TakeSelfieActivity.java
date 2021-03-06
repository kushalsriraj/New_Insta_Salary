package com.huygenslabs.instasalarystaging.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.huygenslabs.instasalarystaging.R;
import com.huygenslabs.instasalarystaging.extras.Urls;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class TakeSelfieActivity extends AppCompatActivity {

    private static final String TAG = "selfie_activity";
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    LinearLayout uploadLayout,cameraLayout;
    RelativeLayout cancelLayout,correctLayout;
    ImageView imageView,capture_image;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    Uri imguri;
    CardView previewcard;
    ProgressDialog progressDialog;
    PreviewView mPreviewView;
    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_selfie);

        if(allPermissionsGranted())
        {
            init();
        }
        else
        {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        

    }

    private void startCamera() 
    {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) { }
            }
        }, ContextCompat.getMainExecutor(this));
        
    }

    private void bindPreview(ProcessCameraProvider cameraProvider)
    {

        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
        ImageCapture.Builder builder = new ImageCapture.Builder();
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector))
        {
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);

        capture_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".png");
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                getImageUri(mPreviewView.getBitmap());
                                imageView.setImageURI(imguri);
                                mPreviewView.setVisibility(View.GONE);
                                previewcard.setVisibility(View.GONE);
                                imageView.setVisibility(View.VISIBLE);
                                capture_image.setVisibility(View.GONE);
                                uploadLayout.setVisibility(View.VISIBLE);

                            }
                        });
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        error.printStackTrace();
                    }
                });

            }
        });

    }

    public String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {  }

        return app_folder_path;
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private void init() {


        previewcard = findViewById(R.id.previewcard);
        mPreviewView = findViewById(R.id.previewView);
        imageView = findViewById(R.id.imageView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.setCancelable(false);


        uploadLayout=findViewById(R.id.layout_bottom);
        cancelLayout=findViewById(R.id.layout_cancel);
        correctLayout=findViewById(R.id.layout_correct);
        capture_image = findViewById(R.id.capture_image);

        imageView.setVisibility(View.GONE);
        mPreviewView.setVisibility(View.VISIBLE);
        previewcard.setVisibility(View.VISIBLE);
        capture_image.setVisibility(View.VISIBLE);
        uploadLayout.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        startCamera();
        onClicks();
    }

    private void onClicks() {

        correctLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                uploadFile(imguri);
            }
        });

        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.cancel();
                mPreviewView.setVisibility(View.VISIBLE);
                previewcard.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                capture_image.setVisibility(View.VISIBLE);
                uploadLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {

            switch (resultCode) {
                case RESULT_OK:

                    if (data != null)
                    {
                        Bitmap bitmap = data.getParcelableExtra("data");
                        assert bitmap != null;
                        getImageUri(bitmap);
                        imageView.setImageURI(imguri);
                        progressDialog.cancel();
                        cameraLayout.setVisibility(View.GONE);
                        uploadLayout.setVisibility(View.VISIBLE);
                    }
                    break;

                case RESULT_CANCELED:
                    Log.v(TAG, "Picture canceled! :(");
                    break;
            }
        }
    }

    private void uploadFile(Uri captured_image)
    {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        File file = new File(getRealPathFromURI(captured_image));

        try
        {
            builder.addFormDataPart("proof[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        builder.addFormDataPart("proof_type", "1");


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
                Log.e(TAG, "onFailure: " + e.getLocalizedMessage() );
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                progressDialog.cancel();
                String jsonData = response.body().string();

                Intent i = new Intent(getApplicationContext(),ReadSMS.class);
                startActivity(i);

                Log.e(TAG, "onResponse: " + jsonData );

            }
        });

    }

    private String getRealPathFromURI(Uri captured_image)
    {
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

    public void getImageUri( Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), inImage, "", null);
        imguri =  Uri.parse(path);
    }

    public static void openPermissionSettings(Activity activity)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS)
        {
            if(allPermissionsGranted())
            {
                init();
            }
            else
            {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                openPermissionSettings(TakeSelfieActivity.this);
            }
        }
    }

}

