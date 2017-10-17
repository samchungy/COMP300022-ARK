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

    public String getTitle() {
        return Place_title;
    }

    public LatLng getLocation() {
        return Place_location;
    }

    public String getNam() {
        return Place_name;
    }

    public String getDetails(){
        return Place_details;
    }

    public void set_active(Boolean active){
        this.active = active;
    }

    public boolean getActive(){return active;}

}