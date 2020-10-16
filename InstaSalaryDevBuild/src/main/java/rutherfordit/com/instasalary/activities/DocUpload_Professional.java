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

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.Urls;

public class DocUpload_Professional extends AppCompatActivity {

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    CrystalPreloader loader_idcard_back, loader_idcard_front, loader_payslip1, loader_payslip2, loader_payslip3, loader_bankstatement1, loader_bankstatement2;
    ImageView prof_id_front, prof_id_back, prof_payslip1, prof_payslip2, prof_payslip3, prof_bankstatement1, prof_bankstatement2;
    TextView prof_bankstatement1_text, prof_bankstatement2_text, prof_payslip1_text, prof_payslip2_text, prof_payslip3_text, prof_id_front_text, prof_id_back_text;
    BottomSheetDialog bottomSheetDialog;
    View view;
    LinearLayout upload_pdf, upload_from_camera, upload_from_gallery;
    String timestamp, filename;
    Uri imguri;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    boolean idfront, idback, payslip1, payslip2, payslip3, bankstatement1 = false;
    String status = "";
    View view1, view2;
    String mSelectedDocFile, Pdf_name;
    RelativeLayout Submit_professional_proofs;
    private int REQUEST_CODE_PERMISSIONS = 1000;
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
        setContentView(R.layout.activity_doc_upload);

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

