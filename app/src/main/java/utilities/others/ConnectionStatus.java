

package utilities.others;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


public class ConnectionStatus
{
    public static boolean checkConnectionStatus(Context context)
    {
        boolean check = false;
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo!= null && (mNetworkInfo.getTypeName()).equalsIgnoreCase("MOBILE"))
        {
            if(mNetworkInfo.isConnected())
                check = true;
        }
        else if (null != mNetworkInfo && mNetworkInfo.getTypeName().equalsIgnoreCase("WIFI"))
        {
            WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo mInfo = mWifiManager.getConnectionInfo();

            if (mInfo.getSupplicantState().toString().equals("COMPLETED")
                    && mInfo.getLinkSpeed() > 5)
                check = true;
        }

        return check;

    }

}

