package ark.ark.Map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by SamCh on 2/10/2017.
 */

public class MapWaypoint {
    private String Place_title;
    private LatLng Place_location;
    private String Place_name;
    private String Place_details;
    private String Place_user;
    private boolean active;

    /**
     * Creates a new waypoint
     * @param title
     * @param location
     * @param name
     * @param details
     */
    public MapWaypoint(String title, LatLng location, String name, String details, boolean active) {
        Place_title = title;
        Place_location = location;
        Place_name = name;
        Place_details = details;
        this.active = active;
    }

    /**
     * Returns the title of the waypoint.
     * @return
     */
    public String getTitle() {
        return Place_title;
    }

    /**
     * Gets the location of the place
     * @return LatLng
     */
    public LatLng getLocation() {
        return Place_location;
    }

    /**
     * Gets the name of the Waypoint
     * @return Place Name
     */
    public String getNam() {
        return Place_name;
    }

    /**
     * Gets the details of the place stored
     * @return Place Details
     */
    public String getDetails(){
        return Place_details;
    }

    /**
     * Sets the Waypoint as active
     * @param active
     */
    public void set_active(Boolean active){
        this.active = active;
    }

    /**
     * Returns the status of the waypoint
     * @return
     */
    public boolean getActive(){return active;}

}