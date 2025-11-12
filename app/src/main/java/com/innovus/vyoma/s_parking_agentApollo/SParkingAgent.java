package com.innovus.vyoma.s_parking_agentApollo;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;

import java.sql.DriverManager;

import io.fabric.sdk.android.Fabric;
import utilities.printer_utils.Config;

/**
 * Created by vyomainnovus on 13/12/19.
 */

public class SParkingAgent extends Application {

    public static DriverManager sDriverManager;
    //public static CardInfoEntity cardInfoEntity;
    private static IDAL dal;
    private static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());
        try{

            appContext = getApplicationContext();
            dal = getDal();
           // sDriverManager = DriverManager.getInstance();
            //cardInfoEntity = new CardInfoEntity();

        }catch (Exception e){
            e.printStackTrace();
        }

        Config.init(this);


    }

    public static IDAL getDal(){
        if(dal == null){
            try {
                long start = System.currentTimeMillis();
                dal = NeptuneLiteUser.getInstance().getDal(appContext);
                Log.i("Test","get dal cost:"+(System.currentTimeMillis() - start)+" ms");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(appContext, "error occurred,DAL is null.", Toast.LENGTH_LONG).show();
            }
        }
        return dal;
    }
}
