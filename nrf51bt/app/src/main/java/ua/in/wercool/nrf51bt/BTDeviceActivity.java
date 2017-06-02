package ua.in.wercool.nrf51bt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class BTDeviceActivity extends AppCompatActivity implements BluetoothLeUart.Callback
{

    NRF51 nRF51;

    // Bluetooth LE UART instance.  This is defined in BluetoothLeUart.java.
    private BluetoothLeUart uart;

    // UI elements
    private TextView btLeUARTtraceTextView;

    private BluetoothGatt mBluetoothGatt;
    BluetoothGattCharacteristic characteristic;
    boolean enabled;

    // Write some text to the messages text view.
    // Care is taken to do this on the main UI thread so writeLine can be called from any thread
    // (like the BTLE callback).
    private void writeLine(final CharSequence text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btLeUARTtraceTextView.append(text);
                btLeUARTtraceTextView.append("\n");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btdevice);

        nRF51 = (NRF51) getApplication();

        btLeUARTtraceTextView = (TextView) findViewById(R.id.btLeUARTtraceTextView);
        btLeUARTtraceTextView.setMovementMethod(new ScrollingMovementMethod());

        Log.i(nRF51.TAG, "Selected BT Device address: " + nRF51.selectedBTDevice.getAddress());

        // Initialize UART.
        uart = new BluetoothLeUart(getApplicationContext());
        uart.unregisterCallback(this);
        uart.disconnect();
        uart.registerCallback(this);
        uart.connectGATT(nRF51.selectedBTDevice);

        nRF51.BLEuart = uart;

//        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
//        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//        mBluetoothGatt.writeDescriptor(descriptor);

    }

    // OnStop, called right before the activity loses foreground focus.  Close the BTLE connection.
    @Override
    protected void onStop()
    {
        super.onStop();
        uart.unregisterCallback(this);
        uart.disconnect();
    }

    @Override
    public void onConnected(BluetoothLeUart uart)
    {
        writeLine("Device connected...");
    }

    @Override
    public void onConnectFailed(BluetoothLeUart uart)
    {

    }

    @Override
    public void onDisconnected(BluetoothLeUart uart)
    {

    }

    @Override
    public void onReceive(BluetoothLeUart uart, BluetoothGattCharacteristic rx) {
        // Called when data is received by the UART.
        writeLine("Received: " + rx.getStringValue(0));
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        // Called when a UART device is discovered (after calling startScan).
        writeLine("Found device : " + device.getAddress());
        writeLine("Waiting for a connection ...");
    }

    @Override
    public void onDeviceInfoAvailable() {
        writeLine(uart.getDeviceInfo());
    }

    public void clearButtonClick(View view)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btLeUARTtraceTextView.setText("");
            }
        });
    }
}
