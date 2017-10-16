package ark.ark.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ark.ark.R;


public class ConvoListAdapter extends BaseAdapter {

    private Context mContext;

    public static class convo {
        String userName;
        String firstMessage;
        String lastUpdated;
        String conversationId;

        convo(String userName, String firstMessage, String lastUpdated, String conversationId) {
            this.userName = userName;
            this.firstMessage = firstMessage;
            this.lastUpdated = lastUpdated;
            this.conversationId = conversationId;
        }

    }

    ArrayList<convo> convoList = new ArrayList<>();
    private LayoutInflater inflater;

    // constructor
    public ConvoListAdapter(Context context, ArrayList<convo> convoList) {
        this.convoList = convoList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    @Override
    public int getCount() {
        return convoList.size();
    }

    @Override
    public Object getItem(int position) {
        return convoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = inflater.inflate(R.layout.chat_list_cell, parent, false);


        TextView userName = (TextView) cell.findViewById(R.id.austin_ChatListCell_userName);
        TextView firstMessage = (TextView) cell.findViewById(R.id.austin_ChatListCell_firstMessage);
        TextView lastUpdated = (TextView) cell.findViewById(R.id.austin_ChatListCell_lastUpdated);

        userName.setText(convoList.get(position).userName);
        firstMessage.setText(decryptMessageBody(convoList.get(position).firstMessage));

        try {
            String time = convoList.get(position).lastUpdated.substring(11, 19);
            lastUpdated.setText(time);
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            lastUpdated.setText("");
        }


        return cell;
    }




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
}