    private void init() {

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        view = getLayoutInflater().inflate(R.layout.bottom_picker, null);
        bottomSheetDialog = new BottomSheetDialog(DocUpload_Professional.this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);

        Submit_professional_proofs = findViewById(R.id.Submit_professional_proofs);
        prof_id_front = findViewById(R.id.prof_id_front);
        prof_id_back = findViewById(R.id.prof_id_back);
        prof_payslip1 = findViewById(R.id.prof_payslip1);
        prof_payslip2 = findViewById(R.id.prof_payslip2);
        prof_payslip3 = findViewById(R.id.prof_payslip3);
        prof_bankstatement1 = findViewById(R.id.prof_bankstatement1);
        prof_bankstatement2 = findViewById(R.id.prof_bankstatement2);
        prof_bankstatement1_text = findViewById(R.id.prof_bankstatement1_text);
        prof_bankstatement2_text = findViewById(R.id.prof_bankstatement2_text);
        prof_payslip1_text = findViewById(R.id.prof_payslip1_text);
        prof_payslip2_text = findViewById(R.id.prof_payslip2_text);
        prof_payslip3_text = findViewById(R.id.prof_payslip3_text);
        prof_id_front_text = findViewById(R.id.prof_id_front_text);
        prof_id_back_text = findViewById(R.id.prof_id_back_text);

        upload_pdf = view.findViewById(R.id.upload_pdf);
        upload_from_camera = view.findViewById(R.id.upload_from_camera);
        upload_from_gallery = view.findViewById(R.id.upload_from_gallery);
        view1 = view.findViewById(R.id.view1);
        view2 = view.findViewById(R.id.view2);

        loader_bankstatement1 = findViewById(R.id.loader_bankstatement1);
        loader_bankstatement2 = findViewById(R.id.loader_bankstatement2);
        loader_idcard_back = findViewById(R.id.loader_idcard_back);
        loader_idcard_front = findViewById(R.id.loader_idcard_front);
        loader_payslip1 = findViewById(R.id.loader_payslip1);
        loader_payslip2 = findViewById(R.id.loader_payslip2);
        loader_payslip3 = findViewById(R.id.loader_payslip3);

        prof_bankstatement1.setOnClickListener(new View.OnClickListener() {
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

        prof_id_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                status = "idfront";
                upload_pdf.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);
                upload_from_camera.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                upload_from_gallery.setVisibility(View.VISIBLE);
                bottomSheetDialog.show();

            }
        });

        prof_id_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                status = "idback";
                upload_pdf.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);
                upload_from_camera.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                upload_from_gallery.setVisibility(View.VISIBLE);
                bottomSheetDialog.show();

            }
        });

        prof_payslip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "pay1";
                upload_pdf.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                upload_from_camera.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                upload_from_gallery.setVisibility(View.VISIBLE);
                bottomSheetDialog.show();
            }
        });

        prof_payslip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "pay2";
                upload_pdf.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                upload_from_camera.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                upload_from_gallery.setVisibility(View.VISIBLE);
                bottomSheetDialog.show();
            }
        });

        prof_payslip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "pay3";
                upload_pdf.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                upload_from_camera.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                upload_from_gallery.setVisibility(View.VISIBLE);
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

                Intent intent4 = new Intent(DocUpload_Professional.this, NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 1);
                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                bottomSheetDialog.cancel();
            }
        });

        Submit_professional_proofs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bankstatement1 && payslip1 && idfront) {
                    Toast.makeText(getApplicationContext(), "Documents Uploaded..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("MESSAGE", "Success");
                    setResult(1000, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Upload All Mandatory Documents to Proceed..", Toast.LENGTH_SHORT).show();
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

                        if (status.equals("idfront")) {
                            prof_id_front_text.setText(filename + ".png");
                        } else if (status.equals("idback")) {
                            prof_id_back_text.setText(filename + ".png");
                        }

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

            }
        }

    }

    private void uploadpdf(String status) {

        String code = "";

        if (status.equals("pay1")) {
            loader_payslip1.setVisibility(View.VISIBLE);
            code = "5";
        } else if (status.equals("pay2")) {
            loader_payslip2.setVisibility(View.VISIBLE);
            code = "5";
        } else if (status.equals("pay3")) {
            loader_payslip3.setVisibility(View.VISIBLE);
            code = "5";
        } else if (status.equals("bank1")) {
            loader_bankstatement1.setVisibility(View.VISIBLE);
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

                if (status.equals("pay1")) {
                    runOnUiThread(new Runnable() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void run() {
                            payslip1 = true;
                            prof_payslip1_text.setText(Pdf_name);
                            prof_payslip1.setImageDrawable(getResources().getDrawable(R.drawable.pdfseticon));
                            loader_payslip1.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Payslip 1 Uploaded", Toast.LENGTH_SHORT).show();

                            if (payslip1 && bankstatement1 && idfront) {
                                Submit_professional_proofs.setBackgroundColor(Color.parseColor("#D81B60"));
                            } else {
                                Submit_professional_proofs.setBackgroundColor(Color.parseColor("#36000000"));
                            }
                        }
                    });
                } else if (status.equals("pay2")) {
                    runOnUiThread(new Runnable() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void run() {
                            payslip2 = true;
                            prof_payslip2_text.setText(Pdf_name);
                            prof_payslip2.setImageDrawable(getResources().getDrawable(R.drawable.pdfseticon));
                            loader_payslip2.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Payslip 2 Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (status.equals("pay3")) {
                    runOnUiThread(new Runnable() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void run() {
                            payslip3 = true;
                            prof_payslip3_text.setText(Pdf_name);
                            prof_payslip3.setImageDrawable(getResources().getDrawable(R.drawable.pdfseticon));
                            loader_payslip3.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Payslip 3 Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (status.equals("bank1")) {
                    runOnUiThread(new Runnable() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void run() {
                            bankstatement1 = true;
                            prof_bankstatement1_text.setText(Pdf_name);
                            prof_bankstatement1.setImageDrawable(getResources().getDrawable(R.drawable.pdfseticon));
                            loader_bankstatement1.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Bank Statement 1 Uploaded", Toast.LENGTH_SHORT).show();

                            if (payslip1 && bankstatement1 && idfront) {
                                Submit_professional_proofs.setBackgroundColor(Color.parseColor("#D81B60"));
                            } else {
                                Submit_professional_proofs.setBackgroundColor(Color.parseColor("#36000000"));
                            }
                        }
                    });
                } else if (status.equals("")) {
                    Toast.makeText(getApplicationContext(), "Status is empty in pdf", Toast.LENGTH_SHORT).show();
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

        if (status.equals("idfront")) {
            loader_idcard_front.setVisibility(View.VISIBLE);
            code = "12";
        } else if (status.equals("idback")) {
            loader_idcard_back.setVisibility(View.VISIBLE);
            code = "12";
        } else if (status.equals("pay1")) {
            loader_payslip1.setVisibility(View.VISIBLE);
            code = "5";
        } else if (status.equals("pay2")) {
            loader_payslip2.setVisibility(View.VISIBLE);
            code = "5";
        } else if (status.equals("pay3")) {
            loader_payslip3.setVisibility(View.VISIBLE);
            code = "5";
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

                if (finalCode.equals("12")) {
                    if (status.equals("idfront")) {
                        idfront = true;

                        runOnUiThread(new Runnable() {
                            public void run() {

                                prof_id_front.setPadding(20, 20, 20, 20);
                                prof_id_front.setScaleType(ImageView.ScaleType.FIT_XY);
                                prof_id_front.setImageURI(imguri);
                                prof_id_front_text.setText(filename + ".png");
                                loader_idcard_front.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Id Front Uploaded", Toast.LENGTH_SHORT).show();

                                if (payslip1 && bankstatement1 && idfront) {
                                    Submit_professional_proofs.setBackgroundColor(Color.parseColor("#D81B60"));
                                } else {
                                    Submit_professional_proofs.setBackgroundColor(Color.parseColor("#36000000"));
                                }
                            }
                        });
                    } else if (status.equals("idback")) {
                        idback = true;

                        runOnUiThread(new Runnable() {
                            public void run() {

                                prof_id_back.setPadding(20, 20, 20, 20);
                                prof_id_back.setScaleType(ImageView.ScaleType.FIT_XY);
                                prof_id_back.setImageURI(imguri);
                                prof_id_back_text.setText(filename + ".png");
                                loader_idcard_back.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Id Back Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else if (finalCode.equals("5")) {
                    if (status.equals("pay1")) {
                        payslip1 = true;

                        runOnUiThread(new Runnable() {
                            public void run() {

                                prof_payslip1.setPadding(20, 20, 20, 20);
                                prof_payslip1.setScaleType(ImageView.ScaleType.FIT_XY);
                                prof_payslip1.setImageURI(imguri);
                                prof_payslip1_text.setText(filename + ".png");
                                loader_payslip1.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Payslip 1 uploaded", Toast.LENGTH_SHORT).show();

                                if (payslip1 && bankstatement1 && idfront) {
                                    Submit_professional_proofs.setBackgroundColor(Color.parseColor("#D81B60"));
                                } else {
                                    Submit_professional_proofs.setBackgroundColor(Color.parseColor("#36000000"));
                                }
                            }
                        });
                    } else if (status.equals("pay2")) {
                        payslip2 = true;

                        runOnUiThread(new Runnable() {
                            public void run() {

                                prof_payslip2.setPadding(20, 20, 20, 20);
                                prof_payslip2.setScaleType(ImageView.ScaleType.FIT_XY);
                                prof_payslip2.setImageURI(imguri);
                                prof_payslip2_text.setText(filename + ".png");
                                loader_payslip2.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Payslip 2 uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (status.equals("pay3")) {
                        payslip3 = true;

                        runOnUiThread(new Runnable() {
                            public void run() {

                                prof_payslip3.setPadding(20, 20, 20, 20);
                                prof_payslip3.setScaleType(ImageView.ScaleType.FIT_XY);
                                prof_payslip3.setImageURI(imguri);
                                prof_payslip3_text.setText(filename + ".png");
                                loader_payslip3.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Payslip 3 uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                init();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                openPermissionSettings(DocUpload_Professional.this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Toast.makeText(getApplicationContext(), "Action Denied..", Toast.LENGTH_SHORT).show();
    }

}