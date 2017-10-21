package ark.ark.Groups;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.HashMap;

import ark.ark.Authentication.ARK_auth;
import ark.ark.Map.MapNavDrawer;
import ark.ark.R;
import ark.ark.ToastUtils;

public class JoinGroupActivity extends AppCompatActivity {

    ListView listView ;
    private GroupProfileAdapter ListAdapter;
    private ArrayList<Group> dataList;
    private HashMap<String, Group> dataMap;
    private static int nData = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
    }


    public void searchGroup(View v){
        listView = (ListView) findViewById(R.id.list);
        dataList = new ArrayList<Group>();
        ListAdapter = new GroupProfileAdapter(this, dataList);
        listView.setAdapter(ListAdapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //Join the group

                String email = CurrentUser.getInstance().getEmail();
                ARK_auth.storeGroup(dataList.get(position).getId(),getApplicationContext());
                postAddUserToGroup(email, dataList.get(position).getId(), getApplicationContext());
            }
        });


        EditText groupName = (EditText)findViewById(R.id.groupNameSearch);
        getSearchGroup(groupName.getText().toString());


    }
    private void finish_activities(){
        Intent myIntent = new Intent(JoinGroupActivity.this, MapNavDrawer.class);
        startActivity(myIntent);
        this.finish();
    }

    public void postAddUserToGroup(String userEmail, final String gID, final Context context) {
        String email = userEmail;

        RequestQueue queue = Volley.newRequestQueue(context);

        String server = "52.65.97.117";
        String path = "/group/add?";

        String requestURL = "http://" + server + path + "email=" + email + "&group_id=" + gID;


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
                                ToastUtils.showToast("successfully joined group", context);
                                ARK_auth.storeGroup(gID, context);
//                                updateGroups(context);
                                finish_activities();

                            } else {
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
    }




    private void getSearchGroup(String gName) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String server ="52.65.97.117";
        String path = "/group/search?";

        String groupName = gName;

        try {
            groupName = URLEncoder.encode(gName, "UTF-8");
        }catch(UnsupportedEncodingException e){

        }

        String requestURL = "http://"+server+path+"group_name="+groupName;

        //ToastUtils.showToast(requestURL,getApplicationContext());


        // Request a string response from the requestURL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // after getting response, try reading the json
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("success").equals("ok")) {
                                // iterate through the list of groups
                                ToastUtils.showToast(res.getJSONArray("groups").length()+" group(s) found",getApplicationContext());

                                nData = 0;

                                for (int i=0;i<res.getJSONArray("groups").length();i++) {

                                    Group group = new Group(res.getJSONArray("groups").getString(i),CurrentUser.getInstance().getEmail());

                                    // pushing the new item into the list
                                    dataList.add(group);
                                    EditText groupName = (EditText)findViewById(R.id.groupNameSearch);
                                    dataList.get(i).setName(groupName.getText().toString());
                                }

                            } else {
                                ToastUtils.showToast(res.getString("msg"),getApplicationContext());
                            }
                            // after pushing into the list, update
                            ListAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
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


}
