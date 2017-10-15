package ark.ark.Profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import ark.ark.Authentication.ARK_auth;
import ark.ark.Groups.CurrentUser;
import ark.ark.Groups.UserRequestsUtil;
import ark.ark.HomeActivity;
import ark.ark.Map.MapNavDrawer;
import ark.ark.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void loginButton(View view){
        EditText password = (EditText) findViewById(R.id.login_password);
        EditText email = (EditText) findViewById(R.id.login_email);

        showToast("logging in... email: "+email.getText().toString()+", password: "+password.getText().toString());


        String parse = password.getText().toString();
        /*
        String hashed;
        hashed = BCrypt.hashpw(parse, BCrypt.gensalt(10));

        postUserLogin(email.getText().toString(), hashed);
        */

        postUserLogin(email.getText().toString(), parse);



    }

    public void goToSignUp(View view){

        showToast("going to Sign Up page...");
        Intent myIntent = new Intent(LoginActivity.this, ProfileCreationActivity.class);
        startActivity(myIntent);

    }

    private void goToHome(){
        Intent myIntent = new Intent(LoginActivity.this, MapNavDrawer.class);
        startActivity(myIntent);

        this.finish();
    }


    private void postUserLogin(final String email, String password) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String server ="52.65.97.117";
        String path = "/users/login?";

        String requestURL = "http://" + server + path +"email="+email+"&password_salted="+password;

        //showToast(requestURL);

        // Request a string response from the requestURL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // after getting response, try reading the json
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("success").equals("ok")) {

                                //get data from res object
                                showToast("Congratulations! You have logged in :)");
                                String sessionID = res.getString("session_id");

                                ARK_auth.storeUserEmail(email,getApplicationContext());
                                ARK_auth.storeSessionId(sessionID,getApplicationContext());
                                goToHome();

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

    public void LoginAsGuest(View v){
        ARK_auth.storeSessionId("guest",getApplicationContext());
        ARK_auth.storeUserEmail("user1@user1.com",getApplicationContext());
//        isDev=true;
        goToHome();
    }
}
