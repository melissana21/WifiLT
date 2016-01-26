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
//        Log.v(TAG, "init packet process");
    }

    public void run() {
        if (mIntent != null) {
//            Log.v(TAG, "run packet process");
//            Gson gson = new Gson();
//            String message = mIntent.getStringExtra("EXTRA_DATA");
//                                    Log.v("receive", message);
//                                    Log.v(TAG, "byte to string: " + new String(message));
//            PacketData packetData = gson.fromJson(message, PacketData.class);
            PacketData packetData = (PacketData) mIntent.getSerializableExtra("EXTRA_DATA");
            PacketData returnData;
            String type = packetData.getType();
            int index;
//            Log.v(TAG, "type: " + type);
            switch (type) {
                case "REQUEST_GLOBAL_RECORD":
                    if (!MainActivity.sIsGroupOwner) {
                        return;
                    }
                    index = Integer.parseInt(new String(packetData.getData()));
                    returnData =
                            new PacketData("ANSWER_GLOBAL_RECORD",
                                    MainActivity.convertIntArrayToString(declaration.globalDecodedSymbolsRecord).getBytes());
//                                    String.valueOf(declaration.globalDecodedSymbolsRecord[index]).getBytes());
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
                    returnData = new PacketData("SUCESS_UPDATE_DECVAL", String.valueOf(1).getBytes());
                    returnData.setPosition(packetData.getPosition());
                    returnData.setDes(packetData.getOri());
                    MainActivity.sendPacket(returnData);
                    break;
//                case "UPDATE_GLOBAL_RECORD":
//                    if (!MainActivity.sIsGroupOwner) {
//                        return;
//                    }
//                    declaration.globalDecodedSymbolsRecord[packetData.getPosition()] = Integer.parseInt(new String(packetData.getData()));
////                    Log.v(TAG, "UPDATE: " + declaration.globalDecodedSymbolsRecord[packetData.getPosition()]);
////                    Log.v(TAG,"position : " + packetData.getPosition());
//                    break;
                case "ANSWER_GLOBAL_RECORD":
//                    Log.v(TAG, "receive answer");
//                    Log.v(TAG, "des: " + packetData.getDes());
//                    Log.v(TAG, "my mac: " + MainActivity.mMacAddress);
                    if (!packetData.getDes().equals(MainActivity.mMacAddress) || declaration.isRecordUpdate[packetData.getPosition()]) {
                        return;
                    }
//                    Log.v("packet delay", "process: " + System.currentTimeMillis());
                    Log.v(TAG, "receive answer global record: " + packetData.getPosition());
//                    int data = Integer.parseInt(new String(packetData.getData()));
                    int[] data = MainActivity.convertStringToIntArray(new String(packetData.getData()));
                    declaration.globalDecodedSymbolsRecord = data;
//                    declaration.globalDecodedSymbolsRecord[packetData.getPosition()] = data;
//                    Log.v(TAG, "position = " + packetData.getPosition());
//                    Log.v(TAG, "before: " +declaration.isRecordUpdate[packetData.getPosition()]);
                    declaration.isRecordUpdate[packetData.getPosition()] = true;
//                    Log.v(TAG, "after: " +declaration.isRecordUpdate[packetData.getPosition()]);

                    synchronized (MainActivity.waitingLock) {
                        Log.v(TAG, "unlock waiting");
//                        MainActivity.isWaiting = false;
                        MainActivity.waitingLock.notify();
                    }
                    break;
                case "ANSWER_GLOBAL_DECVAL":
                    if (!packetData.getDes().equals(MainActivity.mMacAddress)
                            || declaration.isDecvalUpdate[(packetData.getPosition() / declaration.messageSize[declaration.currentLayer])]) {
                        return;
                    }
                    Log.v(TAG, "receive answer global decval: " + packetData.getPosition());
                    System.arraycopy(packetData.getData(), 0, declaration.decVal, packetData.getPosition(), declaration.messageSize[declaration.currentLayer]);
                    declaration.isDecvalUpdate[(packetData.getPosition() / declaration.messageSize[declaration.currentLayer])] = true;
                    synchronized (MainActivity.waitingLock) {
//                        MainActivity.isWaiting = false;
                        MainActivity.waitingLock.notify();
                    }
                    break;
                case "SUCESS_UPDATE_DECVAL":
                    if (!packetData.getDes().equals(MainActivity.mMacAddress)
                            || declaration.isGlobalDecvalUpdate[(packetData.getPosition() / declaration.messageSize[declaration.currentLayer])]) {
                        return;
                    }
                    declaration.isGlobalDecvalUpdate[(packetData.getPosition() / declaration.messageSize[declaration.currentLayer])] = true;
                    synchronized (MainActivity.waitingLock) {
//                        MainActivity.isWaiting = false;
                        MainActivity.waitingLock.notify();
                    }
                    break;
                default:
//                    Toast.makeText(MainActivity.MainContext, "Receive: " + new String(packetData.getData()), Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "data: " + new String(packetData.getData()));
                    break;
            }
        }
    }
}
