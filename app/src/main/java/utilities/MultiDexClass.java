package utilities;

import android.content.Context;

import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by server1 on 12/7/2018.
 */

public class MultiDexClass extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        Fabric.with(this, new Crashlytics());
        MultiDex.install(this);


    }

}
