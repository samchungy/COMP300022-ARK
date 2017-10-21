package ark.ark.Profile;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ark.ark.Authentication.ARK_auth;
import ark.ark.Groups.CurrentUser;
import ark.ark.Groups.GroupListActivity;
import ark.ark.Map.MapNavDrawer;
import ark.ark.R;
import ark.ark.ToastUtils;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Specifies what action should be taken when the login button is pressed
     * @param view The current view
     * */

    public void loginButton(View view){
        EditText password = (EditText) findViewById(R.id.login_password);
        EditText email = (EditText) findViewById(R.id.login_email);

        String parse = password.getText().toString();
        /*
        String hashed;
        hashed = BCrypt.hashpw(parse, BCrypt.gensalt(10));

        postUserLogin(email.getText().toString(), hashed);
        */

        //check that the input fields contain valid strings
        if(validLoginInput(email,password)) {
            postUserLogin(email.getText().toString(), parse);
        }


    }

    /**
     * Sends the user to ProfileCreationActivity
     * @param view The current view
     */
    public void goToSignUp(View view){

//        showToast("going to Sign Up page...");
        Intent myIntent = new Intent(LoginActivity.this, ProfileCreationActivity.class);
        startActivity(myIntent);
        this.finish();

    }

    /**
     * Sends the user to the main activity (MapNavDrawer)
     */
    private void goToHome(){
        Intent myIntent = new Intent(LoginActivity.this, MapNavDrawer.class);
        startActivity(myIntent);

        this.finish();
    }

    /**
     * Sends the user's login details to the server
     * @param email - the email inputted by the user
     * @param password - the password inputted by the user
     */
    public void postUserLogin(final String email, String password) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String server ="52.65.97.117";
        String path = "/users/login?";

        String requestURL = "http://" + server + path +"email="+email+"&password_salted="+password;

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

                                //store the login information
                                ARK_auth.storeUserEmail(email,getApplicationContext());
                                ARK_auth.storeSessionId(sessionID,getApplicationContext());
                                updateGroups(getApplicationContext());

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
     * Updates the user's groups after login
     * @param context The application context
     */
    public void updateGroups(final Context context) {
        CurrentUser mUser = CurrentUser.getInstance();

        if (mUser.getEmail() != null) {
            RequestQueue queue = Volley.newRequestQueue(context);
            String server = "52.65.97.117";

            String path = "/group/show?";
            String requestURL = "http://" + server + path +"email="+ mUser.getEmail();
            //ToastUtils.showToast(requestURL, context);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            // after getting response, try reading the json
                            CurrentUser mUser = CurrentUser.getInstance();
                            //ToastUtils.showToast(response, context);
                            try {
                                JSONObject res = new JSONObject(response);

                                if (res.getString("success").equals("ok")) {
                                    if (res.getJSONArray("groups").length()==0){
                                        //User made without a group. Go to group joiner interface
                                        Intent myIntent2 = new Intent(context, GroupListActivity.class);
                                        startActivity(myIntent2);
                                    }
                                    else{
                                        //Store in cache
                                        ARK_auth.storeGroup(res.getJSONArray("groups").getString(0)
                                                ,getApplicationContext());
                                    }
                                    goToHome();
                                }
                                else {
                                    ToastUtils.showToast(res.getString("msg"), context);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastUtils.showToast("exception", context);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error handling
                    ToastUtils.showToast("Sorry, cannot connect to the server.", context);
                }
            });

            queue.add(stringRequest);
        } else {
            ToastUtils.showToast("Location doesn't exist", context);
        }
    }

    /**
     * Only for developers - for the purpose of testing features when the server is unavailable
     * Allows a developer to bypass login when the server is down
     * Logs in the developer as "user1"
     * @param v The current view
     */
    public void LoginAsGuest(View v){
        ARK_auth.storeSessionId("guest",getApplicationContext());
        ARK_auth.storeUserEmail("user1@user1.com",getApplicationContext());
        ARK_auth.storeGroup("group",getApplicationContext());
//        isDev=true;
        goToHome();
    }

    //Methods to check if input is valid

    /**
     * Checks if the input is a valid email address
     * @param email A string which may or may not be a valid email address
     * @return True if the input is a valid email address
     */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * Checks if the input from the login screen is in the correct format
     * This method is adapted from the standard login activity in Android Studio
     * @param emailField - the field in which users enter their email
     * @param passwordField - the field in which users enter their password
     * @return True if email and password are in the correct format
     */
    private boolean validLoginInput(EditText emailField, EditText passwordField) {

        EditText mPasswordView = passwordField;
        EditText mEmailView = emailField;

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one. Set an error if invalid.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address. Set an error if invalid.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

        return (!cancel);
    }


}
