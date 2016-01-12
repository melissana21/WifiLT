package vc.wifilt;


import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;



public class PacketProcessingService extends Thread {
    String TAG = "PacketProcessing";
    Intent mIntent;

    public PacketProcessingService(Intent intent) {
        mIntent = new Intent(intent);
        Log.v(TAG, "init packet process");
    }

    public void run() {
        if (mIntent != null) {
            Log.v(TAG, "run packet process");
            Gson gson = new Gson();
            String message = mIntent.getStringExtra("EXTRA_DATA");
//                                    Log.v("receive", message);
                                    Log.v(TAG, "byte to string: " + new String(message));
            PacketData packetData = gson.fromJson(message, PacketData.class);
            PacketData returnData;
            String type = packetData.getType();
            int index;
            Log.v(TAG, "type: " + type);
            switch (type) {
                case "REQUEST_GLOBAL_RECORD":
                    if (!MainActivity.sIsGroupOwner) {
                        return;
                    }
                    index = Integer.parseInt(new String(packetData.getData()));
                    returnData =
                            new PacketData("ANSWER_GLOBAL_RECORD",
                                    String.valueOf(declaration.globalDecodedSymbolsRecord[index]).getBytes());
                    returnData.setPosition(index);
                    returnData.setDes(packetData.getOri());
                    MainActivity.sendPacket(returnData);
                    Log.v(TAG, "send answer global record: " + index);
                    break;
                case "REQUEST_GLOBAL_DECVAL":
                    if (!MainActivity.sIsGroupOwner) {
                        return;
                    }
                    byte[] array = Arrays.copyOfRange(declaration.decVal, packetData.getPosition(), packetData.getPosition() + declaration.messageSize[declaration.currentLayer]);
                    returnData = new PacketData("ANSWER_GLOBAL_DECVAL", array);
                    returnData.setPosition(packetData.getPosition());
                    returnData.setDes(packetData.getOri());
                    MainActivity.sendPacket(returnData);
                    Log.v(TAG, "send answer global decval: " + packetData.getPosition());
                    break;
                case "UPDATE_GLOBAL_DECVAL":
                    if (!MainActivity.sIsGroupOwner) {
                        return;
                    }
                    System.arraycopy(packetData.getData(), 0, declaration.decVal, packetData.getPosition(), declaration.messageSize[declaration.currentLayer]);
                    declaration.globalDecodedSymbolsRecord[packetData.getPosition()/declaration.messageSize[declaration.currentLayer]]=1;
                    break;
                case "UPDATE_GLOBAL_RECORD":
                    if (!MainActivity.sIsGroupOwner) {
                        return;
                    }
                    declaration.globalDecodedSymbolsRecord[packetData.getPosition()] = Integer.parseInt(new String(packetData.getData()));
//                    Log.v(TAG, "UPDATE: " + declaration.globalDecodedSymbolsRecord[packetData.getPosition()]);
//                    Log.v(TAG,"position : " + packetData.getPosition());
                    break;
                case "ANSWER_GLOBAL_RECORD":
                    if (!packetData.getDes().equals(MainActivity.mMacAddress)) {
                        return;
                    }
                    Log.v(TAG, "receive answer global record: " + packetData.getPosition());
                    int data = Integer.parseInt(new String(packetData.getData()));
                    declaration.globalDecodedSymbolsRecord[packetData.getPosition()] = data;
                    synchronized (MainActivity.waitingLock) {
                        Log.v(TAG, "unlock waiting");
                        MainActivity.isWaiting = false;
                        MainActivity.waitingLock.notify();
                    }
                    break;
                case "ANSWER_GLOBAL_DECVAL":
                    if (!packetData.getDes().equals(MainActivity.mMacAddress)) {
                        return;
                    }
                    Log.v(TAG, "receive answer global decval: " + packetData.getPosition());
                    System.arraycopy(packetData.getData(), 0, declaration.decVal, packetData.getPosition(), declaration.messageSize[declaration.currentLayer]);
                    synchronized (MainActivity.waitingLock) {
                        MainActivity.isWaiting = false;
                        MainActivity.waitingLock.notify();
                    }
                    break;
                case "TEST":
                    Toast.makeText(MainActivity.MainContext, "Receive: " + new String(packetData.getData()), Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "data: " + new String(packetData.getData()));
                    break;
                default:
                    Toast.makeText(MainActivity.MainContext, "Receive: " + new String(packetData.getData()), Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "data: " + new String(packetData.getData()));
                    break;
            }
        }
    }
}
