package com.innovus.vyoma.s_parking_agentApollo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import data_objects.SParkingAgentModel;
import shared_pref.SharedStorage;
import utilities.bluetooth.BluetoothSocketManager;
import utilities.others.CToast;

public abstract class BaseActivity extends AppCompatActivity {

    BluetoothDevice mmDevice_forboom;
    BluetoothSocket mmSocket_forboom= null;
    BluetoothAdapter mBluetoothAdapter_forboom;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findBT_forboombarier_new();// find a bluetooth printer device


    }

    // this will find a bluetooth printer device
    void findBT_forboombarier() {

        try {
            mBluetoothAdapter_forboom = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter_forboom == null) {
                /*myLabel.setText("No bluetooth adapter available");*/
            }

            if(mBluetoothAdapter_forboom.isEnabled()) {
                Boolean isnotconnected = true;
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter_forboom.getBondedDevices();

                if(pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {

                        // RPP300 is the name of the bluetooth printer device
                        // we got this name from the list of paired devices
                        if (device.getName().equals(getResources().getString(R.string.bluetooth_name_forboom))) {
                            Log.e("device",device.getName());
                            mmDevice_forboom = device;
                            isnotconnected = false;
                            //openBT();

                            Log.e("type_of_device", String.valueOf(device.getType()));

                            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                            try {
                                mmSocket_forboom = mmDevice_forboom.createRfcommSocketToServiceRecord(uuid);
                            } catch (Exception e) {Log.e("xx123","Error creating socket",e);}



                            try {
                                mmSocket_forboom.connect();// bluetooth printer device connection establishment
                                Log.e("xx123","Connected");
                            } catch (IOException e) {
                                Log.e("xx123",e.getMessage(),e);
                                try {
                                    Log.e("xx123","trying fallback...");

                                    mmSocket_forboom =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                                    mmSocket_forboom.connect();

                                    Log.e("xx123","Connected");
                                }
                                catch (Exception e2) {
                                    Log.e("xx123", "Couldn't establish Bluetooth connection!",e2);
                                }
                            }

                            CToast.show(getApplicationContext(),"inside try UUID--->"+uuid.toString());

                            if (!SharedStorage.getValue(getApplicationContext(),"printer_name").equals("pos")){
                                if (!SharedStorage.getValue(getApplicationContext(),"printer_name").equals("pos_new")){
                                    if (!SharedStorage.getValue(getApplicationContext(),"printer_name").equals("")){


                                    }
                                }
                            }
                            break;
                        }

                    }
                    mBluetoothAdapter_forboom.cancelDiscovery();
                }

            }else {
                if(dataModel.isbluetoothon == 0){
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                }
            }



        }catch(Exception e){
            Log.w("xx123", "findBT_forboombarier: ", e);
            e.printStackTrace();
        }
    }

    // this will find a bluetooth printer device

    void findBT_forboombarier_new() {

        try {
            mBluetoothAdapter_forboom = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter_forboom == null) {
                /*myLabel.setText("No bluetooth adapter available");*/
            }

            if(mBluetoothAdapter_forboom.isEnabled()) {
                Boolean isnotconnected = true;
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter_forboom.getBondedDevices();

                if(pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {

                        // RPP300 is the name of the bluetooth printer device
                        // we got this name from the list of paired devices
                        if (device.getName().equals(getResources().getString(R.string.bluetooth_name_forboom))) {
                            Log.e("device",device.getName());
                            mmDevice_forboom = device;
                            isnotconnected = false;
                             mmSocket_forboom = BluetoothSocketManager.getInstance(device,mBluetoothAdapter_forboom);
                            if (mmSocket_forboom != null) {
                                Log.e("xx123","Connected---->"+mmSocket_forboom.isConnected());
                            }

                            if (!SharedStorage.getValue(getApplicationContext(),"printer_name").equals("pos")){
                                if (!SharedStorage.getValue(getApplicationContext(),"printer_name").equals("pos_new")){
                                    if (!SharedStorage.getValue(getApplicationContext(),"printer_name").equals("")){

                                    }
                                }
                            }
                            break;
                        }

                    }
                    mBluetoothAdapter_forboom.cancelDiscovery();
                }

            }else {
                if(dataModel.isbluetoothon == 0){
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                }
            }



        }catch(Exception e){
            Log.w("xx123", "findBT_forboombarier: ", e);
            e.printStackTrace();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    // close the connection to bluetooth printer.
    void closeBT_forBoom() throws IOException {
        try {


                mmSocket_forboom.close();
                Log.e("closed try connection","closed try connection");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
