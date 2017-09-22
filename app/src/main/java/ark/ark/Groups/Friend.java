package ark.ark.Groups;

import android.location.Location;

/**
 * Created by khtin on 20/09/2017.
 */

public class Friend {
    private String email;
    private Location loc;

    public Friend(String email) {
        this.email = email;
    }

    public void setLocation(Location location) {
        if(loc.getLongitude() != location.getLongitude()
                || loc.getLongitude() != location.getLongitude())
            loc.set(location);
    }
    public Location getLocation(){
        return loc;
    }

    public String getEmail() {
        return email;
    }

}
