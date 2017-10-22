package ark.ark.Profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;

import ark.ark.Groups.CurrentUser;
import ark.ark.Groups.Group;
import ark.ark.R;

public class GroupListActivity extends AppCompatActivity {

    ListView listView ;
    private GroupProfileAdapter ListAdapter;
    private ArrayList<Group> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        // Get ListView object from xml
//        listView = (ListView) findViewById(R.id.list);
//        dataList = new ArrayList<Group>(CurrentUser.getInstance().getAllGroups().values());
//        // ListView Item Click Listener
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//
//                ToastUtils.showToast(dataList.get(position).getName(),getApplicationContext());
//            }
//        });


    }


    /**
     * Sends the user to the Group Creation Activity
     * @param v - the current view
     */
    public void goToAddGroup(View v){
        Intent myIntent = new Intent(GroupListActivity.this, GroupCreationActivity.class);
        startActivity(myIntent);
        this.finish();
    }

    /**
     * Sends the user to the Join Group Activity
     * @param v - the current view
     */
    public void goToJoinGroup(View v){
        Intent myIntent = new Intent(GroupListActivity.this, JoinGroupActivity.class);
        startActivity(myIntent);
        this.finish();
    }

    /**
     * Gets the current user's groups
     * @return the current user's groups
     */
    public int getGroups(){
        return CurrentUser.getInstance().getAllGroups().size();
    }



}
