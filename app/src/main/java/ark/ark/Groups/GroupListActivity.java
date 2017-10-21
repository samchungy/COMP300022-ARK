package ark.ark.Groups;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;
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
//        ListAdapter = new GroupProfileAdapter(this, dataList);
//        listView.setAdapter(ListAdapter);
//
//
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

//    public void addFriend(View v){
//
//
//        EditText emailTextField = (EditText) findViewById(R.id.friend_email);
//        String email = emailTextField.getText().toString();
//        Group data = new Group(email);
//
//        ListAdapter.addToList(data);
//        ToastUtils.showToast("count: " + ListAdapter.getCount(),getApplicationContext());
//
//        listView.setAdapter(ListAdapter);
//        emailTextField.setText("");
//
//    }

    public void goToAddGroup(View v){
        Intent myIntent = new Intent(GroupListActivity.this, GroupCreationActivity.class);
        startActivity(myIntent);
        this.finish();
    }

    public void goToJoinGroup(View v){
        Intent myIntent = new Intent(GroupListActivity.this, JoinGroupActivity.class);
        startActivity(myIntent);
        this.finish();
    }


    public int getGroups(){
        return CurrentUser.getInstance().getAllGroups().size();
    }



}
