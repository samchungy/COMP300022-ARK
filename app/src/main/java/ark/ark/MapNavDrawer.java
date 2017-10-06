package ark.ark;

import android.Manifest;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.Algorithm;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import ark.ark.Authentication.ARK_auth;
import ark.ark.UserLocation.ArkMarker;

public class MapNavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    private UiSettings uiSettings;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    Marker mWaypoint;
    Marker mPerson;
    String waypointloc;
    String otheremail;
    String useremail;
    LatLng undobk;
    private BottomSheetBehavior mBottomSheetBehavior;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWaypoint = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_nav_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Map Initiate
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Bottom Panel Initialise
        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(300);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                View bottomSheet = findViewById( R.id.bottom_sheet );
                mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                if(mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED){
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else{
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                if(mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_HIDDEN){
                    changeFAB(fab, R.drawable.map_marker_radius, R.color.colorPrimaryDark);
                    if (mWaypoint != null){
                        switchBottomSheet();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View bottomSheet = findViewById( R.id.bottom_sheet );
                mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                if (mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED){
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else if(mWaypoint != null){
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                else{
                    selectPlace(view);
                }
            }
        });

        //Google Places Initialise
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        //Initiate RequestQueue
        // Instantiate the RequestQueue.
        final TextView mTextView = (TextView) findViewById(R.id.textView2);
        RequestQueue queue = Volley.newRequestQueue(this);
        useremail = ARK_auth.fetchUserEmail(this);
        mTextView.setText(useremail);

        if (useremail == "user1@user1.com"){
            otheremail = "user2@user2.com";
        }
        else{
            otheremail = "user1@user1.com";
        }
        String url ="http://52.65.97.117/locations/show?email=" + otheremail;

        // Request a json response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject loc = response.getJSONObject("location");
                            showPerson(new LatLng(loc.getDouble("lat"),loc.getDouble("lng")),otheremail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else if(mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else{
            fab.setImageResource(R.drawable.map_marker_radius);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        TextView locname = (TextView)findViewById(R.id.textViewLocName);
        TextView locdetails = (TextView)findViewById(R.id.textViewLocDetails);
        TextView loctitle = (TextView)findViewById(R.id.textViewWaypoint);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        locname.setText(marker.getTitle());
        loctitle.setText(marker.getTitle());
        if (marker.equals(mPerson)){
            locdetails.setText(marker.getPosition().toString());
            changeFAB(fab, R.drawable.ic_person_black_24dp, R.color.cyan);
        }
        else if (marker.equals(mWaypoint)){
            locdetails.setText(waypointloc);
            changeFAB(fab, R.drawable.map_marker_radius, R.color.colorPrimaryDark);
        }

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        return true;
    }

    public void selectPlace(View view) {
        int PLACE_PICKER_REQUEST = 1;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            //TODO
        } catch (GooglePlayServicesNotAvailableException e) {
            //TODO
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int PLACE_PICKER_REQUEST = 1;
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                setWaypoint(place);
            }
        }
    }

    public void setWaypoint(Place place){
        TextView locname = (TextView)findViewById(R.id.textViewLocName);
        TextView loctitle = (TextView)findViewById(R.id.textViewWaypoint);
        TextView locdetails = (TextView)findViewById(R.id.textViewLocDetails);

        if (mWaypoint != null) {
            mWaypoint.remove();
            locname.setText("");
            mWaypoint = null;
        }

        mWaypoint = mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                .title(useremail +"'s Hotspot"));
        locname.setText(place.getName());
        loctitle.setText(useremail +"'s Hotspot");
        locdetails.setText(place.getAddress());
        waypointloc = place.getAddress().toString();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void deleteWaypoint(View view){
        if (mWaypoint != null) {
            undobk = mWaypoint.getPosition();
            mWaypoint.remove();
            mWaypoint = null;
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.bottom_sheet_layout),
                R.string.waypointremoved, Snackbar.LENGTH_LONG);
        mySnackbar.setAction(R.string.undo_string, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWaypoint = mMap.addMarker(new MarkerOptions().position(undobk)
                        .title(useremail + "'s Waypoint"));
            }
        }).addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar mySnackbar, int event) {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.show();
            }
        });
        mySnackbar.show();
    }


    public void showPerson(LatLng lat, String name){
        mPerson = mMap.addMarker(new MarkerOptions()
                .position(lat)
                .title(name + "'s Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    public LatLng getCoords(JSONObject response) throws JSONException {
        LatLng coord = new LatLng(response.getDouble("lat"), response.getDouble("lng"));
        return coord;
    }

    private void changeFAB(FloatingActionButton fab, int icon, int colour){
        fab.setImageResource(icon);
        fab.setBackgroundColor(colour);
    }

    private void switchBottomSheet(){
        TextView locname = (TextView)findViewById(R.id.textViewLocName);
        TextView locdetails = (TextView)findViewById(R.id.textViewLocDetails);
        TextView loctitle = (TextView)findViewById(R.id.textViewWaypoint);
        locdetails.setText(mWaypoint.getPosition().toString());
        loctitle.setText(mWaypoint.getTitle());
        locname.setText(mWaypoint.getTitle());
    }

    public void smoothMove(Marker target, LatLng from, LatLng to) {

        final Handler handler = new Handler();
        final Interpolator interpolator = new LinearInterpolator();

        final long start = SystemClock.uptimeMillis();
        final float duration = 5000;

        final Marker copyTarget = target;
        target.remove();

        final boolean visible = true;

        handler.post(new Runnable() {
            Random r = new Random();
            double deltaLAT = -0.00005 + (0.00005 - (-0.00005)) * r.nextDouble();
            double deltaLNG = -0.00005 + (0.00005 - (-0.00005)) * r.nextDouble();


            // Based on https://github.com/shohrabuddin/move_markers_in_map_smoothly

            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed/duration);

                double newlat = (t * copyTarget.getPosition().latitude + deltaLAT)
                        + ((1 - t) * copyTarget.getPosition().latitude);
                double newlng = (t * copyTarget.getPosition().longitude + deltaLNG)
                        + ((1 - t) * copyTarget.getPosition().longitude);

                LatLng nextPos = new LatLng(newlat, newlng);

                copyTarget.setPosition(nextPos);

                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    if(visible == true) {
                        copyTarget.setVisible(true);
                    } else {
                        copyTarget.setVisible(false);
                    }
                }
            }
        });

    }

    public void groupSimulation(View view) {


        final ArrayList<Marker> group1 = new ArrayList<Marker>();

        final LatLng unimelb = new LatLng(-37.7963646, 144.9589851);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(unimelb, 15));


        Marker user1 = mMap.addMarker(new MarkerOptions()
                .position(unimelb)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker user2 = mMap.addMarker(new MarkerOptions()
                .position(unimelb)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        Marker user3 = mMap.addMarker(new MarkerOptions()
                .position(unimelb)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        Marker user4 = mMap.addMarker(new MarkerOptions()
                .position(unimelb)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        Marker user5 = mMap.addMarker(new MarkerOptions()
                .position(unimelb)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        group1.add(user1);
        group1.add(user2);
        group1.add(user3);
        group1.add(user4);
        group1.add(user5);


        final Handler handler = new Handler();
        final Interpolator interpolator = new LinearInterpolator();

        final long start = SystemClock.uptimeMillis();
        final float duration = 5000;

        final boolean visible = false;


        for(int i = 0; i < group1.size(); i++) {

            final int j = i;


            handler.post(new Runnable() {
                Random r = new Random();
                double deltaLAT = -0.00005 + (0.00005 - (-0.00005)) * r.nextDouble();
                double deltaLNG = -0.00005 + (0.00005 - (-0.00005)) * r.nextDouble();


                // Based on https://github.com/shohrabuddin/move_markers_in_map_smoothly

                @Override
                public void run() {

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    double newlat = (t * group1.get(j).getPosition().latitude + deltaLAT)
                            + ((1 - t) * group1.get(j).getPosition().latitude);
                    double newlng = (t * group1.get(j).getPosition().longitude + deltaLNG)
                            + ((1 - t) * group1.get(j).getPosition().longitude);

                    LatLng nextPos = new LatLng(newlat, newlng);

                    group1.get(j).setPosition(nextPos);

                    if (t < 1.0) {
                        handler.postDelayed(this, 16);
                    } else {
                        if(visible == true) {
                            group1.get(j).setVisible(true);
                        } else {
                            group1.get(j).setVisible(false);
                        }
                    }
                }
            });

        }

    }
}
