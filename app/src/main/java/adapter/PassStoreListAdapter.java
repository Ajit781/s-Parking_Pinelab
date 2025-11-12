package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.innovus.vyoma.s_parking_agentApollo.R;

import java.util.ArrayList;

import data_objects.bean.SpclPassStoreBean;


/**
 * Created by vyomahp on 1/17/2018.
 */

public class PassStoreListAdapter extends BaseAdapter{

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<SpclPassStoreBean>  spclPassStoreBeanArrayList = new ArrayList<SpclPassStoreBean>();

    public PassStoreListAdapter(Activity activity, ArrayList<SpclPassStoreBean>spclPassStoreBeanArrayList) {
        this.activity = activity;
        this.spclPassStoreBeanArrayList = spclPassStoreBeanArrayList;

    }
    @Override
    public int getCount() {
        return spclPassStoreBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return spclPassStoreBeanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.store_spinner_layout, null);
        TextView tv_vehicle_type= (TextView) convertView.findViewById(R.id.tv_vehicle_type);
        SpclPassStoreBean spclPassStoreBean= spclPassStoreBeanArrayList.get(position);
        tv_vehicle_type.setText(spclPassStoreBean.getPassStoreName());
        return (convertView);
    }
}
