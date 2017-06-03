package ua.in.wercool.nrf51bt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity
{
    NRF51 nRF51;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nRF51 = (NRF51) getApplication();
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
}
