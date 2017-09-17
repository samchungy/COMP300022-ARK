package ark.ark;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import ark.ark.Authentication.ARK_auth;
import ark.ark.UserLocation.LocationSingleton;
import ark.ark.UserLocation.LocationUpdateService;
import butterknife.ButterKnife;
import ark.ark.Chat.ChatFragment;
import layout.HomeFragment;
import layout.MapFragment;


// the main activity
public class HomeActivity extends AppCompatActivity implements Observer {


    private static final int REQUEST_LOCATION = 2;
    /*
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Integer mUpdateCount = 1;
    private LocationSingleton currentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    */

    Intent mLocUpdateService;

    private Fragment frag_home = new HomeFragment();
    private Fragment frag_chat = new ChatFragment();
    private Fragment frag_map = new MapFragment();
    private Fragment frag_profile = new ProfileFragment();

    private LocationSingleton currentLocation;

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





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //

        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        ButterKnife.bind(this);
        //TODO

        mLocUpdateService = new Intent(this, LocationUpdateService.class);
        currentLocation.getInstance().addObserver(this);

        // switch to home page after entering the app
        switchTo("Home");


    }

    @Override
    public void update(Observable o, Object data) {
        showToast("Updated");
        showToast("ObserverUpdate:" +
                data.toString());

    }

    @Override
    protected void onStart() {
        super.onStart();
        //lblObs.setText("hello");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            startService(mLocUpdateService);
            /*
            locationInitialise();
            */
        }

        //lblLoc.setText("connected");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //startLocationUpdates();
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
        ToastUtils.showToast(message, this);
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


    public void allowUpdateLocation(View v){
        currentLocation.getInstance().allowServerUpdates();
    }

    public void stopUpdateLocation(View v){
        currentLocation.getInstance().stopServerUpdates();
    }

    public void getLocationDetails(View v){
        showToast(currentLocation.getInstance().getLocation().toString() + "\n" + "allowupdates " + currentLocation.getInstance().isAllowingUpdates());
    }


}
