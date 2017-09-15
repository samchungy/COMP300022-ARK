package ark.ark;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

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
import ark.ark.Profile.ProfileCreationActivity;
import butterknife.ButterKnife;
import ark.ark.Chat.ChatFragment;
import butterknife.OnClick;
import layout.HomeFragment;
import layout.MapFragment;


// the main activity
public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 2;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Integer mUpdateCount = 1;


    private Fragment frag_home = new HomeFragment();
    private Fragment frag_chat = new ChatFragment();
    private Fragment frag_map = new MapFragment();
    private Fragment frag_profile = new ProfileFragment();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchTo("Home");
                    return true;
                case R.id.navigation_chat:
                    switchTo("Chat");
                    return true;
                case R.id.navigation_map:
                    switchTo("Map");
                    Intent myIntent = new Intent(HomeActivity.this, MapNavDrawer.class);
                    startActivity(myIntent);
                    return true;
                case R.id.navigation_profile:
                    switchTo("Profile");
                    Intent myIntent2 = new Intent(HomeActivity.this, ArActivity.class);
                    startActivity(myIntent2);
                    //Intent myIntent2 = new Intent(HomeActivity.this, ProfileCreationActivity.class);
                    //startActivity(myIntent2);
                    return true;
            }
            return false;
        }

    };

    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        ButterKnife.bind(this);
        //TODO

        /**Update the location*/
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                showToast("mLocationCallback being run");
                for (Location location : locationResult.getLocations()) {
                    super.onLocationResult(locationResult);
                    mCurrentLocation = location;
                    //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                    if (mCurrentLocation != null) {
                        showToast("up(" + mUpdateCount + ") " + mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());
                        mUpdateCount += 1;
                    }
                }
            };
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // switch to home page after entering the app
        switchTo("Home");



    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            locationInitialise();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }


    // switching to different pages
    private void switchTo(String page) {
        Fragment targetFrag;

        switch (page) {
            case "Home":
                targetFrag = frag_home;
                break;
            case "Chat":
                targetFrag = frag_chat;
                break;
            case "Map":
                targetFrag = frag_map;
                break;
            case "Profile":
                targetFrag = frag_profile;
                break;
            default:
                targetFrag = frag_home;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, targetFrag).commit();
    }



    public void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), message, duration);
        toast.show();
    }




    // test switching users
    public void austinTest_switchToUser1(View v) {
        ARK_auth.storeUserEmail("user1@user1.com", this);
        showToast("congrats folk! You switched to user1!");
        showToast(ARK_auth.fetchUserEmail(this));
    }

    public void austinTest_switchToUser2(View v) {
        ARK_auth.storeUserEmail("user2@user2.com", this);
        showToast("ughhhh! You switched to user2!");
        showToast(ARK_auth.fetchUserEmail(this));
    }




    /***Temporary method to test location updates*/
    /*** only call this function when location is already requested ***/
    @SuppressWarnings("MissingPermission")
    public void locationInitialise(){

        showToast("Setting initial location");


            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mCurrentLocation = location;
                        showToast("we found you! @" + location.getLatitude() + " " +  location.getLongitude());
                    } else {
                        showToast("you don't have location");
                    }
                }
            });
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        showToast("location updates happening");
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    public void updateLocation(View v){
        if (mCurrentLocation != null) {
            RequestQueue queue = Volley.newRequestQueue(this);

            String server ="52.65.97.117";

            String path = "/locations/update?";
            String requestURL = "http://" + server + path +"email="+ ARK_auth.fetchUserEmail(this)
                    +"&lat="+mCurrentLocation.getLatitude()+
                    "&lng="+mCurrentLocation.getLongitude();
            showToast(requestURL);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // after getting response, try reading the json
                            try {
                                JSONObject res = new JSONObject(response);
                                if (res.getString("success").equals("ok")) {
                                    // iterate through the direct list to populate the convo list

                                    showToast("Congratulations! Your location is updated!");

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
        } else {
            showToast("Location doesn't exist");
        }





        // Request a string response from the requestURL.


    }


}
