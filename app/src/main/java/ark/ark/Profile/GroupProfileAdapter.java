package ark.ark.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ark.ark.Groups.Group;
import ark.ark.R;
import ark.ark.ToastUtils;

/**
 * Created by Jane on 6/10/2017.
 * Sends the data to the list view and populates the list of Groups
 */

public class GroupProfileAdapter extends BaseAdapter{

    private Context mContext;


    ArrayList<Group> dataList = new ArrayList<>();
    private LayoutInflater inflater;

    // constructor
    public GroupProfileAdapter(Context context, ArrayList<Group> list) {
        this.dataList = list;
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

    public void addToList(Group data){
        this.dataList.add(data);
    }

    /**
     * Inserts the data into the appropriate fields
     * @param position - the position of the object in the list
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //use the following layout to populate data
        View cell = inflater.inflate(R.layout.group_item_cell, parent, false);

        //insert the data into the following fields
        TextView header = (TextView) cell.findViewById(R.id.testHeaderData);
        TextView info = (TextView) cell.findViewById(R.id.testInfoData);

        //Set the header to a id if group name doesn't exist
        if(dataList.get(position).getName()==null || dataList.get(position).getName().equals("")){
            header.setText(dataList.get(position).getId());
        }else{
            header.setText(dataList.get(position).getName());
        }

        //info.setText(dataList.get(position).getFriends().size() + " members");

        return cell;
    }
}
