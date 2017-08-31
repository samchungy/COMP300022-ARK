package layout.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ark.ark.R;


public class ConvoListAdapter extends BaseAdapter {

    private Context mContext;

    private class convo {
        String userName;
        String firstMessage;
        Date lastUpdated;

        convo(String userName, String firstMessage, Date lastUpdated) {
            this.userName = userName;
            this.firstMessage = firstMessage;
            this.lastUpdated = lastUpdated;
        }

    }

    ArrayList<convo> convoList = new ArrayList<>();
    private LayoutInflater inflater;

    // constructor
    public ConvoListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;

        populateDummyData();
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
        firstMessage.setText(convoList.get(position).firstMessage);
        String time = SimpleDateFormat.getTimeInstance().format(convoList.get(position).lastUpdated);
        lastUpdated.setText(time);


        return cell;
    }






    private void populateDummyData() {
        Date date = new Date();
        convoList.add(new convo("Austin", "haha", date));
        convoList.add(new convo("Rachel", "great!", date));
        convoList.add(new convo("Josh", "OMG", date));

        showToast("Testing data");
    }




    private void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(mContext, message, duration);
        toast.show();
    }

}
