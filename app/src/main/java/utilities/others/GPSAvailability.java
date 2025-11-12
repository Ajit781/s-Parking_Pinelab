

package utilities.others;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;

import com.innovus.vyoma.s_parking_agentApollo.R;


public class GPSAvailability {


    private Context context;
    public LocationManager manager;
    /**
     * If the Gps is unavailable then show an alert and give the user option to turn the gps on.
     * else do nothing.
     *
     * @param ctx
     */
    public Boolean check(Context ctx) {

        context = ctx;
        manager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(context);
            return false;
        } else {
            return true;
        }

    }

    private void buildAlertMessageNoGps(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.location_service_disabled))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.btnYes), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        context.startActivity(new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                }).setNegativeButton(context.getString(R.string.btnNo), new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressWarnings({"unused"})
    private void turnGPSOn(Context context) {
        /*
         * String provider =
		 * Settings.Secure.getString(context.getContentResolver(),
		 * Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		 *
		 * if (!provider.contains("gps")) { // if gps is disabled final Intent
		 * poke = new Intent(); poke.setClassName("com.android.settings",
		 * "com.android.settings.widget.SettingsAppWidgetProvider");
		 * poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		 * poke.setData(Uri.parse("3")); context.sendBroadcast(poke); }
		 */
		/*
		 * Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
		 * intent.putExtra("enabled", true); context.sendBroadcast(intent);
		 */

		/*
		 * Settings.Secure.putString(context.getContentResolver(),
		 * Settings.Secure.LOCATION_PROVIDERS_ALLOWED, "Network,gps");
		 */

    }

    @SuppressWarnings({"unused", "deprecation"})
    private void turnGPSOff(Context context) {
        String provider = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps")) { // if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }



}
