package ua.in.wercool.nrf51bt;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * Created by maska on 6/2/17.
 */

public class NRF51 extends Application
{
    public static final String TAG = "nRF51";

    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothDevice selectedBTDevice = null;
}
