package vc.wifilt;


import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;



public class PacketProcessingService extends Thread {
    String TAG = "PacketProcessing";
    Intent mIntent;

    String output;

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
                    try {
                        MainActivity.sFileOutputStream.write((MainActivity.convertIntArrayToString(declaration.globalDecodedSymbolsRecord)+"\n").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    returnData.setPosition(index);
                    returnData.setDes(packetData.getOri());
                    MainActivity.sendPacket(returnData);
                    Log.v(TAG, "send answer global record: " + index);
                    break;
                case "REQUEST_GLOBAL_DECVAL":
                    if (!MainActivity.sIsGroupOwner) {
                        return;
                    }
                    if (declaration.globalDecodedSymbolsRecord[packetData.getPosition() / declaration.messageSize[declaration.currentLayer]] != 0) {
                        byte[] array = Arrays.copyOfRange(declaration.decVal, packetData.getPosition(), packetData.getPosition() + declaration.messageSize[declaration.currentLayer]);
                        returnData = new PacketData("ANSWER_GLOBAL_DECVAL", array);
                    } else {
                        returnData = new PacketData("ANSWER_GLOBAL_DECVAL", String.valueOf(-1).getBytes());
                    }
                    returnData.setPosition(packetData.getPosition());
                    returnData.setDes(packetData.getOri());
                    MainActivity.sendPacket(returnData);
                    Log.v(TAG, "send answer global decval: " + packetData.getPosition());
                    break;
                case "UPDATE_GLOBAL_DECVAL":
                    if (!MainActivity.sIsGroupOwner) {
                        return;
                    }
                    synchronized (declaration.decVal) {
                        System.arraycopy(packetData.getData(), 0, declaration.decVal, packetData.getPosition(), declaration.messageSize[declaration.currentLayer]);
                    }
                    synchronized (declaration.globalDecodedSymbolsRecord) {
                        declaration.globalDecodedSymbolsRecord[packetData.getPosition() / declaration.messageSize[declaration.currentLayer]] = 1;
                    }
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
                    output = (System.currentTimeMillis() - MainActivity.sRequestRecordTime) + "\n";
                    try {
                        MainActivity.sRequestRecordDelayStream.write(output.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    Log.v("packet delay", "process: " + System.currentTimeMillis());
                    Log.v(TAG, "receive answer global record: " + packetData.getPosition());
//                    int data = Integer.parseInt(new String(packetData.getData()));
                    try {
                        MainActivity.sFileOutputStream.write(packetData.getData());
                        MainActivity.sFileOutputStream.write("\n".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int[] data = MainActivity.convertStringToIntArray(new String(packetData.getData()));
                    synchronized (declaration.globalDecodedSymbolsRecord) {
                        declaration.globalDecodedSymbolsRecord = data;
                    }
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
                    output = (System.currentTimeMillis() - MainActivity.sRequestDecvalTime) + "\n";
                    try {
                        MainActivity.sRequestDecvalDelayStream.write(output.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.v(TAG, "receive answer global decval: " + packetData.getPosition());
                    if ((new String(packetData.getData())).equals(String.valueOf(-1))) {
                        synchronized (declaration.globalDecodedSymbolsRecord) {
                            declaration.globalDecodedSymbolsRecord[packetData.getPosition() / declaration.messageSize[declaration.currentLayer]] = 0;
                        }
                        Log.v("recorderror", "it's in");
                    } else {
                        synchronized (declaration.decVal) {
                            System.arraycopy(packetData.getData(), 0, declaration.decVal, packetData.getPosition(), declaration.messageSize[declaration.currentLayer]);
                        }
                    }
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
                    output = (System.currentTimeMillis() - MainActivity.sUpdateDecvalTime) + "\n";
                    try {
                        MainActivity.sUpdateDecvalDelayStream.write(output.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
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
