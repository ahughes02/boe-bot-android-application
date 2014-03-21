package net.austinhughes.landobot;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LandoConnection extends Thread
{
    private static final String TAG = "LandoBot";
    private final Handler btHandler;

    UUID uuid = UUID.fromString("7A86F613-837F-4D0B-9711-8BFB11DE3CA7");

    InputStream btInStream = null;
    OutputStream btOutStream = null;

    private BluetoothSocket btSocket = null;
    private final BluetoothAdapter btAdapter;

    public LandoConnection(Context context, Handler handler)
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btHandler = handler;

        BluetoothDevice landoBot = btAdapter.getRemoteDevice("00:06:66:61:5F:50");

        Log.d(TAG, "Connected thread constructor");

        InputStream tmpIn = null;
        OutputStream tmpOut = null;


        // Get the BluetoothSocket input and output streams
        try
        {
            btSocket = landoBot.createRfcommSocketToServiceRecord(uuid);
            btSocket.connect();

            tmpIn = btSocket.getInputStream();
            tmpOut = btSocket.getOutputStream();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error getting streams", e);
        }

        btInStream = tmpIn;
        btOutStream = tmpOut;

        Log.d(TAG, "Connected thread constructor over");
    }

    public void run()
    {
        Log.d(TAG, "Connected thread.run()");

        byte[] buffer = new byte[1024];
        int bytes;

        // Keep listening to the InputStream while connected
        while (true)
        {
            try
            {
                // Read from the InputStream
                bytes = btInStream.read(buffer);

                // Send the obtained bytes to the UI Activity
                btHandler.obtainMessage(LandoMain.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
            }
            catch (Exception e)
            {
                Log.e(TAG, "disconnected", e);
                connectionLost();
                break;
            }
        }
    }

    /**
     * Write to the connected OutStream.
     * @param buffer  The bytes to write
     */
    public void write(byte[] buffer)
    {
        Log.d(TAG, "Writing stuff");
        try
        {
            btOutStream.write(buffer);

            // Share the sent message back to the UI Activity
            btHandler.obtainMessage(LandoMain.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception during write", e);
        }
    }

    public void cancel()
    {
        Log.d(TAG, "Closing btSocket");
        try
        {
            btSocket.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }

//    /**
//     * Indicate that the connection was lost and notify the UI Activity.
//     */
    private void connectionLost()
    {
        // Send a failure message back to the Activity
        Message msg = btHandler.obtainMessage(LandoMain.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(LandoMain.TOAST, "Device connection was lost");
        msg.setData(bundle);
        btHandler.sendMessage(msg);
    }

}
