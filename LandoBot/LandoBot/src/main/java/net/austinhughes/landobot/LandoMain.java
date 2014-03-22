/*
    LandoMain.java
    This is the main source file for the LandoBot app
    This app sends serial commands via bluetooth to the LandoBot

    This app was written by Austin Hughes
    Modified Last: 2014-03-14
 */

package net.austinhughes.landobot;

// Java imports
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;


// Android imports
import android.app.*;
import android.os.Bundle;
import android.os.*;
import android.view.*;
import android.bluetooth.*;
import android.widget.*;
import android.content.*;


// Main view/Process
public class LandoMain extends Activity //implements SensorEventListener
{
    // Bluetooth setup
    private final static int REQUEST_ENABLE_BT = 1;
    public static BluetoothSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Create the main UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lando_main);

        // Ask user to enable Bluetooth if it is off
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lando_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //do things here
        return true;
    }

    // Gets the device address of any paired device, only works for paired devices
    public void getAddresses(View v)
    {
        try
        {
            Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

            // If there are paired devices
            if (pairedDevices.size() > 0)
            {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices)
                {
                    // Toast address
                    Toast toast = Toast.makeText(getBaseContext(),
                            device.getName() + "\n" + device.getAddress(), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
        catch(Exception e) // Toast error
        {
            Toast toast = Toast.makeText(getBaseContext(),
                    "Something broke :(\nError: " + e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void startConnection()
    {
        final ExecutorService service;
        final Future<BluetoothSocket> task;
        service = Executors.newFixedThreadPool(1);

        task = service.submit(new Connect());

        try
        {
            socket = task.get();
        }

        catch(final InterruptedException ex)
        {
            ex.printStackTrace();
        }
        catch(final ExecutionException ex)
        {
            ex.printStackTrace();
        }

        service.shutdownNow();

        if(socket.isConnected())
        {
            Toast toast = Toast.makeText(getBaseContext(), "Connected", Toast.LENGTH_LONG);
            toast.show();
        }
        else
        {
            Toast toast = Toast.makeText(getBaseContext(), "Couldn't Connect", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    // Attempts to connect to the LandoBot
    public void connect(View v)
    {
        // Ask user to enable Bluetooth if it is off
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        startConnection();
    }

    // Serial code to go forward
    public void forward(View v) throws IOException
    {
        if(socket != null)
        {
            send signal = new send(socket, "#1700,1300$");
            Handler handler = new Handler();
            handler.post(signal);
        }
    }

    // Serial code to go backwards
    public void reverse(View v)
    {
        if(socket != null)
        {
            send signal = new send(socket, "#1300,1700$");
            Handler handler = new Handler();
            handler.post(signal);
        }
    }

    // Serial code to turn left
    public void turnLeft(View v)
    {
        if(socket != null)
        {
            send signal = new send(socket, "#1300,1500$");
            Handler handler = new Handler();
            handler.post(signal);
        }
    }

    // Serial code to turn right
    public void turnRight(View v)
    {
        if(socket != null)
        {
            send signal = new send(socket, "#1500,1700$");
            Handler handler = new Handler();
            handler.post(signal);
        }
    }

    // Serial code to stop
    public void stop(View v)
    {
        if(socket != null)
        {
            send signal = new send(socket, "#1500,1500$");
            Handler handler = new Handler();
            handler.post(signal);
        }
    }

    // debug message to test character encoding
    public void sendDebug(View v) throws IOException
    {
        if(socket != null)
        {
            send signal = new send(socket, "!@#$%^&*()_+1234567890qwertyuiopasdfghjklzxcvbnm");
            Handler handler = new Handler();
            handler.post(signal);
        }
    }

    public void send(View v)
    {
        try
        {
            EditText mEdit = (EditText)findViewById(R.id.debugInput);

            if(socket != null)
            {
                send signal = new send(socket, mEdit.getText().toString());
                Handler handler = new Handler();
                handler.post(signal);
            }
        }
        catch(Exception e)
        {
            Toast toast = Toast.makeText(getBaseContext(), "Something Broke :(", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
