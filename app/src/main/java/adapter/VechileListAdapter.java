package adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.innovus.vyoma.s_parking_agentApollo.R;

import java.util.ArrayList;
import java.util.List;

import data_objects.SParkingAgentModel;

import data_objects.bean.DataObject;
import utilities.listnerofRecyclerView.CustomItemClickListener;

/**
 * Created by vyomahp on 11/21/2017.
 */

public class VechileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> /*implements AsyncResponse*/ {

    private Context mContext;
    String rqstuserid;
    ProgressDialog progressDialog;
    public int arrpos;
    int serial = 0;
    private int lastPosition=-1;
    CustomItemClickListener listener;
    private List<DataObject> AllLatestNewsBeanArrayList;
    SParkingAgentModel dataModel=SParkingAgentModel.getInstance();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView vechile_number, parking_slot, checkintime,phone_number,tv_alt_phone_no,payment_mode,tv_bookingNumber;
        public RelativeLayout viewBackground, viewForeground;
      //  public LinearLayout call_number;
        public ImageView iv_list_car_type;

        public MyViewHolder(View view) {
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

    public VechileListAdapter(ArrayList<DataObject> AllLatestNewsBeanArrayList, Context mContext, CustomItemClickListener listener) {
        this.mContext = mContext;
        this.AllLatestNewsBeanArrayList = AllLatestNewsBeanArrayList;
        this.listener = listener;;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v = inflater.inflate(R.layout.inflatevechile_listlayout, parent, false);
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
        int adapterPos = holder.getAdapterPosition();
        if (adapterPos == RecyclerView.NO_ID || adapterPos < 0 || adapterPos >= AllLatestNewsBeanArrayList.size()) {
            return; // View is being recycled or position is stale — skip safely
        }
        final DataObject allNewsBean = AllLatestNewsBeanArrayList.get(adapterPos);
        switch (holder.getItemViewType()) {
            case 1: {
                final MyViewHolder viewholder = (MyViewHolder) holder;
                /*Typeface face1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/HelveticaNeuBold.ttf");
                viewholder.newsheading_txt.setTypeface(face1);
                viewholder.date_txt.setTypeface(face1);*/
                viewholder.vechile_number.setText(allNewsBean.getM_strVehicleNo());
                viewholder.tv_bookingNumber.setText("#"+allNewsBean.getBooking_no());
                viewholder.payment_mode.setText(allNewsBean.getPayment_mode());
                if (allNewsBean.getPayment_mode().equals("Cash")){
                    viewholder.payment_mode.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.money, 0);
                }else if (allNewsBean.getPayment_mode().equals("Wallet")){
                    viewholder.payment_mode.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_account_balance_wallet, 0);
                }
                viewholder.parking_slot.setText(allNewsBean.getM_strVehicleType());
                viewholder.phone_number.setText(allNewsBean.getM_strOwnerPhone());
                viewholder.checkintime.setText(allNewsBean.getM_strCheckInTime());
                viewholder.tv_alt_phone_no.setText(allNewsBean.getM_strAlterPhone());
                /*viewholder.date_txt.setText(allNewsBean.getDate());*/

                Glide.with(mContext)
                        .load(allNewsBean.getVehicle_type_icon())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                // progress.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                // progress.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(viewholder.iv_list_car_type);

                Animation animation = AnimationUtils.loadAnimation(mContext,
                        (position > lastPosition) ? R.anim.up_from_bottom
                                : R.anim.down_from_top);
                holder.itemView.startAnimation(animation);
                lastPosition = position;

                viewholder.viewForeground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(view, holder.getPosition());
                    }
                });
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        if(allNewsBean.getM_strVehicleType().equals("Two Wheeler")){
//                            viewholder.iv_list_car_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bike));
//                        }else if (allNewsBean.getM_strVehicleType().equals("Four Wheeler")){
//                            viewholder.iv_list_car_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.car_compact));
//                        }
//                    }
//                }, 100);


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
        if (position < 0 || position >= AllLatestNewsBeanArrayList.size()) {
            return 1; // safe default
        }
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

    public void restoreItem(DataObject item, int position) {
        AllLatestNewsBeanArrayList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public void setFilter(List<DataObject> homeListLoadBeanList) {
        // Mutate the SAME list instance (never replace the reference — that desynchronises the adapter)
        AllLatestNewsBeanArrayList.clear();
        if (homeListLoadBeanList != null && homeListLoadBeanList.size() > 0) {
            AllLatestNewsBeanArrayList.addAll(homeListLoadBeanList);
        }
        notifyDataSetChanged();
    }

    /**
     * Replaces the full dataset and refreshes the list.
     * Use this instead of recreating the adapter from the Activity.
     */
    public void updateList(List<DataObject> newList) {
        AllLatestNewsBeanArrayList.clear();
        if (newList != null) {
            AllLatestNewsBeanArrayList.addAll(newList);
        }
        notifyDataSetChanged();
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
