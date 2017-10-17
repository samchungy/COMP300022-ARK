package ark.ark.Profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import ark.ark.Authentication.ARK_auth;
import ark.ark.Groups.GroupListActivity;
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
        EditText password = (EditText) findViewById(R.id.signup_password);
        EditText cPassword = (EditText) findViewById(R.id.confirmPassword);
        EditText email = (EditText) findViewById(R.id.signup_email);



        if(validFormSubmission(email, password, cPassword, nickname)) {
            postUserCreation(nickname.getText().toString(), email.getText().toString(), password.getText().toString());
        }

    }

    private void goToGroup(){
        Intent myIntent = new Intent(ProfileCreationActivity.this, GroupListActivity.class);
        startActivity(myIntent);
        this.finish();
    }


    //connecting to server

    private void postUserCreation(String nickname, final String email, final String password) {
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
                                postUserLogin(email,password);

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

    public void postUserLogin(final String email, String password) {
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
                                goToGroup();


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



    //Methods to check if email is valid
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    //Verification stuff
    private boolean validFormSubmission(EditText emailField, EditText passwordField, EditText cPasswordField, EditText nickName) {

        EditText mPasswordView = passwordField;
        EditText mConfirmPasswordView = cPasswordField;
        EditText mEmailView = emailField;
        EditText mNickNameView = nickName;

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        mNickNameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String password2 = mConfirmPasswordView.getText().toString();
        String nickname = mNickNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!password2.equals(password)) {
            mConfirmPasswordView.setError(getString(R.string.error_password_match));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(nickname)) {
            mNickNameView.setError(getString(R.string.error_field_required));
            focusView = mNickNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

        return !cancel;
    }

}
