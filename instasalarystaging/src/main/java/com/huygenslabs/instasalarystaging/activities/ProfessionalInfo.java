package com.huygenslabs.instasalarystaging.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.loader.content.CursorLoader;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.huygenslabs.instasalarystaging.R;
import com.huygenslabs.instasalarystaging.extras.MySingleton;
import com.huygenslabs.instasalarystaging.extras.Urls;
import com.huygenslabs.instasalarystaging.model.VendorModel;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ProfessionalInfo extends AppCompatActivity {

    TextInputEditText entercompanyname, enterjobrole, enterexp,entercompanyemail,entercompanydoorno,entercompanystreet,entercompanycity,entercompanystate,entercompanypincode;
    RelativeLayout salariedprofsubmit;
    String Id = "";
    TextInputEditText entervehicleno, entervendor, enteravgincome;
    RelativeLayout ownerprofsubmit;
    boolean click = false;
    Spinner vendorspinner;
    SharedPreferences sharedPreferences;
    String UserAccessToken,Vendor;
    private ArrayList<VendorModel> vendorModelArrayList;
    private ArrayList<String> names,ids;
    ProgressDialog progressBar;
    Button upload_driving_licence,upload_emp_idcard,bankstatement_professional;
    private int Request_driving_front_image = 1;
    Uri imguri;
    ImageView licence_image,idcard_image;
    boolean licence_uploaded = false;
    private int Request_employee_idcard = 2;
    boolean idcard_uploaded = false;
    boolean status = false;
    String mSelectedDocFile,Pdf_name;
    TextView pdf_file;
    CardView card_pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String getvalue = getIntent().getStringExtra("segment");
        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        assert getvalue != null;

        if (getvalue.equals("salaried"))
        {
            setContentView(R.layout.activity_professional_info);
            initsalaried();
        }
        else if (getvalue.equals("owner"))
        {
            setContentView(R.layout.driver_professional_info);
            initowner();
        }

    }

    public void getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "", null);
        imguri =  Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Request_driving_front_image) {

            switch (resultCode) {
                case RESULT_OK:

                    if (data != null) {
                        Bitmap bitmap = data.getParcelableExtra("data");
                        assert bitmap != null;
                        getImageUri(getApplicationContext(), bitmap);
                        uploadFile(imguri, Request_driving_front_image);
                        licence_image.setVisibility(View.VISIBLE);
                        licence_image.setImageURI(imguri);
                     //   progressDialog.cancel();
                    }
                    break;

                case RESULT_CANCELED:
                    break;
            }
        }

        else if (requestCode == Request_employee_idcard) {

            switch (resultCode) {
                case RESULT_OK:

                    if (data != null) {
                        Bitmap bitmap = data.getParcelableExtra("data");
                        assert bitmap != null;
                        getImageUri(getApplicationContext(), bitmap);
                        uploadFile(imguri, Request_employee_idcard);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run()
                            {
                                idcard_image.setVisibility(View.VISIBLE);
                                idcard_image.setImageURI(imguri);
                                upload_emp_idcard.setText("Re Upload");
                            }
                        });


                        //   progressDialog.cancel();
                    }
                    break;

                case RESULT_CANCELED:
                    break;
            }
        }
        else if (requestCode == Constant.REQUEST_CODE_PICK_FILE)
        {
            if (resultCode == RESULT_OK)
            {

                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                NormalFile Url = (list.get(0));

                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                mSelectedDocFile = Url.getPath();
                Pdf_name = Url.getName();

                uploadpdf();

            }
        }

    }

    private void uploadpdf()
    {

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

        builder.addFormDataPart("proof_type", "6");

        RequestBody requestBody = builder.build();

       // String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIzIiwianRpIjoiNDM3YjQ1ZWM1OTQ3OWMzZjg1OWM4OTVkN2UzYmE2YTY4N2Q0YTFkNTU1ZWQ3YjNhNjEwNzE5MGJmMDkwZTAwYTQ5NWRhNGNmNjY0Y2JmMzEiLCJpYXQiOjE1OTU4MzExOTMsIm5iZiI6MTU5NTgzMTE5MywiZXhwIjoxNjI3MzY3MTkzLCJzdWIiOiIxNSIsInNjb3BlcyI6W119.Qo52jc9yDYViigruiW0CIz7qtBio5sM7FYJNdUEKfRJ6JvciNdZKDahs0nitwe1d3Fu-GEklhdyxM43lBNOQtxXD4DIOcYZZYKcqceZjtdmiI3FBQBj8RVJQw15hNPxmzleyf4_ioIeNjyy4vtR-FpwpreEJjVb_rvxMoERrtzZcxNS_5RfxEFjqgSz9epdeDzT2Gvt6YFNw7sqjgYwESMMZXwt96fXYjpExCpnmCMVcSyCYacm5tS7SV6j5T3UUEi9iroFe1JSYoM2mKsmNivxaPnAVfn-ZnY7aCUtcg0MsGIdERm0z36HNZdrxTxNRmiZxRmIBW0Fd4XkTTFI-I_0Ozk32rEMpWN9LmM_HTgQxpvqif1z4u5MpNNAb9zG7oWp19kVGTzrAS_7XPKlKUcJV9xD7a4IufGWhqbldEQH-QLHdYz1FST9rSP0U3QGIpKL4t_BCz9bn4W7VqHKFeZ3duhVQlNteJTrC-MVzsk4kMNPvqPI4WX5MaTLQhOvHiM5968kJax3qybixTB6uFkChjEIr9OAX1dsAE3fdTUYsHdW0A8z2MARj6OsTE-sfOVMr80aiK_c0ASF6a-Z_Z18kVN6SFA87yQZfMwyRo8WqRg0tU2K6WypLYaB44DtNqmRbpQVPaoe_N6L1g_qzHq1z59BfMQJTdt02gU7SZ98";
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
                e.printStackTrace();

                progressBar.cancel();
                Log.e("httpresponse", "onFailure: " + e.getLocalizedMessage() );
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                progressBar.cancel();

                assert response.body() != null;
                String body = response.body().string();

                Log.e("httpresponse", "onResponse: " + body);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        card_pdf.setVisibility(View.VISIBLE);
                        pdf_file.setText(Pdf_name);
                        bankstatement_professional.setVisibility(View.GONE);

                    }
                });
            }
        });

    }

    private void uploadFile(Uri imguri,int Requestcode)
    {

        String code = "";

        if (Requestcode == Request_driving_front_image)
        {
            code = "10";
        }
        else if (Requestcode == Request_employee_idcard)
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

                if (finalCode.equals("10"))
                {
                    licence_uploaded = true;

                    if (entervehicleno.getText().toString().length() > 0 && entervendor.getText().toString().length() > 0 &&
                            enteravgincome.getText().toString().length() > 4 && !Vendor.equals("-- Select Vendor --") && licence_uploaded)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ownerprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                                click = true;
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ownerprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                                click = false;
                            }
                        });
                    }
                }

                else if (finalCode.equals("12"))
                {

                    idcard_uploaded = true;

                    if (entercompanyname.getText().toString().length() > 0
                            && enterjobrole.getText().toString().length() > 0
                            && enterexp.getText().toString().length() > 0
                            && entercompanyemail.getText().toString().length() > 0
                            && entercompanydoorno.getText().toString().length() > 0
                            && entercompanystreet.getText().toString().length() > 0
                            && entercompanycity.getText().toString().length() > 0
                            && entercompanystate.getText().toString().length() > 0
                            && entercompanypincode.getText().toString().length() > 0
                            && idcard_uploaded)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                                status = true;
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                                status = false;
                            }
                        });
                    }
                }
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

    private void initowner()
    {

        progressBar = new ProgressDialog(this);
        entervehicleno = findViewById(R.id.entervehicleno);
        entervehicleno.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        entervendor = findViewById(R.id.entervendor);
        enteravgincome = findViewById(R.id.enteravgincome);
        ownerprofsubmit = findViewById(R.id.ownerprofsubmit);
        vendorspinner = findViewById(R.id.vendorspinner);
        vendorModelArrayList = new ArrayList<>();
        names = new ArrayList<String>();
        names.add("-- Select Vendor --");
        ids = new ArrayList<String>();
        upload_driving_licence = findViewById(R.id.upload_driving_licence);
        licence_image = findViewById(R.id.licence_image);
        licence_image.setVisibility(View.GONE);

        requestvendordata();

        entervendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vendorspinner.performClick();
            }
        });

        vendorspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                Vendor = parent.getItemAtPosition(pos).toString();

                        if (!Vendor.equals("-- Select Vendor --"))
                        {

                            Id = vendorModelArrayList.get(pos-1).getId();

                            ownerprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                            ownerprofsubmit.setEnabled(true);
                            click = true;
                        }
                        else
                        {

                            ownerprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                            ownerprofsubmit.setEnabled(false);
                            click = false;
                        }

                        entervendor.setText(Vendor);
                        entervendor.setCursorVisible(false);

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        entervehicleno.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entervehicleno.getText().toString().length() > 0 && entervendor.getText().toString().length() > 0 &&
                        enteravgincome.getText().toString().length() > 4 && !Vendor.equals("-- Select Vendor --") && licence_uploaded) {
                    ownerprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    ownerprofsubmit.setEnabled(true);
                    click = true;
                } else {
                    ownerprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    ownerprofsubmit.setEnabled(false);
                    click = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enteravgincome.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entervehicleno.getText().toString().length() > 0 && entervendor.getText().toString().length() > 0 &&
                        enteravgincome.getText().toString().length() > 0 && !Vendor.equals("-- Select Vendor --") && licence_uploaded) {
                    ownerprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    ownerprofsubmit.setEnabled(true);
                    click = true;
                } else {
                    ownerprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    ownerprofsubmit.setEnabled(false);
                    click = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        ownerprofsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (click)
                {
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Please Wait..");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    ownerrequest();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please upload image also",Toast.LENGTH_SHORT).show();
                }
            }
        });

        upload_driving_licence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), Request_driving_front_image);
            }
        });

    }

    private void requestvendordata()
    {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Urls.VENDORS_LIST, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {

                try {

                    JSONArray data = response.getJSONArray("data");

                    for (int i = 0 ; i < data.length() ; i++ )
                    {

                        JSONObject dataobj = data.getJSONObject(i);

                        VendorModel Model = new VendorModel();

                        Model.setId(dataobj.getString("id"));
                        Model.setName(dataobj.getString("name"));
                        Model.setPhone(dataobj.getString("phone_number"));
                        Model.setAddress(dataobj.getString("address"));
                        Model.setJoinedon(dataobj.getString("joined_on"));

                        vendorModelArrayList.add(Model);

                        names.add(dataobj.getString("name"));

                        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, names);

                        vendorspinner.setAdapter(aa);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization",UserAccessToken);
                return params;
            }
        };

        MySingleton.getInstance(ProfessionalInfo.this).addToRequestQueue(jsonObjectRequest);

    }

    private void ownerrequest()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("vehicle_number",entervehicleno.getText().toString());
            jsonObject.put("vendor_id",Id);
            jsonObject.put("avg_income",enteravgincome.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("obj", "ownerrequest: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.OWNER_PROF, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("owner_response", "onResponse: " + response );

                if(response!=null)
                {
                    progressBar.cancel();
                    Intent i = new Intent(getApplicationContext(), AddBankDetails.class);
                    startActivity(i);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("eeeeeee", "onErrorResponse: " + error.toString() );

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization",UserAccessToken);
                Log.e(":parama", "getHeaders: " + params );
                return params;
            }
        };

        MySingleton.getInstance(ProfessionalInfo.this).addToRequestQueue(jsonObjectRequest);
    }

    private void initsalaried()
    {

        progressBar = new ProgressDialog(this);
        entercompanyname = findViewById(R.id.entercompanyname);
        enterjobrole = findViewById(R.id.enterjobrole);
        enterexp = findViewById(R.id.enterexp);

        entercompanyemail = findViewById(R.id.entercompanyemail);
        entercompanydoorno = findViewById(R.id.entercompanydoorno);
        entercompanystreet = findViewById(R.id.entercompanystreet);
        entercompanycity = findViewById(R.id.entercompanycity);
        entercompanystate = findViewById(R.id.entercompanystate);
        entercompanypincode = findViewById(R.id.entercompanypincode);
        bankstatement_professional = findViewById(R.id.bankstatement_professional);
        card_pdf = findViewById(R.id.card_pdf);
        pdf_file = findViewById(R.id.pdf_file);
        card_pdf.setVisibility(View.GONE);

        salariedprofsubmit = findViewById(R.id.salariedprofsubmit);

        upload_emp_idcard = findViewById(R.id.upload_emp_idcard);
        idcard_image = findViewById(R.id.idcard_image);
        idcard_image.setVisibility(View.GONE);

        upload_emp_idcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), Request_employee_idcard);
            }
        });

        bankstatement_professional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                progressBar.show();
                Intent intent4 = new Intent(ProfessionalInfo.this, NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 1);

                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
            }
        });

        entercompanyname.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && idcard_uploaded) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                }
                else
                {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterjobrole.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && idcard_uploaded) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterexp.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && idcard_uploaded) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanyemail.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && idcard_uploaded) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanydoorno.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0
                        && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && idcard_uploaded) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanystreet.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && idcard_uploaded) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanycity.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && idcard_uploaded) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanystate.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && idcard_uploaded) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanypincode.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && idcard_uploaded) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        salariedprofsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status)
                {
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Please Wait..");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    salariedrequest();

                } else {
                    Toast.makeText(getApplicationContext(), "please fill the details properly", Toast.LENGTH_SHORT).show();
                }

            }
        });

        enterexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);


                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(ProfessionalInfo.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                int month = monthOfYear + 1;
                                String formattedMonth = "" + month;
                                String formattedDayOfMonth = "" + dayOfMonth;

                                if(month < 10){

                                    formattedMonth = "0" + month;
                                }
                                if(dayOfMonth < 10){

                                    formattedDayOfMonth = "0" + dayOfMonth;
                                }
                                enterexp.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    picker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.instapink));
                    picker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.instapink));
                }
                else
                {
                    picker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    picker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                }

            }
        });

    }

    private void salariedrequest()
    {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("company_name",entercompanyname.getText().toString());
            jsonObject.put("job_role",enterjobrole.getText().toString());
            jsonObject.put("working_since",enterexp.getText().toString());
            jsonObject.put("company_mail",entercompanyemail.getText().toString());
            jsonObject.put("door_no",entercompanydoorno.getText().toString());
            jsonObject.put("street",entercompanystreet.getText().toString());
            jsonObject.put("city",entercompanycity.getText().toString());
            jsonObject.put("state",entercompanystate.getText().toString());
            jsonObject.put("pincode",entercompanypincode.getText().toString());

            Log.e("salaried", "salariedrequest: " + jsonObject );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SALARIED_PROF, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if(response!=null)
                {
                    progressBar.cancel();
                    Intent i = new Intent(getApplicationContext(), AddBankDetails.class);
                    startActivity(i);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("eeeeeee", "onErrorResponse: " + error.toString() );
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization",UserAccessToken);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

}
