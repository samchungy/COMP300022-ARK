package ark.ark.UserLocation;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import ark.ark.Authentication.ARK_auth;
import ark.ark.R;
import ark.ark.ToastUtils;

public class LocationUpdateService extends Service {
    private static final int REQUEST_LOCATION = 2;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Integer mUpdateCount = 1;
    private LocationSingleton currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    public LocationUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();


        ToastUtils.showToast("created LocationUpdateService", this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //ToastUtils.showToast("mLocationCallback being run", getApplicationContext());
                for (Location location : locationResult.getLocations()) {
                    super.onLocationResult(locationResult);
                    currentLocation.getInstance().setLocation(location);
                    //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                    if (currentLocation.getInstance().getLocation() != null) {
                        //ToastUtils.showToast("up(" + mUpdateCount + ") " + currentLocation.getInstance().getLocation().getLatitude() + " " + currentLocation.getInstance().getLocation().getLongitude(), getApplicationContext());
                        mUpdateCount += 1;

                        if(currentLocation.getInstance().isAllowingUpdates() == true) {
                            updateLocation();
                        }

                    }


                }
            };
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    @SuppressWarnings("MissingPermission")
    public void locationInitialise(){

        ToastUtils.showToast("Setting initial location", getApplicationContext());


        mFusedLocationClient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation.getInstance().setLocation(location);
                    //ToastUtils.showToast("we found you! @" + location.getLatitude() + " " +  location.getLongitude(), getApplicationContext());
                } else {
                    //ToastUtils.showToast("you don't have location", getApplicationContext());
                }
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        ToastUtils.showToast("location updates happening", getApplicationContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }


    public void updateLocation(){
        if (currentLocation.getInstance().getLocation() != null) {
            RequestQueue queue = Volley.newRequestQueue(this);
            Location locationToSend = currentLocation.getInstance().getLocation();
            String server = getString(R.string.serverip);

            String path = "/locations/update?";
            String requestURL = "http://" + server + path +"email="+ ARK_auth.fetchUserEmail(this)
                    +"&lat="+locationToSend.getLatitude()+
                    "&lng="+locationToSend.getLongitude();
            //ToastUtils.showToast(requestURL, getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // after getting response, try reading the json
                            try {
                                JSONObject res = new JSONObject(response);
                                if (res.getString("success").equals("ok")) {
                                    // iterate through the direct list to populate the convo list

                                    //ToastUtils.showToast("Congratulations! Your location is updated!", getApplicationContext());

                                } else {
                                    ToastUtils.showToast(res.getString("msg"), getApplicationContext());
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastUtils.showToast("exception", getApplicationContext());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error handling
                    ToastUtils.showToast("Sorry, cannot connect to the server.", getApplicationContext());
                }
            });

            queue.add(stringRequest);
        } else {
            ToastUtils.showToast("Location doesn't exist", getApplicationContext());
        }





        // Request a string response from the requestURL.


    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        locationInitialise();

        startLocationUpdates();

        return START_STICKY; //START_STICKY indicates it stops and starts as needed
    }
}
