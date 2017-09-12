package ark.ark.Profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ark.ark.R;

public class ProfileCreationActivity extends AppCompatActivity {

    //String userID = "blank";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
    }


    //button test
    public void testButton(View view) {

        //showToast("button pressed");

        EditText nickname = (EditText) findViewById(R.id.create_nickname);
        EditText password = (EditText) findViewById(R.id.user_password);
        EditText email = (EditText) findViewById(R.id.user_email);

        /*
        showToast("Hello " + nickname.getText().toString());
        showToast("Your password is " + password.getText().toString());
        showToast("Your email is " + email.getText().toString());
        */

        postUserCreation(nickname.getText().toString(), email.getText().toString(),
               password.getText().toString());


    }


    //connecting to server

    private void postUserCreation(String nickname, String email, String password) {
        RequestQueue queue = Volley.newRequestQueue(this);

        //showToast("creating user...");

        String server ="52.65.97.117";
        /* Test data
        String userEmail = "user1@user1.com";
        String testUserID = "62390ede67dbbec5b650dfd1b0f33d15";
        String requestURL = "http://" + server + "/direct/show?email=" + userEmail;
        requestURL = "http://" + server + "/users/show?user_id=" + testUserID;
        */

        String path = "/users/create?";
        String description = "creating a new user from ARK app with android studio and volley";

        String requestURL = "http://" + server + path +"email="+email+"&nick_name="+nickname+
                "&password_salted="+password+"&description="+description;

        // Request a string response from the requestURL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // after getting response, try reading the json
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("success").equals("ok")) {
                                // iterate thru the direct list to populate the convo list

                                //EditText testField = (EditText) findViewById(R.id.create_nickname);
                                showToast("Congratulations! Your account has been created!");
                                //userID =res.getString("user_id");
                                //showToast("user "+userID+ " has been created");
                                //testField.setText(userID);
                                //showToast(res.getJSONObject("user_info").getString("email"));

                            } else {
                                showToast(res.getString("msg"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast("exception");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error handling
                showToast("Sorry, cannot connect to the server.");
            }
        });

        queue.add(stringRequest);

    }


    //GET request example

    private void getRequestExample() {
        RequestQueue queue = Volley.newRequestQueue(this);

        showToast("creating user...");

        String server ="52.65.97.117";
        String userEmail = "user1@user1.com";
        String testUserID = "62390ede67dbbec5b650dfd1b0f33d15";
        String requestURL = "http://" + server + "/direct/show?email=" + userEmail;

        requestURL = "http://" + server + "/users/show?user_id=" + testUserID;

        // Request a string response from the requestURL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // after getting response, try reading the json
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("success").equals("ok")) {
                                // iterate thru the direct list to populate the convo list

                                showToast("data GET!");
                                showToast(res.getString("user_info"));
                                showToast(res.getJSONObject("user_info").getString("email"));

                            } else {
                                showToast(res.getString("msg"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast("exception");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error handling
                showToast("Sorry, cannot connect to the server.");
            }
        });

        queue.add(stringRequest);
    }



    private void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
    }

}
