package ark.ark.Chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
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

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ark.ark.Groups.CurrentUser;
import ark.ark.R;

public class ChatLogActivity extends AppCompatActivity {

    private MessageListAdapter mAdapter;
    private ArrayList<MessageListAdapter.message> messageList;
    private int lastTimeMessageNum = 0;


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
        setupKeyboardObserver();
    }





    // keyboard observer solution from: https://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
    private void setupKeyboardObserver() {
        final View activityRootView = findViewById(R.id.austin_ChatListView_bottomBar);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(200)) { // if more than 200 dp, it's probably a keyboard
                    scrollToBottom(true);
                }
            }
        });
    }


    // a helper function from:
    // https://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
    public float dpToPx(float valueInDp) {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }


    private void setupMessageList() {
        ListView messageListView = (ListView)findViewById(R.id.austin_MessageListView);
        messageList = new ArrayList<>();
        mAdapter = new MessageListAdapter(this, messageList);
        messageListView.setAdapter(mAdapter);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reloadAllMessages();
            }
        }, 1000, 1000);
    }



    public void onClickSendButton(View v) {
        EditText messageTextfield = (EditText)findViewById(R.id.austin_ChatListView_messageTextfield);
        String msg = messageTextfield.getText().toString();
        if (msg.equals("")) { // if empty message
            showToast("Message cannot be empty");
            return;
        } else {
            messageTextfield.setText("");
            sendMessage(msg);
        }
    }



    private void sendMessage(String msg) {
        msg = encryptMessage(msg);

        RequestQueue queue = Volley.newRequestQueue(this);

        String server ="52.65.97.117";
        String conversationId = getIntent().getStringExtra("conversation_id");
        String requestURL = "http://" + server + "/message/create?conversation_id=" + conversationId + "&email=" + CurrentUser.getInstance().getEmail() + "&message_body=" + msg;


        // Request a string response from the requestURL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // after getting response, try reading the json
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getString("success").equals("ok")) { // msg sent
                                reloadAllMessages();
                            } else {
                                showToast(res.getString("msg"));
                            }
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


    /** encoding msg into hex string
     * @param msg
     * @return
     */
    private String encryptMessage(String msg) {
        return String.format("%040x", new BigInteger(1, msg.getBytes(Charset.forName("UTF-8"))));
    }



    private void reloadAllMessages() {
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
                                messageList.clear();
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
                            if (messageList.size() > lastTimeMessageNum) { // if number of messages are different.
                                mAdapter.notifyDataSetChanged();
                                scrollToBottom(false);
                                lastTimeMessageNum = messageList.size();
                            }
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



    private void scrollToBottom(Boolean smooth) {
        ListView chatListView = (ListView)findViewById(R.id.austin_MessageListView);

        if (!mAdapter.isEmpty()) {
            if (smooth) {
                chatListView.smoothScrollToPosition(mAdapter.getCount() - 1);
            } else {
                chatListView.setSelection(mAdapter.getCount() - 1);
            }
        }
    }



    // helper functions
    private void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
    }
}
