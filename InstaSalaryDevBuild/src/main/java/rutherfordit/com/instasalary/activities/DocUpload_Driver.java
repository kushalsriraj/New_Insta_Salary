package rutherfordit.com.instasalary.activities;

import android.annotation.SuppressLint;
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
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import com.crystal.crystalpreloaders.widgets.CrystalPreloader;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.Urls;

public class DocUpload_Driver extends AppCompatActivity {

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    CrystalPreloader loader_rc_back, loader_rc_front, loader_licence_front, loader_licence_back, loader_bankstatement1_driver, loader_bankstatement2_driver;
    ImageView driver_bankstatement1, driver_bankstatement2, driver_licence_front, driver_licence_back, driver_rc_front, driver_rc_back;
    TextView driver_bankstatement1_text, driver_bankstatement2_text, driver_licence_front_text, driver_licence_back_text, driver_rc_front_text, driver_rc_back_text;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    View view, view1, view2;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout upload_pdf, upload_from_camera, upload_from_gallery;
    Uri imguri;
    String mSelectedDocFile, Pdf_name;
    String timestamp, filename;
    boolean statement1, statement2, rcfront, rcback, licencefront, licenceback = false;
    RelativeLayout Submit_driver_proofs;
    private int REQUEST_CODE_PERMISSIONS = 1000;
    private String status = "";
    private int Request_employee_idcard_camera = 1;
    private int Request_employee_id_gallery = 2;

