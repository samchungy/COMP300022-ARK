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
import java.util.Observable;
import java.util.Observer;

import ark.ark.UserLocation.LocationSingleton;

public class ArActivity extends AppCompatActivity implements Observer {

    // ArchitectView is the view that shows the AR interface
    private ArchitectView architectView;
    private TextView lblCoord;
    private LocationSingleton mCurrentLocation;
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
    public void update(Observable o, Object data) {

        Location location = (Location)data;


        if (location != null) {
            architectView.setLocation(location.getLatitude(), location.getLongitude(), location.getAltitude());
            lblCoord.setText("up(" + mUpdateCount+ ")"
                    + location.getLatitude() + " "
                    + location.getLongitude());
            mUpdateCount += 1;
        }

    }


    /*
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
    */

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

    /*
    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null);
    }
    */

    public void showToast(String message) {
        ToastUtils.showToast(message, this);
    }
}