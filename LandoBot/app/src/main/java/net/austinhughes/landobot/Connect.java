package net.austinhughes.landobot;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class Connect implements Callable<BluetoothSocket>
{
    public BluetoothSocket call()
    {
        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();

        BluetoothDevice landoBot = bt.getRemoteDevice("00:06:66:6B:B1:49");

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        BluetoothSocket socket = null;

        try
        {

            socket=landoBot.createRfcommSocketToServiceRecord(uuid);

            socket.connect();
        }
        catch(Exception e)
        {
            Log.d("net.austinhughes.landobot", "exception thrown ");
            e.printStackTrace();
        }

        return socket;
    }
}
