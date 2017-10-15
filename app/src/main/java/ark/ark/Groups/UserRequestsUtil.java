package ark.ark.Groups;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;

import ark.ark.Authentication.ARK_auth;
import ark.ark.R;
import ark.ark.ToastUtils;

/**
 * Created by khtin on 22/09/2017.
 */


/**
 * Utility class for requests for CurrentUser that handles requests to server
 */
public class UserRequestsUtil {

    /*
    Updates the groups the user is in and then puts locations into the active group
     */
    public static void initialiseCurrentUser(final Context context) {
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
                                    for (int i = 0; i < res.getJSONArray("groups").length(); i++) {
                                        Group g = new Group(res.getJSONArray("groups").getString(i), mUser.getEmail());
                                        mUser.addGroup(g);
                                        updateCurrentUserGroupName(g.getId(),context);
                                    }
                                updateActiveGroupLocations(context);



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
        } else {
            ToastUtils.showToast("Location doesn't exist", context);
        }
    }

    public static void updateGroups(final Context context) {
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
                                    for (int i = 0; i < res.getJSONArray("groups").length(); i++) {
                                        Group g = new Group(res.getJSONArray("groups").getString(i), mUser.getEmail());
                                        mUser.addGroup(g);
                                    }

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
        } else {
            ToastUtils.showToast("Location doesn't exist", context);
        }
    }

    public static void updateActiveGroupLocations(final Context context) {
        CurrentUser mUser = CurrentUser.getInstance();

        if (mUser.getEmail() != null && mUser.getActiveGroup() != null) {
            RequestQueue queue = Volley.newRequestQueue(context);
            String server = "52.65.97.117";

            String path = "/group/locations?";
            String requestURL = "http://" + server + path +"group_id="+ mUser.getActiveGroup().getId();
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
                                    JSONArray friendList = res.getJSONArray("groups");
                                    for (int i = 0; i < friendList.length(); i++) {
                                        JSONObject friend = friendList.getJSONObject(i);
                                        String email = friend.getString("email");
                                        Double lat = friend.getJSONObject("location").getDouble("lat");
                                        Double lng = friend.getJSONObject("location").getDouble("lng");

                                        Location loc = new Location("Server");
                                        loc.setLatitude(lat);
                                        loc.setLongitude(lng);

                                        Friend f = new Friend(email);
                                        mUser.getActiveGroup().updateFriend(f);
                                        mUser.getActiveGroup().setLocation(email, loc);
                                        //ToastUtils.showToast(email, context);
                                    }
                                    //ToastUtils.showToast(mUser.getActiveGroup().toString(), context);


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
        } else {
            ToastUtils.showToast("Location doesn't exist", context);
        }
    }

    public static void postGroupCreation(String groupName, final Context context){
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
                                updateGroups(context);

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

    public static void postAddUserToGroup(String userEmail, String gID, final Context context){
        String email = userEmail;
        String groupID = gID;

        RequestQueue queue = Volley.newRequestQueue(context);

        String server ="52.65.97.117";
        String path = "/group/add?";

        String requestURL = "http://" + server + path +"email="+email+"&group_id="+groupID;


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
                                updateGroups(context);


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

    public static void updateCurrentUserGroupName(String gID, final Context context){
        String groupID = gID;

        RequestQueue queue = Volley.newRequestQueue(context);

        String server ="52.65.97.117";
        String path = "/groupProfile/show?";

        String requestURL = "http://" + server + path +"group_id="+groupID;


        // Request a string response from the requestURL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // after getting response, try reading the json
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("success").equals("ok")) {

                                //get data from res object

                                String group_name = res.getJSONObject("profile").getString("group_name");
                                String group_id = res.getJSONObject("profile").getString("group_id");

                                //ToastUtils.showToast(group_name, context);

                                if(CurrentUser.getInstance().getAllGroups().containsKey(group_id)){
                                    CurrentUser.getInstance().getAllGroups().get(group_id).setName(group_name);
                                }

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
