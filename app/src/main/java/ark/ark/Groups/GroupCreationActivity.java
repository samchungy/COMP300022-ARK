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

import ark.ark.R;
import ark.ark.ToastUtils;

public class GroupCreationActivity extends AppCompatActivity {

//    ListView listView ;
//    private FriendListAdapter ListAdapter;
//    private ArrayList<FriendListAdapter.LFriend> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);
//
//        // Get ListView object from xml
//        listView = (ListView) findViewById(R.id.list);
//        dataList = new ArrayList<>();
//        ListAdapter = new FriendListAdapter(this, dataList);
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
//                ToastUtils.showToast("position: "+position, getApplicationContext());
//            }
//        });
//
//        ToastUtils.showToast("count: " + ListAdapter.getCount(),getApplicationContext());

    }
//
//    public void addFriend(View v){
//
//
//        EditText emailTextField = (EditText) findViewById(R.id.friend_email);
//        String email = emailTextField.getText().toString();
//        FriendListAdapter.LFriend data = new FriendListAdapter.LFriend(email);
//
//        ListAdapter.addToList(data);
//        ToastUtils.showToast("count: " + ListAdapter.getCount(),getApplicationContext());
//
//        listView.setAdapter(ListAdapter);
//        emailTextField.setText("");
//
//    }

    public void addGroup(View v){
        ToastUtils.showToast("adding group...",getApplicationContext());
        EditText groupNameTextField = (EditText) findViewById(R.id.gName);
        UserRequestsUtil.postGroupCreation(groupNameTextField.getText().toString(),getApplicationContext());
        this.finish();
    }




}
