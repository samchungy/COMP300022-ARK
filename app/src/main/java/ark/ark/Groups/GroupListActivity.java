package ark.ark.Groups;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ark.ark.HomeActivity;
import ark.ark.Profile.LoginActivity;
import ark.ark.R;
import ark.ark.ToastUtils;

public class GroupListActivity extends AppCompatActivity {

    ListView listView ;
    private GroupProfileAdapter ListAdapter;
    private ArrayList<Group> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        dataList = new ArrayList<Group>(CurrentUser.getInstance().getAllGroups().values());
        ListAdapter = new GroupProfileAdapter(this, dataList);
        listView.setAdapter(ListAdapter);


        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ToastUtils.showToast(dataList.get(position).getName(),getApplicationContext());
            }
        });


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
    }

    public void goToJoinGroup(View v){
        Intent myIntent = new Intent(GroupListActivity.this, JoinGroupActivity.class);
        startActivity(myIntent);
    }


    public int getGroups(){
        return CurrentUser.getInstance().getAllGroups().size();
    }



}
