package ark.ark.Map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;

import ark.ark.R;

/**
 * Bottom Sheet Class Implementation for TrackAR
 */
public class BottomSheet extends MapNavDrawer {

    private static final int PEEK_HEIGHT = 300;
    private static final int MAX_RESULTS = 1;

    private BottomSheetBehavior mBottomSheetBehavior;
    private Geocoder geocoder;

    /**
     *
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
     * Changes bottom sheet to Place Mode.
     */
    public void set_place_mode(View v, MapWaypoint waypoint){
        View place_layout = v.findViewById(R.id.place_buttons);
        View person_layout = v.findViewById(R.id.person_buttons);

        set_text(waypoint.getTitle(),waypoint.getNam(),waypoint.getDetails(), v);
        person_layout.setVisibility(View.GONE);
        place_layout.setVisibility(View.VISIBLE);
    }

    /**
     * Changes bottom sheet to Person Mode.
     */
    public void set_person_mode(View v, Marker marker){
        String featurename = null;
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(marker.getPosition().latitude,
                    marker.getPosition().longitude, MAX_RESULTS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        View place_layout = v.findViewById(R.id.place_buttons);
        View person_layout = v.findViewById(R.id.person_buttons);

        if((featurename = addresses.get(0).getFeatureName()) != null){
            featurename = addresses.get(0).getLocality() +", "+ addresses.get(0).getCountryName();
        }

        set_text(marker.getTitle(),featurename,addresses.get(0).getAddressLine(0) +", "+
                addresses.get(0).getLocality() + ", " + addresses.get(0).getPostalCode(), v);
        place_layout.setVisibility(View.GONE);
        person_layout.setVisibility(View.VISIBLE);
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

}
