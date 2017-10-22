package ark.ark.Profile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
import ark.ark.Groups.CurrentUser;
import ark.ark.Map.MapNavDrawer;
import ark.ark.R;
import ark.ark.ToastUtils;

public class GroupCreationActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);

    }

    /**
     * Creates a group when the button is pressed
     * @param v The current view
     */
    public void addGroup(View v){
        EditText groupNameTextField = (EditText) findViewById(R.id.gName);

        String groupName = groupNameTextField.getText().toString();

        //Checks for unsupported strings such as " "
        try {
            groupName = URLEncoder.encode(groupName, "UTF-8");
        }catch(UnsupportedEncodingException e){

        }
        postGroupCreation(groupName,getApplicationContext());
    }

    /**
     * Closes the current activity and goes to home (MapNavDrawer)
     */
    public void finishactivity(){
        Intent myIntent = new Intent(GroupCreationActivity.this, MapNavDrawer.class);
        startActivity(myIntent);
        this.finish();
    }

    /**
     * Sends the group creation request to the server
     * @param groupName Specified by the user
     * @param context The application context
     */
    public void postGroupCreation(String groupName, final Context context){
        String email = CurrentUser.getInstance().getEmail();
        String gName = groupName;

        RequestQueue queue = Volley.newRequestQueue(context);

        String server ="52.65.97.117";
        String path = "/group/create?";


        String requestURL = "http://" + server + path +"email="+email+"&group_name="+gName;
        //ToastUtils.showToast(requestURL,context);


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
                                ToastUtils.showToast("Congratulations! You created a group :)", context);
                                String groupID = res.getString("group_id");

                                //store data in groups
                                ARK_auth.storeGroup(groupID,context);
                                finishactivity();

                                //goToGroups();

                            } else {
                                ToastUtils.showToast(res.getString("msg"),context);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast("exception",context);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error handling
                ToastUtils.showToast("Sorry, cannot connect to the server.",context);
            }
        });

        queue.add(stringRequest);


    }




}
