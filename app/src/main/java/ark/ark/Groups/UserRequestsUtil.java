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
    Updates the groups the user is in
     */
    public static void initialiseCurrentUser(Context context) {
        /*
        CurrentUser mUser = CurrentUser.getInstance();

        if (mUser.getEmail() != null) {
            RequestQueue queue = Volley.newRequestQueue(context);
            String server = "52.65.97.117";

            String path = "/group/show?";
            String requestURL = "http://" + server + path +"email="+ mUser.getEmail();
            ToastUtils.showToast(requestURL, context);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            // after getting response, try reading the json
                            CurrentUser mUser = CurrentUser.getInstance();
                            ToastUtils.showToast(response, context);
                            try {
                                JSONObject res = new JSONObject(response);

                                if (res.getString("success").equals("ok")) {
                                    for (int i = 0; i < res.getJSONArray("groups").length(); i++) {
                                        Group g = new Group(res.getJSONArray("groups").getString(i));
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
        */
    }

    public static void updateGroups(final Context context) {
        CurrentUser mUser = CurrentUser.getInstance();

        if (mUser.getEmail() != null) {
            RequestQueue queue = Volley.newRequestQueue(context);
            String server = "52.65.97.117";

            String path = "/group/show?";
            String requestURL = "http://" + server + path +"email="+ mUser.getEmail();
            ToastUtils.showToast(requestURL, context);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            // after getting response, try reading the json
                            CurrentUser mUser = CurrentUser.getInstance();
                            ToastUtils.showToast(response, context);
                            try {
                                JSONObject res = new JSONObject(response);

                                if (res.getString("success").equals("ok")) {
                                    for (int i = 0; i < res.getJSONArray("groups").length(); i++) {
                                        Group g = new Group(res.getJSONArray("groups").getString(i));
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

}
