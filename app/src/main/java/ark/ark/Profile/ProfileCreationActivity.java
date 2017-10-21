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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ark.ark.Authentication.ARK_auth;
import ark.ark.R;
import ark.ark.ToastUtils;

public class ProfileCreationActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
    }


    /**
     * Specifies what action should be taken when the sign up button is pressed
     * @param view The current view
     */
    public void signUpButton(View view) {

        EditText nickname = (EditText) findViewById(R.id.create_nickname);
        EditText password = (EditText) findViewById(R.id.signup_password);
        EditText cPassword = (EditText) findViewById(R.id.confirmPassword);
        EditText email = (EditText) findViewById(R.id.signup_email);


        if(validFormSubmission(email, password, cPassword, nickname)) {
            postUserCreation(nickname.getText().toString(), email.getText().toString(), password.getText().toString());
        }

    }

    /**
     * Sends the user from the current Activity to the GroupListActivity
     */
    private void goToGroup(){
        Intent myIntent = new Intent(ProfileCreationActivity.this, GroupListActivity.class);
        startActivity(myIntent);
        this.finish();
    }


    //connecting to server

    /**
     * Sends the user's profile creation details to the server
     * @param nickname
     * @param email
     * @param password
     */
    private void postUserCreation(String nickname, final String email, final String password) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String server ="52.65.97.117";
        String path = "/users/create?";
        String description = "creatingNewUserFromARK";

        String nName = nickname;

        try {
            nName = URLEncoder.encode(nickname, "UTF-8");
        }catch(UnsupportedEncodingException e){

        }

        String requestURL = "http://" + server + path +"email="+email+"&nick_name="+nName+
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

                                ToastUtils.showToast("Congratulations! Your account has been created!",getApplicationContext());
                                postUserLogin(email,password);

                            } else {
                                ToastUtils.showToast(res.getString("msg"),getApplicationContext());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast("exception",getApplicationContext());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error handling
                ToastUtils.showToast("Sorry, cannot connect to the server.",getApplicationContext());
            }
        });

        queue.add(stringRequest);

    }

    /**
     * Sends the user's login information to the server
     * Called after the user creates their profile
     * @param email
     * @param password
     */
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
                                ToastUtils.showToast("Congratulations! You have logged in :)",getApplicationContext());
                                String sessionID = res.getString("session_id");

                                //store profile data
                                ARK_auth.storeUserEmail(email,getApplicationContext());
                                ARK_auth.storeSessionId(sessionID,getApplicationContext());
                                goToGroup();


                            } else {
                                ToastUtils.showToast(res.getString("msg"),getApplicationContext());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast("exception",getApplicationContext());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error handling
                ToastUtils.showToast("Sorry, cannot connect to the server.",getApplicationContext());
            }
        });

        queue.add(stringRequest);

    }

    //Method to check if email is valid

    /**
     * Checks if the string is in the correct email format
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }


    /**
     * Checks if the input is in the correct format
     * @param emailField - the field which contains the inputted email
     * @param passwordField - the field which contains the inputted password
     * @param cPasswordField - a field which should have the same input as the password field
     * @param nickName - the field which contains the inputted nickname
     * @return Returns true if all strings from input fields are in the correct format
     */
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

        // Check that the user entered a password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        //check that the confirm password is the same as the 1st password entered
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

        // Check that the user entered a nickname
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
