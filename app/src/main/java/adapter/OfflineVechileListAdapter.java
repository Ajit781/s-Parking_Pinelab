package adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.innovus.vyoma.s_parking_agentApollo.R;

import java.util.ArrayList;
import java.util.List;

import data_objects.SParkingAgentModel;
import data_objects.bean.VehicleCheckInBean;
import utilities.listnerofRecyclerView.CustomItemClickListener;

/**
 * Created by vyomahp on 11/21/2017.
 */

public class OfflineVechileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> /*implements AsyncResponse*/ {

    private Context mContext;
    String rqstuserid;
    ProgressDialog progressDialog;
    public int arrpos;
    int serial = 0;
    private int lastPosition=-1;
    CustomItemClickListener listener;
    private List<VehicleCheckInBean> AllLatestNewsBeanArrayList;
    SParkingAgentModel dataModel=SParkingAgentModel.getInstance();


    public class OfflineViewHolder extends RecyclerView.ViewHolder {
        public TextView vechile_number, parking_slot, checkintime,phone_number,tv_alt_phone_no,payment_mode,tv_bookingNumber;
        public RelativeLayout viewBackground, viewForeground;
      //  public LinearLayout call_number;
        public ImageView iv_list_car_type;

        public OfflineViewHolder(View view) {
            super(view);
            tv_bookingNumber = (TextView) view.findViewById(R.id.tv_bookingNumber);
            vechile_number = (TextView) view.findViewById(R.id.vechile_number);
            payment_mode = (TextView) view.findViewById(R.id.payment_mode);
            phone_number = (TextView) view.findViewById(R.id.phone_number);
            parking_slot = (TextView) view.findViewById(R.id.parking_slot);
            checkintime = (TextView) view.findViewById(R.id.checkintime);
            tv_alt_phone_no = (TextView) view.findViewById(R.id.tv_alt_phone_no);
            viewBackground = (RelativeLayout) view.findViewById(R.id.view_background);
            viewForeground = (RelativeLayout) view.findViewById(R.id.view_foreground);
         //   call_number = (LinearLayout) view.findViewById(R.id.call_number);
            iv_list_car_type=(ImageView) view.findViewById(R.id.iv_list_car_type);

        }
    }

    public OfflineVechileListAdapter(ArrayList<VehicleCheckInBean> AllLatestNewsBeanArrayList, Context mContext, CustomItemClickListener listener) {
        this.mContext = mContext;
        this.AllLatestNewsBeanArrayList = AllLatestNewsBeanArrayList;
        this.listener = listener;;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0: {
                View v = inflater.inflate(R.layout.offline_inflatevechile_listlayout, parent, false);
                viewHolder = new OfflineViewHolder(v);
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
        final VehicleCheckInBean allNewsBean = AllLatestNewsBeanArrayList.get(holder.getAdapterPosition());
        switch (holder.getItemViewType()) {
            case 0: {
                final OfflineViewHolder viewholder = (OfflineViewHolder) holder;
                /*Typeface face1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/HelveticaNeuBold.ttf");
                viewholder.newsheading_txt.setTypeface(face1);
                viewholder.date_txt.setTypeface(face1);*/

                viewholder.vechile_number.setText(allNewsBean.getVehicle_number());
                viewholder.tv_bookingNumber.setText("#"+allNewsBean.getBookingid());

                //viewholder.parking_slot.setText(allNewsBean.get());
                viewholder.phone_number.setText(allNewsBean.getMobilenum());
                viewholder.checkintime.setText(allNewsBean.getCheckintime());
                viewholder.tv_alt_phone_no.setText(allNewsBean.getMobilenum());
                /*viewholder.date_txt.setText(allNewsBean.getDate());*/

//                Glide.with(mContext)
//                        .load(allNewsBean.get())
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
//                        .listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                // progress.setVisibility(View.VISIBLE);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                // progress.setVisibility(View.GONE);
//                                return false;
//                            }
//                        })
//                        .into(viewholder.iv_list_car_type);

                Animation animation = AnimationUtils.loadAnimation(mContext,
                        (holder.getPosition() > lastPosition) ? R.anim.up_from_bottom
                                : R.anim.down_from_top);
                holder.itemView.startAnimation(animation);
                lastPosition = holder.getPosition();

                viewholder.viewForeground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(view, holder.getPosition());
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if(allNewsBean.getVehicletype().equals("Two Wheeler")){
                            viewholder.iv_list_car_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bike));
                        }else if(allNewsBean.getVehicletype().equals("Four Wheeler")){
                            viewholder.iv_list_car_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.car_compact));
                        }else if(allNewsBean.getVehicletype().equals("Heavy Vehicle")){
                            viewholder.iv_list_car_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bus));
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
        /*if(newslist.get(position)==null)
            return AD_TYPE;
        return CONTENT_TYPE;*/
        return AllLatestNewsBeanArrayList.get(position).getViewtype();
    }

    @Override
    public int getItemCount() {
        return AllLatestNewsBeanArrayList.size();
    }

    public void removeItem(int position) {
        AllLatestNewsBeanArrayList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(VehicleCheckInBean item, int position) {
        AllLatestNewsBeanArrayList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public void setFilter(List<VehicleCheckInBean> homeListLoadBeanList) {
        if(homeListLoadBeanList.size()>0){
            AllLatestNewsBeanArrayList = new ArrayList<>();
            AllLatestNewsBeanArrayList.addAll(homeListLoadBeanList);
            notifyDataSetChanged();
        }else {
            notifyDataSetChanged();
        }
    }

    private String numberformat(String numberformat) {
        String numberString = "";
        try {
            if (Math.abs(Integer.parseInt(numberformat) / 1000000) > 1) {
                numberString = (Integer.parseInt(numberformat) / 1000000) + "lkh";
            } else if (Math.abs(Integer.parseInt(numberformat) / 10000000) > 1) {
                numberString = (Integer.parseInt(numberformat) / 10000000) + "cr";
            } else if (Math.abs(Integer.parseInt(numberformat) / 1000) > 1) {
                numberString = (Integer.parseInt(numberformat) / 1000) + "k";
            } else {
                numberString = numberformat.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (numberString);
    }
}
