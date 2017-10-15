package ark.ark.Groups;

import android.content.Context;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import ark.ark.Authentication.ARK_auth;
import ark.ark.ToastUtils;

/**
 * Created by khtin on 21/09/2017.
 */

public class CurrentUser extends Observable{
    private static final CurrentUser ourInstance = new CurrentUser();
    //private String nickname;
    private String email;
    private HashMap<String, Group> groups = new HashMap<String, Group>();
    private Group activeGroup;
    private Boolean isUpdating = false;

    public static CurrentUser getInstance() {
        return ourInstance;
    }

    public void logOn(String email, Context context) {
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

    public void switchActiveGroup(String groupID) {
        if(groups.containsKey(groupID)) {
            activeGroup = groups.get(groupID);
        }
    }

    public Group getActiveGroup() {
        return activeGroup;
    }

    public void setActiveGroupLocation(String email, Location loc) {
        activeGroup.setLocation(email, loc);
        setChanged();
        notifyObservers();
    }

    public void updateActiveGroupFriend(Friend friend) {
        activeGroup.updateFriend(friend);
        setChanged();
        notifyObservers();
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
}
