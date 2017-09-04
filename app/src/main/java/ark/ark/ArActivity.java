package ark.ark;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

public class ArActivity extends AppCompatActivity {

    // ArchitectView is the view that shows the AR interface
    private ArchitectView architectView;


    // This part is the location provider that allows current location of the user to be tracked
    //protected GoogleApiClient mGoogleApiClient;                 //google api client needed for loactionservices
    private FusedLocationProviderClient mFusedLocationClient;   // Fused location services tracks user location




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);


        // Set up architect view and AR interface
        this.architectView = (ArchitectView)this.findViewById( R.id.architectView );
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey(getString(R.string.licensekey));
        this.architectView.onCreate( config );

        // Set up location provider to track current user's location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
}