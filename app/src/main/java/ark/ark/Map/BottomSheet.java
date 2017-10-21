package ark.ark.Map;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;

import ark.ark.R;

/**
 * Bottom Sheet Class Implementation for TrackAR
 */
public class BottomSheet extends MapNavDrawer {

    private static final int PEEK_HEIGHT = 340;
    private static final int MAX_RESULTS = 1;

    private BottomSheetBehavior mBottomSheetBehavior;
    private Geocoder geocoder;
    private boolean placemode = false;
    private boolean usermode = false;
    private String activeuser;

    /**
     * Initiate bottom sheet.
     * @param bs Bottom Sheet View
     */
    public BottomSheet(View bs, Geocoder geocoder){
        mBottomSheetBehavior = BottomSheetBehavior.from(bs);
        mBottomSheetBehavior.setPeekHeight(PEEK_HEIGHT);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.geocoder = geocoder;
    }

    /**
     * Set Bottom Sheet Text
     * @param title Bold Text
     * @param locname Italic Text below Bold Text
     * @param locdetails Regular Details Text
     * @param v Main Contens view
     */
    private void set_text(String title, String locname, String locdetails, View v){
        TextView bstitle = (TextView) v.findViewById(R.id.bs_title);
        TextView bslocname = (TextView) v.findViewById(R.id.bs_locname);
        TextView bslocdetails = (TextView) v.findViewById(R.id.bs_locdetails);

        bstitle.setText(title);
        bslocname.setText(locname);
        bslocdetails.setText(locdetails);

    }

    /**
     * Set place mode
     * @param v
     * @param waypoint
     * @param user
     */
    public void set_place_mode(View v, MapWaypoint waypoint, Location user){
        View place_layout = v.findViewById(R.id.place_buttons);
        View person_layout = v.findViewById(R.id.person_buttons);
        set_text(waypoint.getTitle(),waypoint.getNam(),waypoint.getDetails(),v);
        if (user != null){
            set_distance_waypoint(v,user, waypoint.getLocation());
        }
        person_layout.setVisibility(View.GONE);
        place_layout.setVisibility(View.VISIBLE);
        usermode = false;
        placemode = true;
    }

    /**
     * Changes bottom sheet to Person Mode.
     */
    public void set_person_mode(View v, LatLng marker, Location user, MapWaypoint wp, String username){

        activeuser = username;
        Location loc = new Location("Temp");
        loc.setLatitude(marker.latitude);
        loc.setLongitude(marker.longitude);

        View place_layout = v.findViewById(R.id.place_buttons);
        View person_layout = v.findViewById(R.id.person_buttons);

        String featurename = null;
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(marker.latitude,
                    marker.longitude, MAX_RESULTS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.get(0)!=null) {

            if ((featurename = addresses.get(0).getFeatureName()) != null) {
                if ((featurename = addresses.get(0).getLocality()) == null){
                    featurename = addresses.get(0).getCountryName();
                }
                else{
                    featurename += ", " + addresses.get(0).getCountryName();
                }

            }

            set_text(username+"'s Location.", featurename, addresses.get(0).getAddressLine(0), v);
            place_layout.setVisibility(View.GONE);
            person_layout.setVisibility(View.VISIBLE);

            set_distance_person(v, user, loc, wp);
            placemode = false;
            usermode = true;
        }
    }

    /**
     * Sets the bottom sheet to the collapsed mode.
     */
    public void set_collapsed(){
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    /**
     * Sets the bottom sheet to the hidden mode.
     */
    public void set_hidden(){
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    /**
     * Sets the bottom sheet to the expanded mode.
     */
    public void set_expanded(){
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * Checks if bottom sheet is collapsed.
     * @return True or False
     */
    public boolean is_collapsed(){
        return (mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED);
    }

    /**
     * Checks if bottom sheet is hidden.
     * @return True or False
     */
    public boolean is_hidden(){
        return (mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_HIDDEN);
    }

    /**
     * Checks if bottom sheet is expanded.
     * @return True or False
     */
    public boolean is_expanded(){
        return (mBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * Gets the BottomSheetBehaviour Instance
     * @return BottomSheetBehavior
     */
    public BottomSheetBehavior get_bsb(){
        return mBottomSheetBehavior;
    }

    private String metres_to_km(float distance){
        if(distance >= 1000.0){
            return (String.format("%.1fkm", distance/1000.0f));
        }
        else{
            return (String.format("%dm", Math.round(distance)));
        }
    }

    /**
     * Sets the distance of user from another user + the distance to the waypoint
     * @param v View
     * @param user User
     * @param other Other User
     * @param wp Waypoint
     */
    public void set_distance_person(View v, Location user, Location other, MapWaypoint wp){
        TextView distance = (TextView) v.findViewById(R.id.bs_distance);
        String distancetext = "";

        if(user != null){
            distancetext += metres_to_km((user.distanceTo(other)))+" Away. ";
        }
        if (wp != null){
            distancetext += metres_to_km((distance_to_waypoint(other,
                    wp.getLocation())))+" from Waypoint.";
        }

        distance.setText(distancetext);
    }

    /**
     * Set the distance form user to the waypoint
     * @param v View
     * @param user User
     * @param wp Waypoint
     */
    public void set_distance_waypoint(View v, Location user, LatLng wp){
        TextView distance = (TextView) v.findViewById(R.id.bs_distance);
        distance.setText((metres_to_km(distance_to_waypoint(user,wp)))+" Away.");
    }

    /**
     * Return the distance to the waypoint
     * @param user User
     * @param wp Waypoint
     * @return Distance (float)
     */
    private float distance_to_waypoint(Location user, LatLng wp){
        Location waypoint = new Location("Point C");
        waypoint.setLatitude(wp.latitude);
        waypoint.setLongitude(wp.longitude);

        return user.distanceTo(waypoint);

    }

    /**
     * Is the bottom sheet currently displaying a location
     * @return true or false
     */
    public boolean is_place_mode(){
        return placemode;
    }

    /**
     * Is the bottom sheet currently displaying a users' info
     * @return true or false
     */
    public boolean is_user_mode(){ return usermode;}

    /**
     * Returns the user whose info is being shown
     * @return email
     */
    public String get_active_user(){
        return activeuser;
    }

    /**
     * Stops the bottom sheet from showing the place info. (Called when delete waypoibnt is called)
     */
    public void removeplacemode(){
        placemode = false;
    }

}
