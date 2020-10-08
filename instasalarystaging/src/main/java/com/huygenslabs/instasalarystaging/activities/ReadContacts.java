package com.huygenslabs.instasalarystaging.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.huygenslabs.instasalarystaging.R;
import com.huygenslabs.instasalarystaging.extras.MySingleton;
import com.huygenslabs.instasalarystaging.extras.Urls;
import com.huygenslabs.instasalarystaging.model.SMSData;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ReadContacts extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 111;
    private static final String TAG = "COntacts";
    JSONArray jsonArray;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    TextView text;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_contacts);

        if (ContextCompat.checkSelfPermission(ReadContacts.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ReadContacts.this, Manifest.permission.READ_CONTACTS))
            {
                init();
            }
            else
            {
                ActivityCompat.requestPermissions(ReadContacts.this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        else
        {
            init();
        }

    }

    private void init() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading Contacts..");
        progressDialog.show();

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        loadcontacts();

    }

    private void loadcontacts()
    {

        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        List<ContactsModel> userList = new ArrayList<>();

        String lastPhoneName = " ";

        if (phones.getCount() > 0) {
            while (phones.moveToNext()) {

                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contactId = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                String photoUri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                if (!name.equalsIgnoreCase(lastPhoneName)) {
                    lastPhoneName = name;
                    ContactsModel user = new ContactsModel();
                    user.setContactName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    user.setContactNumber(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    userList.add(user);

                    Log.d(TAG, ""+ userList);
                }

                makejson(userList);
            }
        }
        else
        {
            {
                Toast.makeText(getApplicationContext(),"No Contacts Found",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),SignUpDetails.class);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                progressDialog.cancel();

            }
        }
        phones.close();

    }

    private void makejson(List<ContactsModel> userList)
    {

        jsonArray = new JSONArray();

        for(int i = 0 ; i < userList.size() ; i++){

            JSONObject jsonObject = new JSONObject();

            String name = userList.get(i).getContactName();
            String number = userList.get(i).getContactNumber();


            try {

                jsonObject.put("contact_name",name);
                jsonObject.put("contact_number",number);

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);

        }

        Log.d(TAG, "getContactsArray: " + jsonArray );

        request(jsonArray);

    }

    private void request(JSONArray jsonArray)
    {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("contact",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "OBJ: " + jsonObject);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.CONTACTS_DATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response!=null)
                {
                    Toast.makeText(getApplicationContext(),"Contacts Backup Successfull..",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(),SignUpDetails.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    progressDialog.cancel();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "onErrorResponse: " + error.getMessage());

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

        MySingleton.getInstance(ReadContacts.this).addToRequestQueue(jsonObjectRequest);

    }

    public static void openPermissionSettings(Activity activity)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                init();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Permission IS Denied", Toast.LENGTH_LONG).show();
                openPermissionSettings(ReadContacts.this);
            }
        }
    }

}