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

    /**
     * Creates a new waypoint
     * @param title
     * @param location
     * @param name
     * @param details
     */
    public MapWaypoint(String title, LatLng location, String name, String details) {
        Place_title = title;
        Place_location = location;
        Place_name = name;
        Place_details = details;
    }

    public String getTitle() {
        return Place_title;
    }

    public LatLng getLocation() {
        return Place_location;
    }

    public String getName() {
        return Place_name;
    }

    public String getDetails(){
        return Place_details;
    }

}