    public static void openPermissionSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_upload__driver);

        if (allPermissionsGranted()) {
            init();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

    }

    private void init() {

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        view = getLayoutInflater().inflate(R.layout.bottom_picker_driver, null);
        bottomSheetDialog = new BottomSheetDialog(DocUpload_Driver.this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);

        loader_rc_back = findViewById(R.id.loader_rc_back);
        loader_rc_front = findViewById(R.id.loader_rc_front);
        loader_licence_front = findViewById(R.id.loader_licence_front);
        loader_licence_back = findViewById(R.id.loader_licence_back);
        loader_bankstatement1_driver = findViewById(R.id.loader_bankstatement1_driver);
        loader_bankstatement2_driver = findViewById(R.id.loader_bankstatement2_driver);

        driver_bankstatement1 = findViewById(R.id.driver_bankstatement1);
        driver_bankstatement2 = findViewById(R.id.driver_bankstatement2);
        driver_licence_front = findViewById(R.id.driver_licence_front);
        driver_licence_back = findViewById(R.id.driver_licence_back);
        driver_rc_front = findViewById(R.id.driver_rc_front);
        driver_rc_back = findViewById(R.id.driver_rc_back);
        driver_bankstatement1_text = findViewById(R.id.driver_bankstatement1_text);
        driver_bankstatement2_text = findViewById(R.id.driver_bankstatement2_text);
        driver_licence_front_text = findViewById(R.id.driver_licence_front_text);
        driver_licence_back_text = findViewById(R.id.driver_licence_back_text);
        driver_rc_front_text = findViewById(R.id.driver_rc_front_text);
        driver_rc_back_text = findViewById(R.id.driver_rc_back_text);
        Submit_driver_proofs = findViewById(R.id.Submit_driver_proofs);

        upload_pdf = view.findViewById(R.id.upload_pdf_driver);
        upload_from_camera = view.findViewById(R.id.upload_from_camera_driver);
        upload_from_gallery = view.findViewById(R.id.upload_from_gallery_driver);
        view1 = view.findViewById(R.id.view1_driver);
        view2 = view.findViewById(R.id.view2_driver);

        driver_bankstatement1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "bank1";
                upload_pdf.setVisibility(View.VISIBLE);
                view1.setVisibility(View.GONE);
                upload_from_camera.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                upload_from_gallery.setVisibility(View.GONE);
                bottomSheetDialog.show();
            }
        });

        driver_bankstatement2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "bank2";
                upload_pdf.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                bottomSheetDialog.show();
            }
        });

        driver_licence_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "licencefront";
                upload_pdf.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);
                upload_from_camera.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                upload_from_gallery.setVisibility(View.VISIBLE);
                bottomSheetDialog.show();
            }
        });

        driver_licence_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "licenceback";
                upload_pdf.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);
                upload_from_camera.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                upload_from_gallery.setVisibility(View.VISIBLE);
                bottomSheetDialog.show();
            }
        });

        driver_rc_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "rcfront";
                upload_pdf.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);
                bottomSheetDialog.show();
            }
        });

        driver_rc_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "rcback";
                upload_pdf.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);
                bottomSheetDialog.show();
            }
        });

        upload_from_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), Request_employee_idcard_camera);
                bottomSheetDialog.cancel();
            }
        });

        upload_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), Request_employee_id_gallery);
                bottomSheetDialog.cancel();
            }
        });

        upload_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent4 = new Intent(DocUpload_Driver.this, NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 1);
                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                bottomSheetDialog.cancel();
            }
        });

        Submit_driver_proofs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statement1 && rcback && rcfront && licenceback && licencefront) {
                    Toasty.success(getApplicationContext(), "Documents Uploaded..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("MESSAGE", "Success");
                    setResult(1000, intent);
                    finish();
                } else {
                    Toasty.info(getApplicationContext(), "Upload All Mandatory Documents to Proceed..", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                Toasty.warning(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                openPermissionSettings(DocUpload_Driver.this);
            }
        }
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

    private void uploadFile(Uri imguri, int Requestcode) {

        String code = "";

        if (status.equals("licencefront")) {
            loader_licence_front.setVisibility(View.VISIBLE);
            code = "10";
        } else if (status.equals("licenceback")) {
            loader_licence_back.setVisibility(View.VISIBLE);
            code = "10";
        } else if (status.equals("rcfront")) {
            loader_rc_front.setVisibility(View.VISIBLE);
            code = "10";
        } else if (status.equals("rcback")) {
            loader_rc_back.setVisibility(View.VISIBLE);
            code = "10";
        }

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        File file = new File(getRealPathFromURI(imguri));

        try {
            builder.addFormDataPart("proof[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        builder.addFormDataPart("proof_type", code);

        RequestBody requestBody = builder.build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Urls.UPLOAD_IMAGE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", UserAccessToken)
                .post(requestBody)
                .build();


        OkHttpClient client = new OkHttpClient.Builder().build();
        okhttp3.Call call = client.newCall(request);
        String finalCode = code;
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                if (finalCode.equals("10")) {
                    if (status.equals("licencefront")) {
                        licencefront = true;

                        runOnUiThread(new Runnable() {
                            public void run() {

                                driver_licence_front.setPadding(20, 20, 20, 20);
                                driver_licence_front.setScaleType(ImageView.ScaleType.FIT_XY);
                                driver_licence_front.setImageURI(imguri);
                                driver_licence_front_text.setText(filename + ".png");
                                loader_licence_front.setVisibility(View.GONE);
                                Toasty.success(getApplicationContext(), "Licence Front Uploaded", Toast.LENGTH_SHORT).show();

                                if (statement1 && rcback && rcfront && licenceback && licencefront) {
                                    Submit_driver_proofs.setBackgroundColor(Color.parseColor("#D81B60"));
                                } else {
                                    Submit_driver_proofs.setBackgroundColor(Color.parseColor("#36000000"));
                                }
                            }
                        });
                    } else if (status.equals("licenceback")) {
                        licenceback = true;

                        runOnUiThread(new Runnable() {
                            public void run() {

                                driver_licence_back.setPadding(20, 20, 20, 20);
                                driver_licence_back.setScaleType(ImageView.ScaleType.FIT_XY);
                                driver_licence_back.setImageURI(imguri);
                                driver_licence_back_text.setText(filename + ".png");
                                loader_licence_back.setVisibility(View.GONE);
                                Toasty.success(getApplicationContext(), "Licence Back Uploaded", Toast.LENGTH_SHORT).show();

                                if (statement1 && rcback && rcfront && licenceback && licencefront) {
                                    Submit_driver_proofs.setBackgroundColor(Color.parseColor("#D81B60"));
                                } else {
                                    Submit_driver_proofs.setBackgroundColor(Color.parseColor("#36000000"));
                                }
                            }
                        });
                    } else if (status.equals("rcfront")) {
                        rcfront = true;

                        runOnUiThread(new Runnable() {
                            public void run() {

                                driver_rc_front.setPadding(20, 20, 20, 20);
                                driver_rc_front.setScaleType(ImageView.ScaleType.FIT_XY);
                                driver_rc_front.setImageURI(imguri);
                                driver_rc_front_text.setText(filename + ".png");
                                loader_rc_front.setVisibility(View.GONE);
                                Toasty.success(getApplicationContext(), "Rc Front Uploaded", Toast.LENGTH_SHORT).show();

                                if (statement1 && rcback && rcfront && licenceback && licencefront) {
                                    Submit_driver_proofs.setBackgroundColor(Color.parseColor("#D81B60"));
                                } else {
                                    Submit_driver_proofs.setBackgroundColor(Color.parseColor("#36000000"));
                                }
                            }
                        });
                    } else if (status.equals("rcback")) {
                        rcback = true;

                        runOnUiThread(new Runnable() {
                            public void run() {

                                driver_rc_back.setPadding(20, 20, 20, 20);
                                driver_rc_back.setScaleType(ImageView.ScaleType.FIT_XY);
                                driver_rc_back.setImageURI(imguri);
                                driver_rc_back_text.setText(filename + ".png");
                                loader_rc_back.setVisibility(View.GONE);
                                Toasty.success(getApplicationContext(), "Rc Back Uploaded", Toast.LENGTH_SHORT).show();

                                if (statement1 && rcback && rcfront && licenceback && licencefront) {
                                    Submit_driver_proofs.setBackgroundColor(Color.parseColor("#D81B60"));
                                } else {
                                    Submit_driver_proofs.setBackgroundColor(Color.parseColor("#36000000"));
                                }
                            }
                        });
                    }

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Request_employee_idcard_camera) {
            switch (resultCode) {
                case RESULT_OK:

                    if (data != null) {
                        Bitmap bitmap = data.getParcelableExtra("data");
                        assert bitmap != null;
                        getImageUri(getApplicationContext(), bitmap);
                        uploadFile(imguri, Request_employee_idcard_camera);
                    }
                    break;

                case RESULT_CANCELED:
                    break;
            }
        } else if (requestCode == Request_employee_id_gallery) {
            switch (resultCode) {
                case RESULT_OK:

                    if (data != null) {

                        Uri selectedImage = data.getData();
                        assert selectedImage != null;
                        File file = new File(Objects.requireNonNull(selectedImage.getPath()));
                        filename = file.getName();
                        uploadFile(selectedImage, Request_employee_id_gallery);
                    }
                    break;

                case RESULT_CANCELED:
                    break;
            }
        } else if (requestCode == Constant.REQUEST_CODE_PICK_FILE) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                assert list != null;
                NormalFile Url = (list.get(0));

                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                mSelectedDocFile = Url.getPath();
                Pdf_name = Url.getName();

                uploadpdf(status);

            } else if (resultCode == RESULT_CANCELED) {
                Toasty.info(getApplicationContext(), "Cancelled..", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void uploadpdf(String status) {

        String code = "";

        if (status.equals("bank1")) {
            loader_bankstatement1_driver.setVisibility(View.VISIBLE);
            code = "6";
        } else if (status.equals("bank2")) {
            loader_bankstatement2_driver.setVisibility(View.VISIBLE);
            code = "6";
        }

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (mSelectedDocFile != null) {

            File file = new File(mSelectedDocFile);

            try {
                final MediaType MEDIA_TYPE = MediaType.parse((mSelectedDocFile));
                builder.addFormDataPart("proof[]", file.getName(), RequestBody.create(MediaType.parse(String.valueOf(MEDIA_TYPE)), file));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        builder.addFormDataPart("proof_type", code);

        RequestBody requestBody = builder.build();

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Urls.UPLOAD_IMAGE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", UserAccessToken)
                .post(requestBody)
                .build();


        OkHttpClient client = new OkHttpClient.Builder().build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e("httpresponse", "onFailure: " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                assert response.body() != null;
                String body = response.body().string();
                Log.e("httpresponse", "onResponse: " + body);

                if (status.equals("bank1")) {
                    runOnUiThread(new Runnable() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void run() {
                            statement1 = true;
                            driver_bankstatement1_text.setText(Pdf_name);
                            driver_bankstatement1.setImageDrawable(getResources().getDrawable(R.drawable.pdfseticon));
                            loader_bankstatement1_driver.setVisibility(View.GONE);
                            Toasty.success(getApplicationContext(), "Statement 1 Uploaded", Toast.LENGTH_SHORT).show();

                            if (statement1 && rcback && rcfront && licenceback && licencefront) {
                                Submit_driver_proofs.setBackgroundColor(Color.parseColor("#D81B60"));
                            } else {
                                Submit_driver_proofs.setBackgroundColor(Color.parseColor("#36000000"));
                            }
                        }
                    });
                } else if (status.equals("bank2")) {
                    runOnUiThread(new Runnable() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void run() {
                            statement2 = true;
                            driver_bankstatement2_text.setText(Pdf_name);
                            driver_bankstatement2.setImageDrawable(getResources().getDrawable(R.drawable.pdfseticon));
                            loader_bankstatement2_driver.setVisibility(View.GONE);
                            Toasty.success(getApplicationContext(), "Statement 2 Uploaded", Toast.LENGTH_SHORT).show();

                            if (statement1 && rcback && rcfront && licenceback && licencefront) {
                                Submit_driver_proofs.setBackgroundColor(Color.parseColor("#D81B60"));
                            } else {
                                Submit_driver_proofs.setBackgroundColor(Color.parseColor("#36000000"));
                            }
                        }
                    });
                } else if (status.equals("")) {
                    Toasty.info(getApplicationContext(), "Status is empty in pdf", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void getImageUri(Context inContext, Bitmap inImage) {

        long tsLong = System.currentTimeMillis() / 1000;
        filename = Long.toString(tsLong);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, filename, null);
        imguri = Uri.parse(path);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Toasty.error(getApplicationContext(), "Action Denied..", Toast.LENGTH_SHORT).show();
    }
}