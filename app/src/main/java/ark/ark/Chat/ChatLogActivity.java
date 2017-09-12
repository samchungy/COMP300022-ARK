package ark.ark.Chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ark.ark.Authentication.ARK_auth;
import ark.ark.R;

public class ChatLogActivity extends AppCompatActivity {

    private MessageListAdapter mAdapter;
    private ArrayList<MessageListAdapter.message> messageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_log);

        setup();
    }


    private void setup() {
        // getting variables from the sender
        String nickname = getIntent().getStringExtra("nickname");
        setTitle(nickname);

        setupMessageList();
    }




    private void setupMessageList() {
        // TODO: message list setup
        ListView messageListView = (ListView)findViewById(R.id.austin_MessageListView);
        messageList = new ArrayList<>();
        mAdapter = new MessageListAdapter(this, messageList);
        messageListView.setAdapter(mAdapter);

        reloadAllMessages();
    }


    private void reloadAllMessages() {
        // TODO: load all msgs
        RequestQueue queue = Volley.newRequestQueue(this);

        String server ="52.65.97.117";
        String conversationId = getIntent().getStringExtra("conversation_id");
        String requestURL = "http://" + server + "/message/show?conversation_id=" + conversationId;


        // Request a string response from the requestURL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // after getting response, try reading the json
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("success").equals("ok")) {
                                // iterate thru the direct list to populate the convo list
                                for (int i=0;i<res.getJSONArray("messageList").length();i++) {
                                    // getting attributes from the json
                                    String messageEncrypted = res.getJSONArray("messageList").getJSONObject(i).getString("message_body");
                                    String senderEmail = res.getJSONArray("messageList").getJSONObject(i).getString("sender_email");
                                    String senderNickname = res.getJSONArray("messageList").getJSONObject(i).getString("sender_nickname");
                                    String time = res.getJSONArray("messageList").getJSONObject(i).getString("updated_at");

                                    MessageListAdapter.message item = new MessageListAdapter.message(messageEncrypted, senderEmail, senderNickname, time);

                                    // pushing the new item into the list
                                    messageList.add(item);
                                }
                            } else {
                                showToast(res.getString("msg"));
                            }
                            // after pushing into the list, update
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error handling
                showToast("Sorry, cannot connect to the server.");
            }
        });

        queue.add(stringRequest);
    }


    // helper functions
    private void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
    }
}