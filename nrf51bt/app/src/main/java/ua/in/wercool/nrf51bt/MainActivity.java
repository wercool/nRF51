package ua.in.wercool.nrf51bt;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity
{
    NRF51 nRF51;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nRF51 = (NRF51) getApplication();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    // OnStop, called right before the activity loses foreground focus.  Close the BTLE connection.
    @Override
    protected void onStop()
    {
        super.onStop();
        if (nRF51.BLEuart != null)
        {
            if (nRF51.BLEuartCallback != null)
            {
                nRF51.BLEuart.unregisterCallback(nRF51.BLEuartCallback);
            }
            nRF51.BLEuart.disconnect();
            nRF51.BLEuart = null;
        }
    }

    @Override
    public void onDestroy()
    {
        // RUN SUPER | REGISTER ACTIVITY AS NULL IN APP CLASS

        super.onDestroy();
        this.finish();
        System.exit(0);
    }

    public void exit(View v)
    {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void scanForDevicesButtonClick(View view)
    {
        Intent intent = new Intent(this, BTDevicesActivity.class);
        startActivity(intent);
    }

    public void testServerConnectionButtonClick(View view)
    {
        nRF51.serverConnectionHandler = new ServerConnectionHandler();
        int statusCode = nRF51.serverConnectionHandler.connect();

        if (statusCode == HttpURLConnection.HTTP_OK)
        {
            Toast.makeText(getApplicationContext(), "Successfully connected", Toast.LENGTH_LONG).show();
            nRF51.serverConnectionHandler.disconnect();
        }
        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED)
        {
            Toast.makeText(getApplicationContext(), "Unauthorized", Toast.LENGTH_LONG).show();
            nRF51.serverConnectionHandler.disconnect();
        }
    }


}
