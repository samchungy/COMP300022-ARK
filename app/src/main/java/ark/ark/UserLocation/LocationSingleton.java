package ark.ark.UserLocation;

import android.location.Location;

/**
 * Created by khtin on 15/09/2017.
 */

public class LocationSingleton {
    private static final LocationSingleton ourInstance = new LocationSingleton();
    private Location loc;
    private boolean updateToServer = false;

    public static LocationSingleton getInstance() {
        return ourInstance;
    }

    public void setLocation(Location location) {
        loc = location;
    }

    public Location getLocation(){
        return loc;
    }

    public void allowServerUpdates() {
        updateToServer = true;
    }

    public void stopServerUpdates() {
        updateToServer = false;
    }

    public boolean isAllowingUpdates(){
        return updateToServer;
    }

    private LocationSingleton() {
    }
}
