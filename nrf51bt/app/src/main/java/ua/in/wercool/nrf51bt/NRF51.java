package ua.in.wercool.nrf51bt;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;

/**
 * Created by maska on 6/2/17.
 */

public class NRF51 extends Application
{
    public static final String TAG = "nRF51";

    public BluetoothAdapter mBluetoothAdapter               = null;
    public BluetoothLeScanner mBluetoothLeScanner           = null;
    public BluetoothDevice selectedBTDevice                 = null;
    public BluetoothLeUart BLEuart                          = null;
    public BTDeviceActivity BLEuartCallback                 = null;

}
