package ark.ark.Profile;

import android.location.Location;

/**
 * Created by khtin on 15/09/2017.
 */

public class LocationSingleton {
    private static final LocationSingleton ourInstance = new LocationSingleton();
    private Location loc;

    public static LocationSingleton getInstance() {
        return ourInstance;
    }

    public void setLocation(Location location) {
        loc = location;
    }

    public Location getLocation(){
        return loc;
    }

    private LocationSingleton() {
    }
}
