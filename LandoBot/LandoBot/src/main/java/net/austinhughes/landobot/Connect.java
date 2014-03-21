package net.austinhughes.landobot;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class Connect implements Callable<BluetoothSocket>
{
    public BluetoothSocket call()
    {
        BluetoothSocket socket = null;

        try
        {
            BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();

            BluetoothDevice landoBot = bt.getRemoteDevice("00:06:66:61:5F:50");

            UUID uuid = UUID.fromString("7A86F613-837F-4D0B-9711-8BFB11DE3CA7");

            socket=landoBot.createRfcommSocketToServiceRecord(uuid);

            socket.connect();
        }
        catch(Exception e)
        { }

        return socket;
    }
}
