package com.innovus.vyoma.s_parking_agentApollo;

import android.content.Intent;
import android.os.Bundle;

import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import adapter.AdvBookingListAdapter;
import data_objects.SParkingAgentModel;
import shared_pref.SharedStorage;
import utilities.listnerofRecyclerView.CustomItemClickListener;

public class AdvBookingListActivity extends AppCompatActivity {

    private RecyclerView rl_booking_status;
    SParkingAgentModel datamodel = SParkingAgentModel.getInstance();
    AdvBookingListAdapter advBookingListAdapter;
    private ImageView noitem_in_cart;
    private TextView tv_message;
    private RelativeLayout no_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_booking_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+getResources().getString(R.string.app_name)+"</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initviews();
    }

    private void initviews() {

        datamodel.about_advanced_dash = 1;

        rl_booking_status = (RecyclerView)findViewById(R.id.rl_booking_status);
        noitem_in_cart = (ImageView)findViewById(R.id.noitem_in_cart);
        tv_message = (TextView) findViewById(R.id.tv_message);
        no_result = (RelativeLayout) findViewById(R.id.no_result);

        if(SharedStorage.getValue(this,"agent_mode").equals("1")){//check in for offline mode

            getAdvbookinglist();//set list
        }else {
            no_result.setVisibility(View.VISIBLE);
            noitem_in_cart.setImageResource(R.drawable.ic_signal_wifi_off);
            tv_message.setText(getResources().getString(R.string.youareoffline));
        }

    }

    private void getAdvbookinglist() {

        if ( datamodel.advBookingReqBeanArrayList.size()>0){

            rl_booking_status.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(AdvBookingListActivity.this);
            rl_booking_status.setLayoutManager(layoutManager);
            rl_booking_status.setItemAnimator(new DefaultItemAnimator());
            advBookingListAdapter = new AdvBookingListAdapter(datamodel.advBookingReqBeanArrayList, AdvBookingListActivity.this,
                    new CustomItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {

                        }
                    });
            rl_booking_status.setAdapter(advBookingListAdapter);
        }
        else {
            no_result.setVisibility(View.VISIBLE);
            rl_booking_status.setVisibility(View.GONE);
            tv_message.setText(getResources().getString(R.string.no_item_advancebook));
            // for  showing gif file if there is no any vehicle in the list
            Glide.with(getApplicationContext()).load(R.drawable.nobooking).asGif().into(noitem_in_cart);// for  showing gif file if there is no any vehicle in the list

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        datamodel.about_advanced_dash = 0;
        super.onBackPressed();
        Intent intent = new Intent(AdvBookingListActivity.this, DashBoardActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }



}
