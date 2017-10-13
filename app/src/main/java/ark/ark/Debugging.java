package ark.ark;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ark.ark.Authentication.ARK_auth;
import ark.ark.Chat.ChatFragment;
import ark.ark.Groups.CurrentUser;
import ark.ark.Groups.GroupLocationUpdateService;
import ark.ark.Groups.UserRequestsUtil;
import ark.ark.Profile.LoginActivity;
import ark.ark.UserLocation.LocationSingleton;
import ark.ark.UserLocation.LocationUpdateService;
import butterknife.ButterKnife;
import layout.HomeFragment;
import layout.MapFragment;

public class Debugging extends AppCompatActivity {

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
    Intent mGroupLocUpdateService;

    private LocationSingleton currentLocation;

    private CurrentUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        ButterKnife.bind(this);

        mLocUpdateService = new Intent(this, LocationUpdateService.class);
        mGroupLocUpdateService = new Intent(this, GroupLocationUpdateService.class);

        mCurrentUser = mCurrentUser.getInstance();
    }

    public void showToast(String message) {
        ToastUtils.showToast(message, this);
    }


    public void austinTest_switchToUser1(View v) {
        /*
        ARK_auth.storeUserEmail("user1@user1.com", this);
        showToast("congrats folk! You switched to user1!");

        showToast(ARK_auth.fetchUserEmail(this));
        */
        mCurrentUser.logOn("user1@user1.com");
        UserRequestsUtil.initialiseCurrentUser(this);


        showToast(mCurrentUser.getEmail());
        //showToast(mCurrentUser.getActiveGroup().getId());
    }

    public void austinTest_switchToUser2(View v) {

        mCurrentUser.logOn("user2@user2.com");
        UserRequestsUtil.initialiseCurrentUser(this);
        //UserRequestsUtil.updateActiveGroupLocations(this);
    }

    public void allowUpdateLocation(View v){
        LocationSingleton.getInstance().allowServerUpdates();
    }

    public void stopUpdateLocation(View v){
        LocationSingleton.getInstance().stopServerUpdates();
    }

    public void getLocationDetails(View v){
        showToast(LocationSingleton.getInstance().getLocation().toString() + "\n" + "allowupdates " + LocationSingleton.getInstance().isAllowingUpdates());
    }


    public void logoutButton(View v){
        showToast("logging out...");
        ARK_auth.clearUserData(this);
        Intent myIntent2 = new Intent(Debugging.this, LoginActivity.class);
        //Intent myIntent2 = new Intent(HomeActivity.this, ProfileCreationActivity.class);
        startActivity(myIntent2);
        this.finish();
    }

}
