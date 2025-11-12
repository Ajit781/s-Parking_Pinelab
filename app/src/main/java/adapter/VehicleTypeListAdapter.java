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

import data_objects.bean.VehicleType;


/**
 * Created by vyomahp on 1/17/2018.
 */

public class VehicleTypeListAdapter extends BaseAdapter{

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<VehicleType> vehicleTypeArrayList = new ArrayList<VehicleType>();

    public VehicleTypeListAdapter(Activity activity, ArrayList<VehicleType>vehicleTypeArrayList) {
        this.activity = activity;
        this.vehicleTypeArrayList = vehicleTypeArrayList;

    }
    @Override
    public int getCount() {
        return vehicleTypeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return vehicleTypeArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.vehicletype_spinner_layout, null);
        TextView tv_vehicle_type= (TextView) convertView.findViewById(R.id.tv_vehicle_type);
        VehicleType vehicletype= vehicleTypeArrayList.get(position);
        tv_vehicle_type.setText(vehicletype.getVehicleTypeName());
        return (convertView);
    }
}
