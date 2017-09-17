package ark.ark;

/**
 * Created by khtin on 15/09/2017.
 */

public class LocationDataSingleton {
    static LocationDataSingleton instance = null;

    public LocationDataSingleton getInstance() {
        if (instance == null) {
            instance = new LocationDataSingleton();
            return instance;
        } else {
            return instance;
        }
    }


}
