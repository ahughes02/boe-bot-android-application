/*
    LandoMain.java
    This is the main source file for the LandoBot app
    This app sends serial commands via bluetooth to the LandoBot

    This app was written by Austin Hughes
    Modified Last: 2014-03-17
 */

package net.austinhughes.landobot;

// Java imports
import java.io.IOException;
import java.util.*;


// Android imports
import android.app.*;
import android.os.Bundle;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.bluetooth.*;
import android.widget.*;
import android.content.*;


// Main view/Process
public class LandoMain extends Activity //implements SensorEventListener
{
    private static final String TAG = "LandoBot";

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final int STATE_NONE = 0;       // we're doing nothing
    // public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Bluetooth setup
    private final static int REQUEST_ENABLE_BT = 1;
    public static BluetoothAdapter btAdaptor = BluetoothAdapter.getDefaultAdapter();

    private LandoConnection lConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Create the main UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lando_main);

        // If the adapter is null, then Bluetooth is not supported
        if (btAdaptor == null)
        {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Ask user to enable Bluetooth if it is off
        // setupChat() will then be called during onActivityResult
        if (!btAdaptor.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        else
        {
            if (lConnection == null) setup();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Stop the Bluetooth services
        if (lConnection != null) lConnection.cancel();
    }

//    @Override
//    public synchronized void onResume()
//    {
//        super.onResume();
//
//        // do stuff on resume here if needed
//    }

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

    private void setup()
    {
        Log.d(TAG, "setup()");

        // Initialize the BluetoothChatService to perform bluetooth connections

    }

    // Gets the device address of any paired device, only works for paired devices
    public void getAddresses(View v)
    {
        try
        {
            Set<BluetoothDevice> pairedDevices = btAdaptor.getBondedDevices();

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

    // Attempts to connect to the LandoBot
    public void connect(View v)
    {
        // Ask user to enable Bluetooth if it is off
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Log.d(TAG, "connect button pushed");

        lConnection = new LandoConnection(this, btHandler);
        lConnection.start();

        Log.d(TAG, "connect method called");
    }

    // Serial code to go forward
    public void forward(View v) throws IOException
    {

        lConnection.write("#1700,1300$".getBytes());
//        if(socket != null)
//        {
//            send signal = new send(socket, "#1700,1300$");
////            Handler handler = new Handler();
////            handler.post(signal);
//        }
    }

    // Serial code to go backwards
    public void reverse(View v)
    {
//        if(socket != null)
//        {
//            send signal = new send(socket, "#1300,1700$");
////            Handler handler = new Handler();
////            handler.post(signal);
//        }
    }

    // Serial code to turn left
    public void turnLeft(View v)
    {
//        if(socket != null)
//        {
//            send signal = new send(socket, "#1300,1500$");
////            Handler handler = new Handler();
////            handler.post(signal);
//        }
    }

    // Serial code to turn right
    public void turnRight(View v)
    {
//        if(socket != null)
//        {
//            send signal = new send(socket, "#1500,1700$");
////            Handler handler = new Handler();
////            handler.post(signal);
//        }
    }

    // Serial code to stop
    public void stop(View v)
    {
        lConnection.write("#1500,1500$".getBytes());
//        if(socket != null)
//        {
//            send signal = new send(socket, "#1500,1500$");
//            Handler handler = new Handler();
//            handler.post(signal);
//        }
    }

    // debug message to test character encoding
    public void sendDebug(View v) throws IOException
    {
//        if(socket != null)
//        {
//            send signal = new send(socket, "!@#$%^&*()_+1234567890qwertyuiopasdfghjklzxcvbnm");
//            Handler handler = new Handler();
//            handler.post(signal);
//        }
    }

    public void send(View v)
    {
        try
        {
            EditText mEdit = (EditText)findViewById(R.id.debugInput);

//            if(socket != null)
//            {
//                send signal = new send(socket, mEdit.getText().toString());
//                Handler handler = new Handler();
//                handler.post(signal);
//            }
        }
        catch(Exception e)
        {
            Toast toast = Toast.makeText(getBaseContext(), "Something Broke :(", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler btHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MESSAGE_STATE_CHANGE:

                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);

                    switch (msg.arg1)
                    {
                        case STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to, btConnectedDeviceName));
                            break;
                        case STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            break;
                        case STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;

                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    Toast.makeText(getApplicationContext(), "Sent "
                            + writeMessage, Toast.LENGTH_SHORT).show();

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    Toast.makeText(getApplicationContext(), "Received "
                            + readMessage, Toast.LENGTH_SHORT).show();

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String btConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + btConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK)
                {
                   // Bluetooth is now enabled, so set up a chat session
                    setup();
                } else
                {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "Bluetooth not enabled");
                    Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}
