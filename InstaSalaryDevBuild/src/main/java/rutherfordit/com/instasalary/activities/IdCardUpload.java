package rutherfordit.com.instasalary.activities;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class IdCardUpload extends AppCompatActivity {
    RelativeLayout Idsubmit;
    Button upload_idCard;
    ImageView id_image;
    private static final int Request_idCard = 1;
    Uri imguri;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    String code = "";
    boolean idcardupload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card_upload);

        init();
    }

    private void init() {

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");
        progressDialog = new ProgressDialog(this);
        Idsubmit = findViewById(R.id.Idsubmit);
        id_image = findViewById(R.id.id_image);
        upload_idCard = findViewById(R.id.upload_idCard);

        upload_idCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), Request_idCard);
            }
        });

        Idsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Idsubmit.isEnabled()){
                    Idsubmit.setEnabled(false);
                    Intent i = new Intent(getApplicationContext(), ProfessionalInfo.class);
                    startActivity(i);

                }


            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Request_idCard) {

            switch (resultCode) {
                case RESULT_OK:

                    if (data != null)
                    {
                        Bitmap bitmap = data.getParcelableExtra("data");
                        assert bitmap != null;
                        getImageUri(getApplicationContext(),bitmap);
                        uploadFile(imguri,Request_idCard);
                        id_image.setImageURI(imguri);
                        progressDialog.cancel();
                    }
                    break;

                case RESULT_CANCELED:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void uploadFile(Uri imguri, int Requestcode)
    {

        if (Requestcode == Request_idCard)
        {
            code = "12";
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

                if (code.equals("12"))
                {

                    if (idcardupload)
                    {
                        Idsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    }
                    else
                    {
                        progressDialog.cancel();
                        String jsonData = response.body().string();
                        upload_idCard.setBackgroundColor(Color.GREEN);
                        idcardupload = true;
                        Idsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    }
                }

            }
        });

    }

    public void getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
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