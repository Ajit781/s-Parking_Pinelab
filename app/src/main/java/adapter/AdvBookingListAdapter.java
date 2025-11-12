package adapter;

import android.app.Activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.innovus.vyoma.s_parking_agentApollo.R;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import data_objects.SParkingAgentModel;
import data_objects.bean.AdvBookingReqBean;
import dmax.dialog.SpotsDialog;
import shared_pref.SharedStorage;
import utilities.ShowAlertDialog;
import utilities.async_tasks.AsyncResponse;
import utilities.async_tasks.RemoteAsync;
import utilities.constants.Constants;
import utilities.constants.Urls;
import utilities.listnerofRecyclerView.CustomItemClickListener;


/**
 * Created by vyomahp on 9/22/2017.
 */

public class AdvBookingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AsyncResponse{

    private Activity mContext;

    CustomItemClickListener listener;

    private ArrayList<AdvBookingReqBean> bookingHistoryArrayList;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();
    RemoteAsync remoteAsync;
    private SpotsDialog progressDialog;
    private static  int pos=0;
    private int AdvBookingStatusIDByAgent;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_vehicle_image;
        private RelativeLayout rl_accept,rl_decline;
        private TextView tv_vehicle_number,tv_booking_time,tv_booked_rate;

        public MyViewHolder(View view) {
            super(view);

            iv_vehicle_image = (ImageView)view.findViewById(R.id.iv_vehicle_image);
            rl_accept = (RelativeLayout) view.findViewById(R.id.rl_accept);
            rl_decline = (RelativeLayout) view.findViewById(R.id.rl_decline);
            tv_vehicle_number = (TextView) view.findViewById(R.id.tv_vehicle_number);
            tv_booking_time = (TextView) view.findViewById(R.id.tv_booking_time);
            tv_booked_rate = (TextView) view.findViewById(R.id.tv_booked_rate);

        }
    }

    public AdvBookingListAdapter(ArrayList<AdvBookingReqBean> bookingHistoryArrayList, Activity mContext, CustomItemClickListener customItemClickListener) {
        this.mContext = mContext;
        this.bookingHistoryArrayList = bookingHistoryArrayList;
        this.listener = customItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v = inflater.inflate(R.layout.inflate_advanced_booking_list, parent, false);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final AdvBookingReqBean AdvBookingReqBean = bookingHistoryArrayList.get(holder.getAdapterPosition());
        switch (holder.getItemViewType()) {
            case 1: {
                final MyViewHolder viewholder = (MyViewHolder) holder;

                viewholder.tv_vehicle_number.setText(AdvBookingReqBean.getVehicle_no());
                viewholder.tv_booked_rate.setText("₹"+AdvBookingReqBean.getTotalamount());

                if (AdvBookingReqBean.getVehicletype_id().equals("2")){
                    viewholder.iv_vehicle_image.setImageResource(R.drawable.car_compact_list);
                }else if(AdvBookingReqBean.getVehicletype_id().equals("1")){
                    viewholder.iv_vehicle_image.setImageResource(R.drawable.motorcycle_list);
                }

                //change display format of date and time
                SimpleDateFormat sdf_bookingdate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf_starttime = new SimpleDateFormat("HH:mm:ss");
                Date newbookingdate = new Date();
                Date newbookingdateend = new Date();
                Date newstartttime = new Date();
                Date newendtime = new Date();
                try {
                    newbookingdate = sdf_bookingdate.parse(AdvBookingReqBean.getBooking_date());
                    newbookingdateend = sdf_bookingdate.parse(AdvBookingReqBean.getBooking_dateend());
                    newstartttime = sdf_starttime.parse(AdvBookingReqBean.getStartTime());
                    newendtime = sdf_starttime.parse(AdvBookingReqBean.getEndTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat timeFormat_bookingdate = new SimpleDateFormat("EEE, MMM d");
                SimpleDateFormat timeFormat_starttime = new SimpleDateFormat("hh:mm a");
                String finalbookingDate = timeFormat_bookingdate.format(newbookingdate);
                String finalbookingDateend = timeFormat_bookingdate.format(newbookingdateend);
                String finalstarttime = timeFormat_starttime.format(newstartttime);
                String finalendtime = timeFormat_starttime.format(newendtime);

                viewholder.tv_booking_time.setText(finalbookingDate +", "+ finalstarttime + " - "+" \n "+
                        finalbookingDateend +", "+finalendtime);

                viewholder.rl_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //listener.onItemClick(v, holder.getPosition());
                        pos = position;
                        Log.e("accept_position", String.valueOf(pos));
                        AdvBookingStatusIDByAgent = 1;
                        AdvBookingAcceptOrDeclineByAgent(AdvBookingReqBean.getAdvbookingid(),AdvBookingStatusIDByAgent);
                    }
                });

                viewholder.rl_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // listener.onItemClick(v, holder.getPosition());
                        pos = position;
                        Log.e("decline_position", String.valueOf(pos));
                        AdvBookingStatusIDByAgent = 0;

                        AdvBookingAcceptOrDeclineByAgent(AdvBookingReqBean.getAdvbookingid(),AdvBookingStatusIDByAgent);

                    }
                });

                break;
            }
        }
    }

    private void AdvBookingAcceptOrDeclineByAgent(String advbookingid, int advBookingStatusIDByAgent) {

        start_progress_dialog();
        Urls Urls = new Urls();
        String login_url = Urls.AdvBookingAcceptOrDeclineByAgent;
        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.ADVBOOKINGACCEPTORDECLINE;
        remoteAsync.delegate = this;
        String urlParams = "";

        try {
            urlParams = "AdvBookingID=" + URLEncoder.encode(advbookingid,"UTF-8")+
            "&AdvBookingStatusIDByAgent=" + URLEncoder.encode(String.valueOf(advBookingStatusIDByAgent),"UTF-8")+
            "&AgentID=" + URLEncoder.encode(String.valueOf(SharedStorage.getValue(mContext,"UserId")),"UTF-8");

        } catch (Exception e) {
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>>>", urlParams);
    }

    void start_progress_dialog() {
        try{
            progressDialog = new SpotsDialog(mContext, R.style.CustomWaitDialog);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stop_progress_dialog() {
        try{
            if(progressDialog!=null){

                progressDialog.dismiss();
                progressDialog=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String type, String output) {
        stop_progress_dialog();
        try{

            JSONObject obj = new JSONObject(output);
            Log.e("Response-->", obj.toString()); // Response from server

            if (obj.getString("status").equals(Constants.SUCCESS)) {

                    ShowAlertDialog.showAlertDialog(mContext,obj.getString("message"));
                    removeItem(pos);
            }else {
                ShowAlertDialog.showAlertDialog(mContext,obj.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getItemViewType(int position) {

        return bookingHistoryArrayList.get(position).getViewtype();
    }

    @Override
    public int getItemCount() {
        return bookingHistoryArrayList.size();
    }

   public AdvBookingReqBean getItem(int position) {
        return bookingHistoryArrayList.get(position);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public void removeItem(int position) {
        bookingHistoryArrayList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(AdvBookingReqBean item, int position) {
        bookingHistoryArrayList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }




}
