package com.huygenslabs.instasalarystaging.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huygenslabs.instasalarystaging.R;
import com.huygenslabs.instasalarystaging.extras.Urls;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AdharImageUpload extends AppCompatActivity {

    RelativeLayout AdharImagesubmit;
    RelativeLayout upload_adhar_front,upload_adhar_back;
    ImageView front_image,back_image;
    private static final int Request_Adhar_front_image = 1;
    private static final int Request_Adhar_back_image = 2;
    Uri imguri;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    String code = "";
    boolean frontuploaded = false;
    boolean backuploaded = false;
    String Adharno;
    LinearLayout layout_textback,layout_textfront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhar_image_upload);

        init();
    }

    private void init()
    {

        Adharno = getIntent().getStringExtra("adharno");

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");
        progressDialog = new ProgressDialog(this);
        AdharImagesubmit = findViewById(R.id.AdharImagesubmit);
        back_image = findViewById(R.id.back_image);
        back_image.setVisibility(View.GONE);
        front_image = findViewById(R.id.front_image);
        front_image.setVisibility(View.GONE);
        upload_adhar_front = findViewById(R.id.upload_adhar_front);
        upload_adhar_back = findViewById(R.id.upload_adhar_back);
        layout_textback = findViewById(R.id.layout_textback);
        layout_textback.setVisibility(View.VISIBLE);
        layout_textfront = findViewById(R.id.layout_textfront);
        layout_textfront.setVisibility(View.VISIBLE);


        upload_adhar_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), Request_Adhar_front_image);
            }
        });

        upload_adhar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), Request_Adhar_back_image);
            }
        });

        AdharImagesubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (frontuploaded && backuploaded)
                {
                    Intent i = new Intent(getApplicationContext(),SignUpDetails.class);
                    i.putExtra("adharno",Adharno);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please Upload Front And Back Side of Adhar Card..",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Request_Adhar_front_image) {

            switch (resultCode) {
                case RESULT_OK:

                    if (data != null)
                    {
                        Bitmap bitmap = data.getParcelableExtra("data");
                        assert bitmap != null;
                        getImageUri(getApplicationContext(),bitmap);
                        uploadFile(imguri,Request_Adhar_front_image);
                        front_image.setImageURI(imguri);
                        progressDialog.cancel();
                    }
                    break;

                case RESULT_CANCELED:
                    break;
            }
        }
        else if (requestCode == Request_Adhar_back_image) {

            switch (resultCode) {
                case RESULT_OK:

                    if (data != null)
                    {
                        Bitmap bitmap = data.getParcelableExtra("data");
                        assert bitmap != null;
                        getImageUri(getApplicationContext(),bitmap);
                        uploadFile(imguri,Request_Adhar_back_image);
                        back_image.setImageURI(imguri);
                        progressDialog.cancel();
                    }
                    break;

                case RESULT_CANCELED:
                    break;
            }
        }
        
    }

    private void uploadFile(Uri imguri,int Requestcode)
    {

        if (Requestcode == Request_Adhar_front_image)
        {
            code = "3";
        }
        else if (Requestcode == Request_Adhar_back_image)
        {
            code = "4";
        }

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        File file = new File(getRealPathFromURI(imguri));

        try {
            // final MediaType MEDIA_TYPE = MediaType.parse((mSelectedDocFile));
            builder.addFormDataPart("proof[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        builder.addFormDataPart("proof_type", code);


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
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                if (code.equals("3"))
                {

                    progressDialog.cancel();

                    if (frontuploaded && backuploaded)
                    {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                AdharImagesubmit.setBackgroundColor(Color.parseColor("#D81B60"));

                            }
                        });


                    }
                    else if (backuploaded)
                    {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                layout_textfront.setVisibility(View.GONE);
                                front_image.setVisibility(View.VISIBLE);
                                front_image.setImageURI(imguri);
                                frontuploaded = true;
                                upload_adhar_front.setBackgroundColor(Color.GREEN);
                                AdharImagesubmit.setBackgroundColor(Color.parseColor("#D81B60"));

                            }
                        });


                    }
                    else
                    {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                layout_textfront.setVisibility(View.GONE);
                                front_image.setVisibility(View.VISIBLE);
                                front_image.setImageURI(imguri);
                                String jsonData = response.body().toString();
                               // upload_adhar_front.setBackgroundColor(Color.GREEN);
                                frontuploaded = true;
                                AdharImagesubmit.setBackgroundColor(Color.parseColor("#36000000"));

                            }
                        });


                    }
                }
                else if (code.equals("4"))
                {

                    if (frontuploaded && backuploaded)
                    {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                AdharImagesubmit.setBackgroundColor(Color.parseColor("#D81B60"));

                            }
                        });


                    }
                    else if (frontuploaded)
                    {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                progressDialog.cancel();
                                String jsonData = response.body().toString();
                                back_image.setVisibility(View.VISIBLE);
                                back_image.setImageURI(imguri);
                                layout_textback.setVisibility(View.GONE);
                               // upload_adhar_back.setBackgroundColor(Color.GREEN);
                                backuploaded = true;
                                AdharImagesubmit.setBackgroundColor(Color.parseColor("#D81B60"));

                            }
                        });


                    }
                    else
                    {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                progressDialog.cancel();
                                back_image.setVisibility(View.VISIBLE);
                                back_image.setImageURI(imguri);
                                layout_textback.setVisibility(View.GONE);
                                String jsonData = response.body().toString();
                               // upload_adhar_back.setBackgroundColor(Color.GREEN);
                                backuploaded = true;
                                AdharImagesubmit.setBackgroundColor(Color.parseColor("#36000000"));

                            }
                        });


                    }
                }
            }
        });

    }

    public void getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "", null);
        imguri =  Uri.parse(path);
    }

    public static void openPermissionSettings(Activity activity)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
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
}