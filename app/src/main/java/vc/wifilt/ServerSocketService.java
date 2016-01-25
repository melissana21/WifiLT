package vc.wifilt;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

/**
 * Provides a ServerSocket waiting for socket to connect.
 * Initialize with an intent contains the port to open named "EXTRA_PORT".
 * To get the received data, please implement BroadcastReceiver with LocalBroadcastManager,
 * and set IntentFilter as "WIFI_DIRECT_SOCKET", then call
 * getSerializableExtra("EXTRA_DATA") with the received intent.
 */
public class ServerSocketService extends Service {
//    private ServerSocket mServerSocket = null;
    protected static DatagramSocket mServerSocket = null;
    public static final String TAG = "ServerSocket";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int port = intent.getIntExtra("EXTRA_PORT", 31067);
        try {
//            mServerSocket = new ServerSocket(port);
            mServerSocket = new DatagramSocket(port);

            InetAddress address = null;

            Log.d(TAG, "ServerSocket start");
            ServerThread serverThread = new ServerThread(this, mServerSocket);
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mServerSocket.close();
        Log.d(TAG, "ServerSocket close");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

class ServerThread extends Thread {
    private Context mContext = null;
//    private ServerSocket mServerSocket = null;
    private DatagramSocket mServerSocket = null;

    public ServerThread(Context context, DatagramSocket serverSocket) {
        super();
        mContext = context;
        mServerSocket = serverSocket;
        Log.d(ServerSocketService.TAG, "Thread start");
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (mServerSocket.isClosed()) {
                    return;
                }

                DatagramPacket packet = new DatagramPacket(new byte[81920], 81920);
                mServerSocket.receive(packet);
                String clientIP = packet.getAddress().toString();
                String data = new String(packet.getData(), packet.getOffset(), packet.getLength());

                Gson gson = new Gson();
                PacketData packetData = gson.fromJson(data, PacketData.class);
                if (packetData.getType().equals("OWNER_ADDRESS")) {
                    MainActivity.mOwnerAddress = new String(packetData.getData());
                    Log.v(ServerSocketService.TAG, "owner address: " + MainActivity.mOwnerAddress);
                    continue;
                }
                if (!packetData.getDes().equals(MainActivity.mMacAddress)) {
                    continue;
                }

/*                Socket socket = mServerSocket.accept();
                ObjectInputStream objectInputStream =
                        new ObjectInputStream(socket.getInputStream());
                Serializable data = (Serializable) objectInputStream.readObject();
                String clientIP = socket.getInetAddress().getHostAddress();
*///                Log.d(ServerSocketService.TAG, "client ip address: " + clientIP);
                Intent intent = new Intent("WIFI_DIRECT_SOCKET");
                intent.putExtra("EXTRA_DATA", packetData);
//                intent.putExtra("EXTRA_IP",clientIP);

//                Log.d(ServerSocketService.TAG, "Socket accept: " + data);
//                Log.v(ServerSocketService.TAG, "string: " + data.toString());
//                Log.v(ServerSocketService.TAG, "byte to string: " + new String(data));
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
            } catch (OptionalDataException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}