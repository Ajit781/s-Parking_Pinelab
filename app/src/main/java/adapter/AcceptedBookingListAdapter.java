package adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
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
import data_objects.bean.AcceptedBookingBean;
import utilities.listnerofRecyclerView.CustomItemClickListener;

/**
 * Created by vyomahp on 11/21/2017.
 */

public class AcceptedBookingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> /*implements AsyncResponse*/ {

    private Activity mContext;
    String rqstuserid;
    ProgressDialog progressDialog;
    public int arrpos;
    int serial = 0;
    private int lastPosition=-1;
    CustomItemClickListener listener;
    private List<AcceptedBookingBean> acceptedBookingBeanList;
    SParkingAgentModel dataModel=SParkingAgentModel.getInstance();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView vechile_number, parking_slot, checkintime,phone_number,tv_alt_phone_no,payment_mode,tv_bookingNumber;
        public RelativeLayout rl_background, rl_foreground;
        //  public LinearLayout call_number;
        public ImageView iv_checkin,iv_list_car_type;

        public MyViewHolder(View view) {
            super(view);

            rl_background = (RelativeLayout)view.findViewById(R.id.rl_background);
            rl_foreground = (RelativeLayout)view.findViewById(R.id.rl_foreground);
            iv_checkin = (ImageView) view.findViewById(R.id.iv_checkin);
            iv_list_car_type = (ImageView) view.findViewById(R.id.iv_list_car_type);
            vechile_number = (TextView) view.findViewById(R.id.vechile_number);
            phone_number = (TextView) view.findViewById(R.id.phone_number);
            parking_slot = (TextView) view.findViewById(R.id.parking_slot);
            checkintime = (TextView) view.findViewById(R.id.checkintime);
            tv_alt_phone_no = (TextView) view.findViewById(R.id.tv_alt_phone_no);
            tv_bookingNumber = (TextView) view.findViewById(R.id.tv_bookingNumber);
            payment_mode = (TextView) view.findViewById(R.id.payment_mode);

        }
    }

    public AcceptedBookingListAdapter(ArrayList<AcceptedBookingBean> acceptedBookingBeanList, Activity mContext, CustomItemClickListener listener) {
        this.mContext = mContext;
        this.acceptedBookingBeanList = acceptedBookingBeanList;
        this.listener = listener;;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v = inflater.inflate(R.layout.inflate_accepted_book_listlayout, parent, false);
                viewHolder = new MyViewHolder(v);
                final RecyclerView.ViewHolder finalViewHolder = viewHolder;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // listener.onItemClick(v, finalViewHolder.getPosition());
                    }
                });
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final AcceptedBookingBean allNewsBean = acceptedBookingBeanList.get(holder.getAdapterPosition());
        switch (holder.getItemViewType()) {
            case 1: {
                final MyViewHolder viewholder = (MyViewHolder) holder;

                viewholder.vechile_number.setText(allNewsBean.getVehicleno());
                viewholder.checkintime.setText(allNewsBean.getAdvbookingstarttime() + " - " + allNewsBean.getAdvbookingendtime());
                viewholder.tv_bookingNumber.setText(allNewsBean.getTotaltime() + " Hrs");
                viewholder.phone_number.setText(allNewsBean.getVehicleownermobile());
                viewholder.payment_mode.setText("₹"+allNewsBean.getTotalamount());

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if(allNewsBean.getVehicletypename().equals("Two Wheeler")){
                            viewholder.iv_list_car_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bike));
                        }else if (allNewsBean.getVehicletypename().equals("Four Wheeler")){
                            viewholder.iv_list_car_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.car_compact));
                        }
                    }
                }, 100);

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

        return acceptedBookingBeanList.get(position).getViewtype();
    }

    @Override
    public int getItemCount() {
        return acceptedBookingBeanList.size();
    }

    public void removeItem(int position) {
        acceptedBookingBeanList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(AcceptedBookingBean item, int position) {
        acceptedBookingBeanList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public void setFilter(List<AcceptedBookingBean> homeListLoadBeanList) {
        if(homeListLoadBeanList.size()>0){
            acceptedBookingBeanList = new ArrayList<>();
            acceptedBookingBeanList.addAll(homeListLoadBeanList);
            notifyDataSetChanged();
        }else {
            notifyDataSetChanged();
        }
    }

    private String numberformat(String numberformat) {
        String numberString = "";
        try{
            if (Math.abs(Integer.parseInt(numberformat) / 1000000) > 1) {
                numberString = (Integer.parseInt(numberformat) / 1000000) + "lkh";
            }
            else if (Math.abs(Integer.parseInt(numberformat)/10000000) > 1) {
                numberString = (Integer.parseInt(numberformat) / 10000000) + "cr";
            }else if(Math.abs(Integer.parseInt(numberformat)/1000) > 1){
                numberString = (Integer.parseInt(numberformat) / 1000) + "k";
            }
            else {
                numberString = numberformat.toString();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return (numberString);
    }
}
