package ua.in.wercool.nrf51bt;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BTDevicesActivity extends AppCompatActivity
{
    NRF51 nRF51;

    private static final int REQUEST_ENABLE_BT = 333;

    ListView btDeviceListView;
    List<Map<String, String>> btDevicesList = new ArrayList<Map<String, String>>();
    SimpleAdapter btDevicesListAdapter;
    List<BluetoothDevice> btDevices = new ArrayList<BluetoothDevice>(){};
    ProgressDialog progressDialog;

    int selectedBTDevicesListPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btdevices);

        nRF51 = (NRF51) getApplication();

        progressDialog = new ProgressDialog(this);

        btDeviceListView = (ListView) findViewById(R.id.btDeviceListView);
        btDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectedBTDevicesListPosition = position;
                nRF51.selectedBTDevice = btDevices.get(position);

                Intent intent = new Intent(getApplicationContext(), BTDeviceActivity.class);
                startActivity(intent);
            }

        });

        Map<String, String> datum = new HashMap<String, String>(2);
        datum.put("name", "...");
        datum.put("mac", "");
        btDevicesList.add(datum);

        btDevicesListAdapter = new SimpleAdapter(this, btDevicesList,
                                                    android.R.layout.simple_expandable_list_item_2,
                                                    new String[]{"name", "mac"},
                                                    new int[]{android.R.id.text1,
                                                    android.R.id.text2});

        btDeviceListView.setAdapter(btDevicesListAdapter);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        nRF51.mBluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            nRF51.mBluetoothLeScanner = nRF51.mBluetoothAdapter.getBluetoothLeScanner();
        }


        boolean startScanning = true;

        if (nRF51.mBluetoothAdapter == null)
        {
            Context context = getApplicationContext();
            Toast.makeText(context, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }

        if (!nRF51.mBluetoothAdapter.isEnabled())
        {
            startScanning = false;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Use this check to determine whether BLE is supported on the device. Then you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, "BLE is unsupported on the device.", Toast.LENGTH_SHORT).show();
            finish();
        }

//        // Register for broadcasts when a device is discovered.
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter);

        if (startScanning)
        {
            scanForDevicesButtonClick(null);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
//        unregisterReceiver(mReceiver);
    }

    public void scanForDevicesButtonClick(View view)
    {
        btDevices.clear();
        btDevicesList.clear();
        btDevicesListAdapter.notifyDataSetChanged();

//        nRF51.mBluetoothAdapter .startDiscovery();
        if (nRF51.mBluetoothLeScanner != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                nRF51.mBluetoothLeScanner.startScan((ScanCallback) mLeScanCallback);
            }
        }
        else
        {
            nRF51.mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
//        Log.d(nRF51.TAG, "startDiscovery");

        Log.d(nRF51.TAG, "startLeScan");

        progressDialog.setTitle("In progress");
        progressDialog.setMessage("BT Devices discovery...");
        progressDialog.show();

        Runnable progressRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                if (nRF51.mBluetoothLeScanner != null)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        nRF51.mBluetoothLeScanner.stopScan((ScanCallback) mLeScanCallback);
                    }
                }
                else
                {
                    nRF51.mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }

                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 10000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 333)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth is enabled", Toast.LENGTH_LONG).show();
            scanForDevicesButtonClick(null);
        }
    }

    private void addBTDeviceToList(BluetoothDevice device)
    {
        boolean newDevice = true;

        for (BluetoothDevice bt : btDevices)
        {
            if (bt.getAddress().equals(device.getAddress()))
            {
                newDevice = false;
            }
        }

        if (newDevice)
        {
            btDevices.add(device);

            Map<String, String> datum = new HashMap<String, String>(2);
            if (device.getAddress().equals("D0:31:B1:A8:4A:3A"))
            {
                datum.put("name", "nRF51");
            }
            else
            {
                if (device.getName() == null )
                {
                    datum.put("name", "undefined");
                }
            }
            datum.put("mac", device.getAddress());
            btDevicesList.add(datum);

            btDevicesListAdapter.notifyDataSetChanged();

            if (device.getAddress().equals("D0:31:B1:A8:4A:3A"))
            {
                nRF51.mBluetoothAdapter.stopLeScan(mLeScanCallback);
                progressDialog.cancel();
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
//    {
//        public void onReceive(Context context, Intent intent)
//        {
//            progressDialog.hide();
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action))
//            {
//                // Discovery has found a device. Get the BluetoothDevice
//                // object and its info from the Intent.
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//
//                Log.d(nRF51.TAG, "deviceName:" + deviceName + ", deviceHardwareAddress:" + deviceHardwareAddress);
//
//                addBTDeviceToList(device);
//            }
//        }
//    };

    // LE BT Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback(){
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress(); // MAC address

                            Log.d(nRF51.TAG, "deviceName:" + deviceName + ", deviceHardwareAddress:" + deviceHardwareAddress);

                            addBTDeviceToList(device);
                        }
                    });
                }
    };

}
