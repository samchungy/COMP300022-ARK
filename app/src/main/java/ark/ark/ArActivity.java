package ark.ark;

import android.icu.text.DateFormat;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.Date;

public class ArActivity extends AppCompatActivity {

    // ArchitectView is the view that shows the AR interface
    private ArchitectView architectView;
    private TextView lblCoord;

    // This part is the location provider that allows current location of the user to be tracked
    //protected GoogleApiClient mGoogleApiClient;                 //google api client needed for loactionservices
    private FusedLocationProviderClient mFusedLocationClient;   // Fused location services tracks user location

    // Location updater constants
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Integer mUpdateCount = 1;
    //private String mLastUpdateTime;

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

        // Set up location provider to track current user's location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    super.onLocationResult(locationResult);
                    mCurrentLocation = locationResult.getLastLocation();
                    //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                    if (mCurrentLocation != null) {
                        architectView.setLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), mCurrentLocation.getAltitude());
                        lblCoord.setText("up(" + mUpdateCount + ") " + mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());
                        mUpdateCount += 1;
                    }
                }
            };
        };

        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();
        try {
            this.architectView.load("file:///android_asset/demo/index.html");

        } catch (Exception e) {

        }

    }


    @Override
    @SuppressWarnings("MissingPermission")
    protected void onStart() {
        super.onStart();
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                architectView.setLocation(location.getLatitude(), location.getLongitude(), location.getAltitude());
                                lblCoord.setText("init " + location.getLatitude() + " " + location.getLongitude());
                            }
                        }
                    });
        } catch (Exception e) {

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
        startLocationUpdates();
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

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }


}