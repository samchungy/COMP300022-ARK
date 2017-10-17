package ark.ark.Groups;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.util.HashMap;
import java.util.Observable;

import ark.ark.Authentication.ARK_auth;
import ark.ark.UserLocation.LocationSingleton;

/**
 * Created by khtin on 21/09/2017.
 */

public class CurrentUser extends Observable{
    private static final CurrentUser ourInstance = new CurrentUser();
    private String nickname;
    private String email;
    private HashMap<String, Group> groups = new HashMap<String, Group>();
    private Group activeGroup;
    private Boolean isUpdating = true;
    private Boolean isInitiated = false;
    private Boolean isInitialising = false;
    private Boolean groupisInitiated = false;
    private Boolean waypointisInitiated = false;
    private int groupLocations = 0;

    public static CurrentUser getInstance() {
        return ourInstance;
    }

    public void logOn(Context context) {
        this.email = ARK_auth.fetchUserEmail(context);
    }

    public void logOut(){
        this.email = null;
        this.groups.clear();
        this.activeGroup = null;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {return nickname; }

    public void switchActiveGroup(String groupID) {
        if(groups.containsKey(groupID)) {
            activeGroup = groups.get(groupID);
        }
    }

    public Group getActiveGroup() {
        return activeGroup;
    }

    public void setActiveGroupLocation(String email, Location loc) {
        if(activeGroup.getFriend(email) != null){
            activeGroup.setLocation(email, loc);
            setChanged();
            notifyObservers(email);
        }
    }

    public void updateActiveGroupFriend(Friend friend) {
        activeGroup.updateFriend(friend);
        //setChanged();
        //notifyObservers();
    }


    /**
     * Changing the active group waypoints
     * Notifies observers with waypoint
     * @param lat
     * @param lng
     * @param creator
     * @param placename
     * @param placeaddress
     * @param active
     */
    public void updateActiveGroupWaypoint(Double lat, Double lng, String creator, String placename,
                            String placeaddress, Boolean active) {
        activeGroup.setWaypoint(lat, lng, creator, placename, placeaddress, active);
        setChanged();
        notifyObservers(activeGroup.getWaypoint());
    }


    public boolean isUpdating() {
        return isUpdating;
    }

    public HashMap<String,Group> getAllGroups() {
        return groups;
    }

    public void addGroup(Group group){
        if(!groups.containsKey(group.getId())) {
            groups.put(group.getId(), group);
            if(groups.size() == 1) {
                switchActiveGroup(group.getId());
            }
        }
    }

    public boolean isInitialising(){ return isInitialising;}

    public boolean isInitiated(){
        return isInitiated;
    }

    public void setIsInitiated(){
        if (groupisInitiated && waypointisInitiated){
            Log.d("User Initiated","DONE");
            isInitialising = false;
            isInitiated = true;
            Log.d("Group Friends:", this.getActiveGroup().getFriends().toString());
            LocationSingleton.getInstance().notifyObservers();
        }
    }

    public void setIsInitialising(){
        isInitialising = true;
    }

    public void setWaypointisInitiated(){
        waypointisInitiated = true;
        Log.d("Waypoint inited","yes");
    }

    public void setGroupInitiated(){
        groupisInitiated = true;
        Log.d("Group Locations inits",activeGroup.toString());
    }
}
