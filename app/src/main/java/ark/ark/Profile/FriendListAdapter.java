package ark.ark.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ark.ark.R;

/**
 * Created by Jane on 6/10/2017.
 */

public class FriendListAdapter extends BaseAdapter{

    private Context mContext;

    public static class LFriend{
        String email;

        LFriend(String email) {
            this.email = email;
        }

    }

    ArrayList<LFriend> dataList = new ArrayList<>();
    private LayoutInflater inflater;

    // constructor
    public FriendListAdapter(Context context, ArrayList<LFriend> convoList) {
        this.dataList = convoList;
//        populateList();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    private void populateList(){
//
//        for(int i=0; i<5; i++){
//            LFriend data = new LFriend("test List "+ i);
//            this.dataList.add(data);
//        }
//
//    }

    public void addToList(LFriend data){
        this.dataList.add(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //use the following layout to populate data
        View cell = inflater.inflate(R.layout.group_item_cell, parent, false);

        //insert the data into the following fields
        TextView header = (TextView) cell.findViewById(R.id.testHeaderData);
        TextView info = (TextView) cell.findViewById(R.id.testInfoData);

        header.setText(dataList.get(position).email);

        return cell;
    }
}
