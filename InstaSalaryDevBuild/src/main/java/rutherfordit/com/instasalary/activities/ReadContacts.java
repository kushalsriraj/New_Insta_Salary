package rutherfordit.com.instasalary.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.IntroSliderActivity;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;

public class ReadContacts extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 111;
    private static final String TAG = "COntacts";
    JSONArray jsonArray;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    List<ContactsModel> contactsModel;
    CardView loader_contacts;
    RelativeLayout rl_layout;
    Handler handler;

    public static void openPermissionSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    /*@Override
    protected void onRestart() {

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                if (isNetworkAvailable())
                {
                    init();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Connect To Internet..", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onRestart();
    }*/

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_contacts);

         if (isNetworkAvailable()) {

             if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
             {
                // init();
                 handler = new Handler();
                 handler.postDelayed(new Runnable() {
                     @Override
                     public void run() {

                         init();
                     }
                 }, 2000);
             }



                //
                } else {
                    Toast.makeText(getApplicationContext(), "Please Connect To Internet..", Toast.LENGTH_SHORT).show();
                }

    }

    private void init() {

        rl_layout = findViewById(R.id.rl_layout);
        rl_layout.setVisibility(View.GONE);
        loader_contacts = findViewById(R.id.loader_contacts);
        loader_contacts.setVisibility(View.VISIBLE);
        contactsModel = new ArrayList<>();

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        loadcontacts();
        makejson(contactsModel);

    }

    private void loadcontacts() {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        ContactsModel data = new ContactsModel();

                        data.setContactName(name);
                        data.setContactNumber(phoneNo);

                        contactsModel.add(data);

                        Log.i(TAG, "Name: " + name);
                        Log.i(TAG, "Phone Number: " + phoneNo);
                    }
                    // createExcelSheet();
                    // createCSV();

                    pCur.close();
                }
            }
        } else {
            loader_contacts.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "..", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), TakeSelfieActivity.class);
            startActivity(i);
        }

        if (cur != null) {
            cur.close();
        }


        /*String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
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
        phones.close();*/

    }

    private void makejson(List<ContactsModel> userList) {

        jsonArray = new JSONArray();

        for (int i = 0; i < userList.size(); i++) {

            JSONObject jsonObject = new JSONObject();

            String name = userList.get(i).getContactName();
            String number = userList.get(i).getContactNumber();


            try {

                jsonObject.put("contact_name", name);
                jsonObject.put("contact_number", number);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);

        }

        Log.d(TAG, "getContactsArray: " + jsonArray);

        request(jsonArray);

    }

    private void request(JSONArray jsonArray) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("contact", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "OBJ: " + jsonObject);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.CONTACTS_DATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG, "onResponse: " + response);

                if (response != null) {
                    Toast.makeText(getApplicationContext(), "Success..", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), TakeSelfieActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    loader_contacts.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "onErrorResponse: " + error.getMessage());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization", UserAccessToken);
                return params;
            }
        };

        MySingleton.getInstance(ReadContacts.this).addToRequestQueue(jsonObjectRequest);

    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                *//*loader_contacts.setVisibility(View.GONE);
                rl_layout.setVisibility(View.VISIBLE);*//*
                Toast.makeText(getApplicationContext(), "Please Grant Location Permission...", Toast.LENGTH_LONG).show();
                openPermissionSettings(ReadContacts.this);
            }
        }
    }*/

    public void gotopermcontacts(View view) {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + ReadContacts.this.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(intent);

    }
}