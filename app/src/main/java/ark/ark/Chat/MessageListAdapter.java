package ark.ark.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        TextView messageDecrypted = (TextView) cell.findViewById(R.id.austin_MessageListCell_messageDecrypted);
        TextView time = (TextView) cell.findViewById(R.id.austin_MessageListCell_time);


        String userEmail = "user1@user1.com";


        if (messageList.get(position).senderEmail.equals(userEmail)) {
            // if the sender is this user
            thisUserName.setText("Me");
            otherUserName.setText("");
        } else {
            thisUserName.setText("");
            otherUserName.setText(messageList.get(position).senderNickname);
        }

        messageDecrypted.setText(decryptMessageBody(messageList.get(position).messageEncrypted));
        time.setText(messageList.get(position).time.substring(11, 19));


        return cell;
    }



    private String decryptMessageBody(String messageBody) {
        return messageBody;
    }


    private void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(mContext, message, duration);
        toast.show();
    }

}
