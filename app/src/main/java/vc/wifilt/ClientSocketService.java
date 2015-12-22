package vc.wifilt;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Establishes a socket to connect to and transfer data with a ServerSocket.
 * Initialize with an intent contains ServerSocket's IP named "EXTRA_IP",
 * ServerSocket's port named "EXTRA_PORT", and the data to send named
 * "EXTRA_DATA", which should be Serializable.
 */
public class ClientSocketService extends IntentService {
    public static final String TAG = "ClientSocket";

    public ClientSocketService() {
        super("ClientSocketService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            intent.setAction("WIFI_DIRECT_SOCKET");
            String ip = intent.getStringExtra("EXTRA_IP");
            int port = intent.getIntExtra("EXTRA_PORT", 31067);
            Serializable data = intent.getSerializableExtra("EXTRA_DATA");
            try {
//                Socket socket = new Socket(ip, port);
                byte[] message = data.toString().getBytes();
                DatagramPacket packet = new DatagramPacket(message, message.length);
                packet.setAddress(InetAddress.getByName(ip));
                packet.setPort(port);
//                DatagramSocket socket = ServerSocketService.mServerSocket;
//                socket.setBroadcast(true);
                DatagramSocket socket = new DatagramSocket();
                socket.send(packet);

                Log.d(TAG, "ClientSocket start");
//                ObjectOutputStream objectOutputStream =
//                        new ObjectOutputStream(socket.getOutputStream());
//                objectOutputStream.writeObject(data);
                Log.d(TAG, "Socket sent: " + data.toString());
//                socket.close();
                Log.d(TAG, "ClientSocket close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}