package ark.ark.Map;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import ark.ark.ArActivity;
import ark.ark.Authentication.ARK_auth;
import ark.ark.Chat.ChatLogActivity;
import ark.ark.Debugging;
import ark.ark.Groups.CurrentUser;
import ark.ark.Groups.Friend;
import ark.ark.Groups.Group;
import ark.ark.Profile.GroupListActivity;
import ark.ark.Groups.GroupLocationUpdateService;
import ark.ark.Groups.UserRequestsUtil;
import ark.ark.Profile.LoginActivity;
import ark.ark.R;
import ark.ark.ToastUtils;
import ark.ark.UserLocation.LocationSingleton;
import ark.ark.UserLocation.LocationUpdateService;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Map + Nav Drawer Class
 */
public class MapNavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        Observer,
        DrawerLayout.DrawerListener{

    //Services
    Intent mLocUpdateService;
    Intent mGroupLocUpdateService;

    //Map Stuff
    private GoogleMap mMap;
    private UiSettings uiSettings;
    private Marker mWaypoint = null;
    private Geocoder geocoder;
    private MapWaypoint undobk;
    private String useremail;
    private LocationSingleton mCurrentLocation;
    private BottomSheet bs;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mylocation;
    private HashMap<String, Marker> mGroup;
    private HashMap<Integer, String> idToEmail = new HashMap<>();
    private CurrentUser curruser;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ClipboardManager clipboard;

    // Stuff for Zengster for navigation drawer
    private ImageView profilePicture;
    private TextView currentUserName, currentUserGroup;
    private View drawerHeader;
    boolean isLoaded = false;
    boolean isPopulated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_nav_drawer);
        isLoaded = false;
        clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);

        //showToast(ARK_auth.fetchSessionId(getApplicationContext()));
        if(ARK_auth.fetchSessionId(getApplicationContext()).equals("no session id")) {
            Intent myIntent2 = new Intent(this, LoginActivity.class);
            startActivity(myIntent2);
            this.finish();
        }
        else if(ARK_auth.fetchGroup(getApplicationContext()) == null) {
            Intent myIntent2 = new Intent(this, GroupListActivity.class);
            startActivity(myIntent2);
            this.finish();
        }

        //Get User from Server
        curruser = CurrentUser.getInstance();
        curruser.addObserver(this);
        curruser.logOn(this);
        UserRequestsUtil.initialiseCurrentUser(this);

        //Location Updates to Server + Group Location Updates from Server
        mLocUpdateService = new Intent(this, LocationUpdateService.class);
        mGroupLocUpdateService = new Intent(this, GroupLocationUpdateService.class);
        mCurrentLocation = LocationSingleton.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
                startService(mLocUpdateService);
                startService(mGroupLocUpdateService);
                mCurrentLocation.addObserver(this);
            }
        }

        // Map Initiate
        //Initialise Map
        MapsInitializer.initialize(getApplicationContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Get User
        curruser = CurrentUser.getInstance();
        curruser.addObserver(this);


        // Assorted drawer code. Get current username and group to show in header.
        drawerHeader = navigationView.getHeaderView(0);
        currentUserName = (TextView) drawerHeader.findViewById(R.id.userEmail);
        currentUserGroup = (TextView) drawerHeader.findViewById(R.id.activeGroup);

        // Picture processing uses library from https://github.com/amulyakhare/TextDrawable
        TextDrawable drawable = TextDrawable.builder().buildRound(
                curruser.getEmail().substring(0, 1).toUpperCase(), Color.rgb(48, 63, 159));
        profilePicture = (ImageView) drawerHeader.findViewById(R.id.profileImg);
        profilePicture.setImageDrawable(drawable);


        // Geocoder
        geocoder = new Geocoder(this);

        //Bottom Sheet Initiate
        View bs_view = findViewById(R.id.bottom_sheet);
        bs = new BottomSheet(bs_view, geocoder);
        bs.set_hidden();

        //Google Places Initialise
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        hide_fab();
    }

    /**
     * Initialise all the content which requires data from the server.
     */
    private void onload(){
        initiateDrawer();

        // Bottom Sheet Initialize
        View bs_view = findViewById(R.id.bottom_sheet);
        bs = new BottomSheet(bs_view, geocoder);

        bs_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bs.is_collapsed()) {
                    bs.set_expanded();
                } else {
                    bs.set_collapsed();
                }
            }
        });

        bs.get_bsb().setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                if (bs.is_hidden()) {
                    changeFAB(fab, R.drawable.map_marker_radius, R.color.colorPrimaryDark);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });

        // FAB 1 - Set Waypoint Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bs.is_collapsed()) {
                    bs.set_expanded();
                } else if (mWaypoint != null) {
                    bs.set_place_mode(findViewById(android.R.id.content),
                            (MapWaypoint) mWaypoint.getTag(), get_location());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mWaypoint.getPosition(), 15));
                    bs.set_collapsed();
                } else {
                    selectPlace(view);
                }
            }
        });

        // FAB - Messages Button
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                RequestQueue queue = Volley.newRequestQueue(MapNavDrawer.this);

                String server ="52.65.97.117";
                String requestURL = "http://" + server + "/group/convo?group_id=" + CurrentUser.getInstance().getActiveGroup().getId();


                // Request a string response from the requestURL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // after getting response, try reading the json
                                try {
                                    JSONObject res = new JSONObject(response);
                                    if (res.getString("success").equals("ok")) {
                                        String convo_id = res.getString("conversation_id");

                                        Intent mIntent = new Intent(MapNavDrawer.this, ChatLogActivity.class);
                                        // passing variables to the new activity
                                        mIntent.putExtra("nickname", CurrentUser.getInstance().getActiveGroup().getName());
                                        mIntent.putExtra("conversation_id", convo_id);
                                        // starting up the new activity
                                        startActivity(mIntent);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast("cannot connect to server", MapNavDrawer.this);
                    }
                });

                queue.add(stringRequest);
            }
        });

        // FAB - AR Shortcut
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent2 = new Intent(MapNavDrawer.this, ArActivity.class);
                startActivity(myIntent2);
            }
        });

        hide_fab();

        //Initialise Group
        useremail = curruser.getEmail();
        mGroup = new HashMap<>();

        Group group = curruser.getActiveGroup();
        if (group != null){
            if (group.getFriends() != null){
                for(Map.Entry<String, Friend> entry : group.getFriends().entrySet()){
                    LatLng loc = new LatLng(entry.getValue().getLocation().getLatitude(),
                            entry.getValue().getLocation().getLongitude());
                    set_person_marker(loc,entry.getValue().getEmail());
                }
            }
            else{
                Log.d("Group has no frands","no friends");
            }
        }
        else{
            Log.d("Group is null","null");
        }
        Log.d("Group", mGroup.toString());

        if (curruser.getActiveGroup().getWaypoint().getActive() != false) {
            setWaypoint(curruser.getActiveGroup().getWaypoint());
        }
         show_fab();
        isLoaded = true;

    }


    /**
     * Sets up the nav drawer
     */
    private void initiateDrawer() {

        // In the rare event a current user does not exist, this prevents the app from crashing.
        if(curruser != null) {
            currentUserName.setText(curruser.getEmail());
            currentUserGroup.setText(curruser.getActiveGroup().getName());
        } else {
            currentUserName.setText("Not set");
            currentUserGroup.setText("Not set");
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        final Menu menu = navigationView.getMenu();
        int j = 1;

        // Refresh the nav drawer every time it is closed.
        if(isPopulated == true) {
            invalidateOptionsMenu();
            menu.removeItem(0);
            for(int key: idToEmail.keySet()) {
                menu.removeItem(key);
            }
        }

        // Links to the debugging class which is a large part of what we used to test the
        // app's functionality.
        menu.add(0, 0, 0, "Debugging").setIcon(R.drawable.ic_settings_black_24dp);

        // Iterate through the current user's group members and add them to the nav drawer.
        for(Friend tempFriend: CurrentUser.getInstance().getActiveGroup().getFriends().values()) {
            menu.add(0, j, 0, tempFriend.getEmail()).setIcon(R.drawable.ic_person_black_24dp);
            idToEmail.put(j, tempFriend.getEmail());
            j++;
            isPopulated = true;
        }

    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Uses Google Places API to select a waypoint.
     * @param view
     */
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

    /**
     * Sets a waypoint for the group
     * @param wp Waypoint
     */
    public void setWaypoint(MapWaypoint wp) {

        add_waypoint_marker(wp.getLocation(), wp.getTitle(), wp);
        curruser.getActiveGroup().setWaypoint(wp.getLocation().latitude,
                wp.getLocation().longitude, useremail, wp.getNam(), wp.getDetails(), true);

        mMap.animateCamera(CameraUpdateFactory.newLatLng(wp.getLocation()));
        bs.set_collapsed();
    }

    /**
     * Update Waypoint for the group
     * @param wp Waypoint
     */
    public void updateWaypoint(MapWaypoint wp){
        if (mWaypoint != null) {
            Log.d("Waypoint removed", "Removed");
            mWaypoint.remove();
            mWaypoint = null;
        }

        setWaypoint(wp);
        UserRequestsUtil.sendActiveWaypointToServer(this);

    }

    /**
     * Updates the current waypoint using the one stored in the server.
     * @param wp Waypoint
     */
    public void update_from_server_waypoint(MapWaypoint wp){
        if (mWaypoint != null){
            mWaypoint.remove();
            mWaypoint = null;
        }

        mWaypoint = mMap.addMarker(new MarkerOptions()
                .position(wp.getLocation())
                .title(wp.getTitle())
        );
        mWaypoint.setTag(wp);

        curruser.getActiveGroup().setWaypoint(wp.getLocation().latitude,
                wp.getLocation().longitude, useremail, wp.getNam(), wp.getDetails(), true);

        ToastUtils.showToast("The Group Waypoint Was Updated.", getApplicationContext());

    }


    /**
     * Adds a waypoint when no waypoint exists.
     * @param pos LatLng
     * @param title Waypoint Title
     * @param mw Waypoint
     */
    public void add_waypoint_marker(LatLng pos, String title, MapWaypoint mw) {
        mWaypoint = mMap.addMarker(new MarkerOptions()
                .position(pos)
                .title(title)
        );
        mWaypoint.setTag(mw);

        bs.set_place_mode(findViewById(android.R.id.content), mw, get_location());
    }

    /**
     * Deletes the current waypoint when the server waypoint is deleted
     */
    public void server_delete_waypoint(){
        if (mWaypoint != null){
            mWaypoint.remove();
            mWaypoint = null;
            ToastUtils.showToast("The Group Waypoint Was Deleted.", getApplicationContext());
        }

        if (bs.is_place_mode()){
            bs.set_hidden();
            bs.removeplacemode();
        }

    }

    /**
     * Deletes the current waypoint
     * @param view
     */
    public void deleteWaypoint(View view) {
        if (mWaypoint != null) {
            undobk = (MapWaypoint) mWaypoint.getTag();
            mWaypoint.remove();
            mWaypoint = null;
        }
        hide_fab();

        bs.removeplacemode();
        bs.set_hidden();

        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.bottom_sheet_layout),
                R.string.waypointremoved, Snackbar.LENGTH_LONG);
        mySnackbar.setAction(R.string.undo_string, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_waypoint_marker(undobk.getLocation(), undobk.getTitle(), undobk);

            }
        }).addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar mySnackbar, int event) {
                show_fab();
                remove_waypoint_server();
            }
        });
        mySnackbar.show();
    }

    /**
     * Deletes the waypoint from server
     */
    public void remove_waypoint_server(){
        curruser.getActiveGroup().deleteWaypoint();
        UserRequestsUtil.sendActiveWaypointToServer(this);
    }

    /**
     * Sets the markers for people on the Google Map
     * @param lat LatLng
     * @param name Name of the Person
     */
    public void set_person_marker(LatLng lat, String name) {
        IconGenerator iconFactory = new IconGenerator(this);
        Marker mPerson;
        Drawable d = getResources().getDrawable(R.drawable.ic_person_black_24dp);
        mPerson = mMap.addMarker(new MarkerOptions()
                .position(lat)
                .title(name +  "'s Location.")
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon((""+name.charAt(0)).
                        toUpperCase())))
        );
        mPerson.setTag(name);
        mGroup.put(name,mPerson);
    }

    /**
     * Changes the first FAB Icon/Colour
     * @param fab FloatingActionButton
     * @param icon Icon from Resources
     * @param colour Colour of the icon
     */
    public void changeFAB(FloatingActionButton fab, int icon, int colour) {
        fab.setImageResource(icon);
    }

    /**
     * Updates the position of a person when their location updates at the server
     * @param l The Lagitude and Longtitude
     * @param email The email of the user being updated
     */
    private void update_position(LatLng l, String email){
        if(mGroup.get(email) == null){
            Log.d("group null","null " + email + mGroup.toString());
        }
        if((mGroup.get(email) != null && !(mGroup.get(email).getPosition().equals(l)))){
            animateMarker(mGroup.get(email),l, false);
            mGroup.get(email).setPosition(l);
            if (bs.is_user_mode() && email.equals(bs.get_active_user())){
                if (mWaypoint != null){
                    bs.set_person_mode(findViewById(android.R.id.content), mGroup.get(email).getPosition(), get_location(),
                            (MapWaypoint) mWaypoint.getTag(),(String) mGroup.get(email).getTag());
                }
                else{
                    bs.set_person_mode(findViewById(android.R.id.content), mGroup.get(email).getPosition(), get_location(),
                            null, (String) mGroup.get(email).getTag());
                }
            }
        }
    }


    /**
     * Gets the current location of you.
     * @return Location (Your Location)
     */
    private Location get_location() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mylocation = location;
                        }
                    }
                });
        return mylocation;
    }

    /**
     * Hides the floating action buttons
     */
    public void hide_fab(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab.setVisibility(View.GONE);
        fab2.setVisibility(View.GONE);
        fab3.setVisibility(View.GONE);
    }

    /**
     * Shows the Floating action buttons
     */
    public void show_fab(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab.setVisibility(View.VISIBLE);
        fab2.setVisibility(View.VISIBLE);
        fab3.setVisibility(View.VISIBLE);
    }

    /**
     * Opens the nav drawer
     * @param view
     */
    public void open_drawer(View view){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(Gravity.LEFT);
    }

    /**
     * Checks for Location Permissions
     * @return true or false
     */
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapNavDrawer.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Copys the Bottom Sheet Text
     * @param view
     */
    public void copyText(View view){
        if (bs.is_user_mode() || bs.is_place_mode()){
            TextView locdetails;
            TextView locname;
            locdetails = (TextView) this.findViewById(R.id.bs_locdetails);
            locname = (TextView) this.findViewById(R.id.bs_locname);
            ClipData clip = ClipData.newPlainText("place",locname.getText().toString() + ", "+
                    locdetails.getText().toString());
            clipboard.setPrimaryClip(clip);
            ToastUtils.showToast("Location Copied to Clipboard", getApplicationContext());
        }

    }

    /**
     * Smooth Animation of Marker taken from: https://github.com/googlemaps/android-samples/blob/master/ApiDemos/app/src/main/java/com/example/mapdemo/MarkerDemoActivity.java
     * @param marker
     * @param toPosition
     * @param hideMarker
     */
    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (bs.is_expanded()) {
            bs.set_collapsed();
        } else if (bs.is_collapsed()) {
            bs.set_hidden();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = null;

        // Go to debugging activity.
        if(id == 0) {
            intent = new Intent(this, Debugging.class);
            startActivity(intent);
            return true;
        }

        // Animate smoothly and center over the group member selected in the nav drawer.
        for(int friendID: idToEmail.keySet()) {
            if (id == friendID) {
                LatLng moveCamera = new LatLng(
                        mGroup.get(idToEmail.get(id)).getPosition().latitude,
                        mGroup.get(idToEmail.get(id)).getPosition().longitude
                );
                if (mMap.getCameraPosition().zoom <= 15.0) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(moveCamera, 15));
                }
                else{
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(moveCamera,
                            mMap.getCameraPosition().zoom));
                }

                if (mWaypoint != null){
                    bs.set_person_mode(findViewById(android.R.id.content),
                            mGroup.get(idToEmail.get(id)).getPosition(), get_location(),
                            (MapWaypoint) mWaypoint.getTag(),idToEmail.get(id));
                }
                else{
                    bs.set_person_mode(findViewById(android.R.id.content),
                            mGroup.get(idToEmail.get(id)).getPosition(), get_location(),
                            null, idToEmail.get(id));
                }
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                changeFAB(fab, R.drawable.ic_person_black_24dp, R.color.cyan);
                bs.set_collapsed();
            }
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
        mMap.setPadding(0,100,0,0);

    }

    @Override
    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "Travelling to your current location...", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (bs.is_expanded()) {
            bs.set_collapsed();
        } else {
            fab.setImageResource(R.drawable.map_marker_radius);
            bs.set_hidden();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (mMap.getCameraPosition().zoom <= 15.0) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
        }
        else{
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),
                    mMap.getCameraPosition().zoom));
        }

        if (marker.getTag() instanceof MapWaypoint) {
            bs.set_place_mode(findViewById(android.R.id.content), (MapWaypoint) marker.getTag(),
                    get_location());
            changeFAB(fab, R.drawable.map_marker_radius, R.color.colorPrimaryDark);
        } else {
            if (mWaypoint != null){
                bs.set_person_mode(findViewById(android.R.id.content), marker.getPosition(), get_location(),
                        (MapWaypoint) mWaypoint.getTag(),(String) marker.getTag());
            }
            else{
                bs.set_person_mode(findViewById(android.R.id.content), marker.getPosition(), get_location(),
                        null, (String) marker.getTag());
            }

            changeFAB(fab, R.drawable.ic_person_black_24dp, R.color.cyan);
        }

        bs.set_collapsed();
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int PLACE_PICKER_REQUEST = 1;
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                updateWaypoint(new MapWaypoint(useremail + "'s Waypoint.", place.getLatLng(),
                        place.getName().toString(), place.getAddress().toString(), true));
            }
        }
    }


    @Override
    public void update(Observable o, Object data) {

        if (isLoaded){
            HashMap<String, Friend> friendslist = null;
            Location location = get_location();


            /**
             * If CurrentUser updates and it sends back an email
             * (Indicating that the location of that email has change)
             */
            if (o == curruser && data instanceof String){
                String email = (String)data;
                Location loc = curruser.getActiveGroup().getFriend(email).getLocation();

                if (loc != null){
                    LatLng l = new LatLng(loc.getLatitude(),loc.getLongitude());
                    update_position(l, email);
                }

            }


            /**
             * If CurrentUser updates its waypoint and sends back a waypoint
             */

            if (o == curruser && data instanceof MapWaypoint) {
                if (((MapWaypoint) data).getActive() != false){
                    if (mWaypoint == null || !((MapWaypoint) data).getLocation().equals(mWaypoint.getPosition())){
                        update_from_server_waypoint((MapWaypoint)data);
                    }
                }
                else{
                    server_delete_waypoint();
                }

                if(bs.is_place_mode() && mWaypoint != null){
                    bs.set_place_mode(findViewById(android.R.id.content), (MapWaypoint) mWaypoint.getTag(),
                            get_location());
                }
            }

            if (o == mCurrentLocation){
                if (bs.is_place_mode()) {
                    if (location != null && mWaypoint != null){
                        bs.set_distance_waypoint(findViewById(android.R.id.content),location,
                                mWaypoint.getPosition()
                        );
                    }
                }
            }

            if(bs.is_user_mode()){
                if (mWaypoint != null){
                    bs.set_distance_person(findViewById(android.R.id.content),
                            location, curruser.getActiveGroup().getFriends().get(bs.get_active_user())
                                    .getLocation(), (MapWaypoint) mWaypoint.getTag());
                }
                else{
                    bs.set_distance_person(findViewById(android.R.id.content),
                            location, curruser.getActiveGroup().getFriends().get(bs.get_active_user())
                                    .getLocation(), null);
                }

            }

        }
        else{
            Log.d("NOT initialised", "boourns");
            if (curruser.isInitiated()){
                onload();
            }
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDrawerSlide(View view, float v) {

    }

    @Override
    public void onDrawerOpened(View view) {

    }

    @Override
    public void onDrawerClosed(View view) {
        if(isLoaded){
            initiateDrawer();
        }
    }

    @Override
    public void onDrawerStateChanged(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        startService(mLocUpdateService);
                        startService(mGroupLocUpdateService);
                        mCurrentLocation.addObserver(this);
                        enableMyLocation();
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
                startService(mLocUpdateService);
                startService(mGroupLocUpdateService);
                mCurrentLocation.addObserver(this);
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            }
        }
        if ((curruser == null || curruser.getActiveGroup() == null)&& !curruser.isInitialising()){
            isLoaded = false;
            UserRequestsUtil.initialiseCurrentUser(this);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        if ((curruser == null || curruser.getActiveGroup() == null)&& !curruser.isInitialising()){
            isLoaded = false;
            UserRequestsUtil.initialiseCurrentUser(this);
        }
    }

}
