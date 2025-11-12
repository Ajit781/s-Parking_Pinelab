package utilities.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.innovus.vyoma.s_parking_agentApollo.SplashActivity;

/**
 * Created by vyomainnovus on 18/12/19.
 */

public class BootReciever extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Intent myIntent = new Intent(context, SplashActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }

}

