package ark.ark.UserLocation;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Zengster on 03/10/2017.
 */

public class ArkMarker implements ClusterItem {
    private int id;
    private LatLng position;
    private String title;
    private String snippet;

    public ArkMarker(double lat, double lng) {
        position = new LatLng(lat, lng);
    }

    public ArkMarker(int newID, LatLng start) {
        id = newID;
        position = start;
    }

    public ArkMarker(double lat, double lng, String newTitle, String newSnippet) {
        position = new LatLng(lat, lng);
        title = newTitle;
        snippet = newSnippet;
    }

    public void setPosition(LatLng newPos) {
        position = newPos;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

}
