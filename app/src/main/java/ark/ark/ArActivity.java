package ark.ark;

import android.icu.text.DateFormat;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ark.ark.Groups.CurrentUser;
import ark.ark.Groups.Friend;
import ark.ark.Map.MapWaypoint;
import ark.ark.UserLocation.LocationSingleton;

public class ArActivity extends AppCompatActivity implements Observer {

    // ArchitectView is the view that shows the AR interface
    private ArchitectView architectView;
    private TextView lblCoord;
    private LocationSingleton mCurrentLocation;
    private CurrentUser mUser;
    private Integer mUpdateCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        this.lblCoord = (TextView)this.findViewById(R.id.lblCoord);

        // Set up architect view and AR interface
        this.architectView = (ArchitectView)this.findViewById( R.id.architectView );
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey(getString(R.string.licensekey));
        this.architectView.onCreate( config );

        mCurrentLocation.getInstance().addObserver(this);

        mUser = CurrentUser.getInstance();
        mUser.addObserver(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();
        try {
            this.architectView.load("file:///android_asset/squadAR/index.html");
            //this.architectView.callJavascript("loadPoi();");
            if (mCurrentLocation.getInstance().getLocation() != null) {
                Location location = mCurrentLocation.getInstance().getLocation();
                architectView.setLocation(location.getLatitude(), location.getLongitude(), location.getAltitude());
            }

            if (mUser.getActiveGroup().getFriends().values() != null) {
                Collection<Friend> friends = mUser.getActiveGroup().getFriends().values();
                for (Friend f : friends) {
                    updatePOI(f.getLocation().getLatitude(), f.getLocation().getLatitude(), f.getEmail(), f.getLocation().distanceTo(mCurrentLocation.getInstance().getLocation()));
                }
            }

            if (mUser.getActiveGroup().getWaypoint() != null) {
                updateWaypoint(mUser.getActiveGroup().getWaypoint());
                Log.d("waypointAR", "set");
            }

        } catch (Exception e) {

        }

    }

    @Override
    public void update(Observable o, Object data) {
        if(o == mCurrentLocation) {
            Location location = (Location) data;


            if (location != null) {
                architectView.setLocation(location.getLatitude(), location.getLongitude(), location.getAltitude());
                lblCoord.setText("up(" + mUpdateCount + ")"
                        + location.getLatitude() + " "
                        + location.getLongitude());
                mUpdateCount += 1;

            }
            showToast("current location changed");
        } else if (o == CurrentUser.getInstance() && data instanceof String) {
            String email = (String)data;
            CurrentUser user = (CurrentUser)o;
            Location loc = user.getActiveGroup().getFriend(email).getLocation();
            updatePOI(loc.getLatitude(), loc.getLongitude(), email, mCurrentLocation.getInstance().getLocation().distanceTo(loc));
        } else if (o == CurrentUser.getInstance() && data instanceof MapWaypoint) {
            showToast("Waypoint updated");
            updateWaypoint(CurrentUser.getInstance().getActiveGroup().getWaypoint());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();

        architectView.onResume();
        if (mUser.getActiveGroup().getWaypoint() != null) {

            updateWaypoint(mUser.getActiveGroup().getWaypoint());
            Log.d("waypointAR", "set");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        architectView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        architectView.onPause();
    }

    /*
    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null);
    }
    */
    private void updatePOI(Double lat, Double lng, String user, Float dist){
        architectView.callJavascript("updatePOI(" + lat + ", " + lng + ", '" +  user + "', " + dist + ");");
        //showToast(user + " updated");
        //showToast("updatePOI(" + lat + ", " + lng + ", '" +  user + "');");
    }

    private void updateWaypoint(MapWaypoint wp) {
        if(wp.getActive() == true) {
            architectView.callJavascript("updateWP(" + wp.getLocation().latitude + ", " + wp.getLocation().longitude + ", '" +  "waypoint" + "');");
        } else {
            architectView.callJavascript("deleteWP();");
        }
    }

    public void initLoc(View v){
        showToast("init");
        architectView.callJavascript("deleteWP()");

    }

    public void updateloc1(View v){
        showToast("loc1");
        updateWaypoint(mUser.getActiveGroup().getWaypoint());

    }
    public void updateloc2(View v){
        showToast("loc2");
        architectView.callJavascript("updateWP(30, -144, 'user2');");

    }

    public void showToast(String message) {
        ToastUtils.showToast(message, this);
    }
}