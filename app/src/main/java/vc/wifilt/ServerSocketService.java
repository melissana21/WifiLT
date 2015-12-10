package vc.wifilt;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Provides a ServerSocket waiting for socket to connect.
 * Initialize with an intent contains the port to open named "EXTRA_PORT".
 * To get the received data, please implement BroadcastReceiver with LocalBroadcastManager,
 * and set IntentFilter as "WIFI_DIRECT_SOCKET", then call
 * getSerializableExtra("EXTRA_DATA") with the received intent.
 */
public class ServerSocketService extends Service {
    private ServerSocket mServerSocket = null;
    public static final String TAG = "ServerSocket";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int port = intent.getIntExtra("EXTRA_PORT", 31067);
        try {
            mServerSocket = new ServerSocket(port);
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
        try {
            mServerSocket.close();
            Log.d(TAG, "ServerSocket close");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

class ServerThread extends Thread {
    private Context mContext = null;
    private ServerSocket mServerSocket = null;

    public ServerThread(Context context, ServerSocket serverSocket) {
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
                Socket socket = mServerSocket.accept();
                ObjectInputStream objectInputStream =
                        new ObjectInputStream(socket.getInputStream());
                Serializable data = (Serializable) objectInputStream.readObject();
                String clientIP = socket.getInetAddress().getHostAddress();
                Log.d(ServerSocketService.TAG, "client ip address: " + clientIP);
                Intent intent = new Intent("SOCKET_PACKET");
                intent.putExtra("EXTRA_DATA", data);
                intent.putExtra("EXTRA_IP",clientIP);

                Log.d(ServerSocketService.TAG, "Socket accept: " + data.toString());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
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