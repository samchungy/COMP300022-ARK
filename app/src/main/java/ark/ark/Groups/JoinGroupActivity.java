package ark.ark.Groups;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ark.ark.R;
import ark.ark.ToastUtils;

public class JoinGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
    }

    public void joinGroup(View v){

        EditText groupID = (EditText)findViewById(R.id.groupIDinput);
        String email = CurrentUser.getInstance().getEmail();
        UserRequestsUtil.postAddUserToGroup(email, groupID.getText().toString(), this);
    }

}
