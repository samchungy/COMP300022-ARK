package ark.ark.Profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import ark.ark.HomeActivity;
import ark.ark.R;

public class ProfileCreationActivity extends AppCompatActivity {

    //String userID = "blank";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
    }


    //button test
    public void signUpButton(View view) {

        EditText nickname = (EditText) findViewById(R.id.create_nickname);
        EditText password = (EditText) findViewById(R.id.login_password);
        EditText email = (EditText) findViewById(R.id.login_email);



        postUserCreation(nickname.getText().toString(), email.getText().toString(), password.getText().toString());

    }

    private void goToLogin(){
        Intent myIntent = new Intent(ProfileCreationActivity.this, LoginActivity.class);
        startActivity(myIntent);
        this.finish();
    }


    //connecting to server

    private void postUserCreation(String nickname, String email, String password) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String server ="52.65.97.117";
        String path = "/users/create?";
        String description = "creatingNewUserFromARK";

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
                                // iterate through the direct list to populate the convo list

                                showToast("Congratulations! Your account has been created!");
                                goToLogin();

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


    //Verification stuff

}
