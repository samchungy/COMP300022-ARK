package ark.ark.Chat;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.toolbox.ByteArrayPool;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import ark.ark.Authentication.ARK_auth;
import ark.ark.Groups.CurrentUser;
import ark.ark.R;


public class MessageListAdapter extends BaseAdapter {

    private Context mContext;

    public static class message {
        String messageEncrypted;
        String senderEmail;
        String senderNickname;
        String time;

        message(String messageEncrypted, String senderEmail, String senderNickname, String time) {
            this.messageEncrypted = messageEncrypted;
            this.senderEmail = senderEmail;
            this.senderNickname = senderNickname;
            this.time = time;
        }

    }

    ArrayList<message> messageList = new ArrayList<>();
    private LayoutInflater inflater;


    // constructor
    public MessageListAdapter(Context context, ArrayList<message> messageList) {
        this.messageList = messageList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = inflater.inflate(R.layout.message_list_cell, parent, false);


        TextView thisUserName = (TextView) cell.findViewById(R.id.austin_MessageListCell_thisUserName);
        TextView otherUserName = (TextView) cell.findViewById(R.id.austin_MessageListCell_otherUserName);
        TextView thisMessage = (TextView) cell.findViewById(R.id.austin_MessageListCell_thisMessage);
        TextView otherMessage = (TextView) cell.findViewById(R.id.austin_MessageListCell_otherMessage);
        TextView time = (TextView) cell.findViewById(R.id.austin_MessageListCell_time);


        String userEmail = ARK_auth.fetchUserEmail(mContext);


        if (messageList.get(position).senderEmail.equals(userEmail)) {
            // if the sender is this user
            thisUserName.setText("Me");
            otherUserName.setText("");
            thisMessage.setText(decryptMessageBody(messageList.get(position).messageEncrypted));
        } else {
            thisUserName.setText("");
            otherUserName.setText(messageList.get(position).senderNickname);
            otherMessage.setText(decryptMessageBody(messageList.get(position).messageEncrypted));
        }

        time.setText(messageList.get(position).time.substring(11, 19));


        return cell;
    }


    /** decoding msg from hex string
     * @param messageBody
     * @return
     */
    private String decryptMessageBody(String messageBody) {
        String msg = "";
        try {
            msg = new String(hexStringToByteArray(messageBody), Charset.forName("UTF-8"));
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return msg;
    }



    private byte[] hexStringToByteArray(String hex) {
        int l = hex.length();
        byte[] data = new byte[l/2];
        for (int i = 0; i < l; i += 2) {
            data[i/2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }


    private void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(mContext, message, duration);
        toast.show();
    }

}
