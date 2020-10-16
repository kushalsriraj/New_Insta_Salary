package rutherfordit.com.instasalary.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.Urls;

public class ProfileFragment extends Fragment {

    ProgressDialog progressBar;
    CardView loader_profile;
    CircleImageView img;
    View v;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    LinearLayout layout_driver_profition, layout_salary_profiyion;
    TextView profile_doorno, profile_street, profile_state, profile_city, profile_pincode,
            curr_profile_doorno, curr_profile_street, curr_profile_state, curr_profile_city, profile_c_name, profile_c_role, profile_c_joining, profile_c_email;
    TextView profile_fullname, profile_email, profile_phone, profile_aadhar, profile_pan, profile_dob, curr_profile_pincode;
    TextView profile_c_doorno, profile_c_street, profile_c_state, profile_c_city, profile_c_pincode, profile_d_vechilenumber, profile_d_avgincome;
    JSONObject data;
    String TAG = "profle";
    String id, userid, vendor_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.layout_profile, null);

        init();

        return v;
    }

    private void init() {

        loader_profile = v.findViewById(R.id.loader_profile);

        sharedPreferences = getActivity().getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        Log.e(TAG, "init: " + UserAccessToken);

        loader_profile.setVisibility(View.VISIBLE);

        img = v.findViewById(R.id.img);
        profile_fullname = v.findViewById(R.id.profile_fullname);
        profile_email = v.findViewById(R.id.profile_email);
        profile_phone = v.findViewById(R.id.profile_phone);
        profile_aadhar = v.findViewById(R.id.profile_aadhar);
        profile_pan = v.findViewById(R.id.profile_pan);
        profile_dob = v.findViewById(R.id.profile_dob);
        profile_doorno = v.findViewById(R.id.profile_doorno);
        profile_street = v.findViewById(R.id.profile_street);
        profile_state = v.findViewById(R.id.profile_state);
        profile_city = v.findViewById(R.id.profile_city);

        curr_profile_doorno = v.findViewById(R.id.curr_profile_doorno);
        curr_profile_street = v.findViewById(R.id.curr_profile_street);
        curr_profile_state = v.findViewById(R.id.curr_profile_state);
        curr_profile_city = v.findViewById(R.id.curr_profile_city);
        profile_c_name = v.findViewById(R.id.profile_c_name);
        profile_c_role = v.findViewById(R.id.profile_c_role);
        profile_c_joining = v.findViewById(R.id.profile_c_joining);
        profile_c_email = v.findViewById(R.id.profile_c_email);
        profile_c_doorno = v.findViewById(R.id.profile_c_doorno);
        profile_c_street = v.findViewById(R.id.profile_c_street);
        profile_c_city = v.findViewById(R.id.profile_c_city);
        profile_c_state = v.findViewById(R.id.profile_c_state);
        profile_c_pincode = v.findViewById(R.id.profile_c_pincode);

        layout_salary_profiyion = v.findViewById(R.id.salary_profition);
        layout_driver_profition = v.findViewById(R.id.driver_profition);

        profile_d_vechilenumber = v.findViewById(R.id.profile_d_vehiclenumber);
        profile_d_avgincome = v.findViewById(R.id.profile_d_monthlyincome);


        curr_profile_pincode = v.findViewById(R.id.curr_profile_pincode);
        profile_pincode = v.findViewById(R.id.profile_pincode);

        req();

    }

    private void req() {
        Log.e("profle", "senddata: " + data);

        try {
            profile_fullname.setText(data.getString("name"));
            profile_email.setText(data.getString("email"));
            profile_phone.setText(data.getString("phone_number"));
            profile_aadhar.setText(data.getString("aadhar_number"));
            profile_pan.setText(data.getString("pan_number"));
            profile_dob.setText(data.getString("dob"));
            //   profile_doorno.setText(data.getString("door_no"));


            JSONObject address_obj = data.getJSONObject("addressdetails");

            JSONArray address_array = address_obj.getJSONArray("data");

            for (int j = 0; j < address_array.length(); j++) {

                JSONObject obj1 = address_array.getJSONObject(0);

                Log.e(TAG, "obj1 " + address_array.length());

                profile_doorno.setText(obj1.getString("door_no"));
                profile_street.setText(obj1.getString("street"));
                profile_state.setText(obj1.getString("state"));
                profile_city.setText(obj1.getString("city"));
                profile_pincode.setText(obj1.getString("pincode"));

                if (address_array.length() > 1) {
                    JSONObject obj2 = address_array.getJSONObject(1);

                    curr_profile_doorno.setText(obj2.getString("door_no"));
                    curr_profile_street.setText(obj2.getString("street"));
                    curr_profile_state.setText(obj2.getString("state"));
                    curr_profile_city.setText(obj2.getString("city"));
                    curr_profile_pincode.setText(obj2.getString("pincode"));
                } else {
                    curr_profile_doorno.setText(obj1.getString("door_no"));
                    curr_profile_street.setText(obj1.getString("street"));
                    curr_profile_state.setText(obj1.getString("state"));
                    curr_profile_city.setText(obj1.getString("city"));
                    curr_profile_pincode.setText(obj1.getString("pincode"));
                }

                loader_profile.setVisibility(View.GONE);

            }

            JSONObject image_obj = data.getJSONObject("imagedetails");

            JSONArray image_array = image_obj.getJSONArray("data");

            Log.e(TAG, "imageobje " + image_obj);

            for (int k = 0; k < image_array.length(); k++) {

                JSONObject object = image_array.getJSONObject(0);

                String image = object.getString("proof");

             //   Picasso.with(getContext()).load(Urls.IMAGE_CONSTANT + image).into(img);

                Picasso.get().load(Urls.IMAGE_CONSTANT + image).into(img);

                Log.e(TAG, "imageeeeee" + image);

                loader_profile.setVisibility(View.GONE);

            }

            if (data.has("driverprofession")) {
                JSONObject driv_obj = data.getJSONObject("driverprofession");

                Log.e(TAG, "req: " + driv_obj);

                JSONObject driv_array = driv_obj.getJSONObject("data");

                profile_d_avgincome.setText(driv_array.getString("avg_income"));
                profile_d_vechilenumber.setText(driv_array.getString("vehicle_number"));
            } else {
                layout_driver_profition.setVisibility(View.GONE);
                layout_salary_profiyion.setVisibility(View.VISIBLE);
            }


            if (data.has("profession")) {
                JSONObject prof_obj = data.getJSONObject("profession");

                JSONArray _array = prof_obj.getJSONArray("data");

                if (data.length() > 0) {
                    Log.e(TAG, "imageobje " + prof_obj);

                    for (int k = 0; k < _array.length(); k++) {

                        JSONObject object = _array.getJSONObject(0);

                        profile_c_name.setText(object.getString("company_name"));
                        profile_c_email.setText(object.getString("company_mail"));
                        profile_c_role.setText(object.getString("job_role"));
                        profile_c_joining.setText(object.getString("working_since"));
                        profile_c_pincode.setText(object.getString("pincode"));
                        profile_c_state.setText(object.getString("state"));
                        profile_c_city.setText(object.getString("city"));
                        profile_c_street.setText(object.getString("street"));
                        profile_c_doorno.setText(object.getString("door_no"));

                    }
                } else {

                    layout_salary_profiyion.setVisibility(View.GONE);
                    layout_driver_profition.setVisibility(View.VISIBLE);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            loader_profile.setVisibility(View.GONE);
        }
    }


    public void senddata(JSONObject mydata) {

        data = mydata;
    }
}
