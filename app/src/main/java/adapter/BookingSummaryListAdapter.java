package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.innovus.vyoma.s_parking_agentApollo.R;

import java.util.ArrayList;
import java.util.List;

import data_objects.SParkingAgentModel;
import data_objects.bean.ParkingSummaryByAgentBean;
import utilities.listnerofRecyclerView.CustomItemClickListener;

/**
 * Created by vyomahp on 11/21/2017.
 */

public class BookingSummaryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> /*implements AsyncResponse*/ {

    private Context mContext;

    public int arrpos;
    private int lastPosition=-1;
    CustomItemClickListener listener;
    private List<ParkingSummaryByAgentBean> parkingSummaryByAgentBeanList;
    SParkingAgentModel dataModel=SParkingAgentModel.getInstance();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_sl_no, tv_vehicle_type, tv_count,tv_amount;
        public RelativeLayout viewForeground;
      //  public LinearLayout call_number;
        public ImageView iv_list_car_type;

        public MyViewHolder(View view) {
            super(view);
            viewForeground = (RelativeLayout) view.findViewById(R.id.viewForeground);
            tv_sl_no = (TextView) view.findViewById(R.id.tv_sl_no);
            tv_vehicle_type = (TextView) view.findViewById(R.id.tv_vehicle_type);
            tv_count = (TextView) view.findViewById(R.id.tv_count);
            tv_amount = (TextView) view.findViewById(R.id.tv_amount);

        }
    }

    public BookingSummaryListAdapter(ArrayList<ParkingSummaryByAgentBean> parkingSummaryByAgentBeanList, Context mContext, CustomItemClickListener listener) {
        this.mContext = mContext;
        this.parkingSummaryByAgentBeanList = parkingSummaryByAgentBeanList;
        this.listener = listener;;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v = inflater.inflate(R.layout.inflatebooking_summarylistlayout, parent, false);
                viewHolder = new MyViewHolder(v);
                final RecyclerView.ViewHolder finalViewHolder = viewHolder;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(v, finalViewHolder.getPosition());
                    }
                });
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ParkingSummaryByAgentBean summaryByAgentBean = parkingSummaryByAgentBeanList.get(holder.getAdapterPosition());
        switch (holder.getItemViewType()) {
            case 1: {
                final MyViewHolder viewholder = (MyViewHolder) holder;

                viewholder.tv_sl_no.setText(String.valueOf(position+1));
                viewholder.tv_vehicle_type.setText(summaryByAgentBean.getVehicleTypeName());
                viewholder.tv_count.setText(String.valueOf(summaryByAgentBean.getParkingCount()));
                viewholder.tv_amount.setText(String.valueOf(summaryByAgentBean.getCollectionAmount()));

                viewholder.viewForeground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // listener.onItemClick(view, holder.getPosition());
                    }
                });

                break;
            }
        }
    }
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
    public int getItemViewType(int position) {
        /*if(newslist.get(position)==null)
            return AD_TYPE;
        return CONTENT_TYPE;*/
        return parkingSummaryByAgentBeanList.get(position).getViewtype();
    }

    @Override
    public int getItemCount() {
        return parkingSummaryByAgentBeanList.size();
    }

}
