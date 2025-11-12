package utilities.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class BluetoothSocketManager {

    private static BluetoothSocket bluetoothSocketinstance;


    public static BluetoothSocket getInstance(BluetoothDevice bluetoothDevice, BluetoothAdapter bluetoothAdapter) {
        if(bluetoothSocketinstance == null){
            BluetoothConnector bluetoothConnector = new BluetoothConnector(bluetoothDevice,false,
                    bluetoothAdapter,null);
            try {
                bluetoothSocketinstance = bluetoothConnector.connect().getUnderlyingSocket();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("xx123", "getInstance: ", e);
            }
        }
        return bluetoothSocketinstance;
    }

    private BluetoothSocketManager() {
    }
}
