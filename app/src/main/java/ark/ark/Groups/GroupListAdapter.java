package ark.ark.Groups;

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

public class GroupListAdapter extends BaseAdapter{

    private Context mContext;


    ArrayList<Group> dataList = new ArrayList<>();
    private LayoutInflater inflater;

    // constructor
    public GroupListAdapter(Context context, ArrayList<Group> list) {
        this.dataList = list;
//        populateList();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }


    private void setUpList(){
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

    public void addToList(Group data){
        this.dataList.add(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //use the following layout to populate data
        View cell = inflater.inflate(R.layout.group_item_cell, parent, false);

        //insert the data into the following fields
        TextView header = (TextView) cell.findViewById(R.id.testHeaderData);
        TextView info = (TextView) cell.findViewById(R.id.testInfoData);

        header.setText(dataList.get(position).getId());
        info.setText(dataList.get(position).getFriends().size() + " members");

        return cell;
    }
}
