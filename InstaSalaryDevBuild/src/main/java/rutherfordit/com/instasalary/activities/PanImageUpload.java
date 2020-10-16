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

public class PanImageUpload extends AppCompatActivity {

    private static final int Request_Pan = 1;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    RelativeLayout PanImagesubmit;
    CardView loader_panupload;
    LinearLayout layout_bottom_panupload;
    ImageView pan_image;
    Uri imguri;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    boolean Panupload = false;
    CardView pan_image_Card;
    PreviewView previewViewPan;
    RelativeLayout capture_image_pan, layout_cancel_pan, layout_correct_pan;
    private int Request_pan_image = 10;
    private Executor executor = Executors.newSingleThreadExecutor();
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private int REQUEST_CODE_PERMISSIONS = 1000;

    public static void openPermissionSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                init();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                openPermissionSettings(PanImageUpload.this);
            }
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
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Action Denied..", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_image_upload);

        if (allPermissionsGranted()) {
            init();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void init() {

        loader_panupload = findViewById(R.id.loader_panupload);
        loader_panupload.setVisibility(View.GONE);
        capture_image_pan = findViewById(R.id.capture_image_pan);
        previewViewPan = findViewById(R.id.previewViewPan);
        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");
        pan_image_Card = findViewById(R.id.pan_image_Card);
        pan_image_Card.setVisibility(View.VISIBLE);
        layout_correct_pan = findViewById(R.id.layout_correct_pan);
        layout_cancel_pan = findViewById(R.id.layout_cancel_pan);
        layout_bottom_panupload = findViewById(R.id.layout_bottom_panupload);
        PanImagesubmit = findViewById(R.id.PanImagesubmit);
        pan_image = findViewById(R.id.pan_image);
        pan_image.setVisibility(View.GONE);
        layout_bottom_panupload.setVisibility(View.GONE);
        startCamera();

        layout_cancel_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewViewPan.setVisibility(View.VISIBLE);
                capture_image_pan.setVisibility(View.VISIBLE);
                layout_bottom_panupload.setVisibility(View.GONE);
                pan_image.setVisibility(View.GONE);
                Panupload = false;
                PanImagesubmit.setBackgroundColor(Color.parseColor("#36000000"));
            }
        });

        layout_correct_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Panupload = true;
                loader_panupload.setVisibility(View.VISIBLE);
                uploadFile(imguri);
            }
        });

        PanImagesubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Panupload) {
                    Toast.makeText(getApplicationContext(), "Document Uploaded..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("MESSAGE", "Success");
                    setResult(1, intent);
                    finish();
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
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {

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

        preview.setSurfaceProvider(previewViewPan.createSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis, imageCapture);

        capture_image_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loader_panupload.setVisibility(View.VISIBLE);

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

                                getImageUri(previewViewPan.getBitmap());
                                pan_image.setImageURI(imguri);
                                previewViewPan.setVisibility(View.GONE);
                                pan_image.setVisibility(View.VISIBLE);
                                capture_image_pan.setVisibility(View.GONE);
                                layout_bottom_panupload.setVisibility(View.VISIBLE);
                                loader_panupload.setVisibility(View.GONE);

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

    public String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/imagess/Pan";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {
        }

        return app_folder_path;
    }

    public void getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), inImage, "", null);
        imguri = Uri.parse(path);
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

    private void uploadFile(Uri captured_image) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        File file = new File(getRealPathFromURI(captured_image));

        try {
            builder.addFormDataPart("proof[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        builder.addFormDataPart("proof_type", "2");


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

                        if (Panupload) {
                            loader_panupload.setVisibility(View.GONE);
                            PanImagesubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                        } else {
                            loader_panupload.setVisibility(View.GONE);
                            PanImagesubmit.setBackgroundColor(Color.parseColor("#36000000"));
                        }
                    }
                });

                String jsonData = response.body().string();
                Log.e("lodddddd", "onResponse: " + jsonData);

            }
        });

    }

}