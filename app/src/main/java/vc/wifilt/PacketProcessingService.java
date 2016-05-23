package vc.wifilt;


import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


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

//                    int[] clientRecord = MainActivity.convertStringToIntArray(new String(packetData.getData()));
//                    Map<Integer, byte[]> valueMap = new HashMap<>();
//
//                    for (int i = 0; i < declaration.globalDecodedSymbolsRecord.length; i++) {
//                        if (clientRecord[i] == 0 && declaration.globalDecodedSymbolsRecord[i] >= 1) {
//                            valueMap.put(i, Arrays.copyOfRange(declaration.decVal,
//                                    declaration.messageSize[declaration.currentLayer] * i,
//                                    declaration.messageSize[declaration.currentLayer] * (i + 1)));
//                            Log.v("map", "send key: " + i);
//                            if (valueMap.size() == 10) {
//                                break;
//                            }
//                        }
//                    }
//
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    try {
//                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
//                        objectOutputStream.writeObject(valueMap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

//                    returnData =
//                            new PacketData("ANSWER_GLOBAL_RECORD", byteArrayOutputStream.toByteArray());

//                    index = Integer.parseInt(new String(packetData.getData()));
                    returnData =
                            new PacketData("ANSWER_GLOBAL_RECORD",
                                    MainActivity.convertIntArrayToString(declaration.globalDecodedSymbolsRecord).getBytes());
//                                    String.valueOf(declaration.globalDecodedSymbolsRecord[index]).getBytes());
//                    try {
//                        MainActivity.sFileOutputStream.write((MainActivity.convertIntArrayToString(declaration.globalDecodedSymbolsRecord)+"\n").getBytes());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    returnData.setPosition(index);
                    returnData.setPosition(0);
                    returnData.setDes(packetData.getOri());
                    MainActivity.sendPacket(returnData);
//                    Log.v(TAG, "send answer global record: " + index);
                    output = System.currentTimeMillis()+"\t"+type+"\n";
                    try {
                        MainActivity.sFileTimeStampRecord.write(output.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "REQUEST_GLOBAL_DECVAL":
                    if (!MainActivity.sIsGroupOwner) {
                        return;
                    }

                    // int[] clientIndices = MainActivity.convertStringToIntArray(new String(packetData.getData()));


                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packetData.getData());
                    Map<Integer, byte[]> valueMap = new HashMap<>();
                    try {
                        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                        Map<Integer, byte[]> InputMap = (Map<Integer, byte[]>) objectInputStream.readObject();
                        for (Integer key : InputMap.keySet()) {
                            valueMap.put(key, Arrays.copyOfRange(declaration.decVal,
                                    declaration.messageSize[declaration.currentLayer] *key,
                                    declaration.messageSize[declaration.currentLayer] * (key + 1)));
                            Log.v("map", "send key: " + key);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    output = System.currentTimeMillis()+"\t"+type+": Map Size = "+valueMap.size()+"\n";
                    try {
                        MainActivity.sFileTimeStampRecord.write(output.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    for (int i = 0; i < clientIndices.length; i++) {
//                        if (clientRecord[i] == 0 && declaration.globalDecodedSymbolsRecord[i] >= 1) {
//                            valueMap.put(clientIndices[i], Arrays.copyOfRange(declaration.decVal,
//                                    declaration.messageSize[declaration.currentLayer] * clientIndices[i],
//                                    declaration.messageSize[declaration.currentLayer] * (clientIndices[i] + 1)));
//                            Log.v("map", "send key: " + clientIndices[i]);
//                            if (valueMap.size() == 10) {
//                                break;
//                            }
//                        }
//                    }

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    try {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(valueMap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    returnData =
                            new PacketData("ANSWER_GLOBAL_DECVAL", byteArrayOutputStream.toByteArray());
//                    byte[] values = new byte[clientIndices.length * declaration.messageSize[declaration.currentLayer]];
//                    for (int i = 0; i < clientIndices.length; i++) {
//                        System.arraycopy(declaration.decVal,
//                                clientIndices[i] * declaration.messageSize[declaration.currentLayer],
//                                values,
//                                i * declaration.messageSize[declaration.currentLayer],
//                                declaration.messageSize[declaration.currentLayer]);
//                    }
//                    returnData = new PacketData("ANSWER_GLOBAL_DECVAL", values);

//                    if (declaration.globalDecodedSymbolsRecord[packetData.getPosition() / declaration.messageSize[declaration.currentLayer]] != 0) {
//                        byte[] array = Arrays.copyOfRange(declaration.decVal, packetData.getPosition(), packetData.getPosition() + declaration.messageSize[declaration.currentLayer]);
//                        returnData = new PacketData("ANSWER_GLOBAL_DECVAL", array);
//                    } else {
//                        returnData = new PacketData("ANSWER_GLOBAL_DECVAL", String.valueOf(-1).getBytes());
//                    }
//                    returnData.setPosition(packetData.getPosition());
                    returnData.setDes(packetData.getOri());
                    MainActivity.sendPacket(returnData);
                    Log.v(TAG, "send answer global decval: " + packetData.getPosition());
                    break;
                case "UPDATE_GLOBAL_DECVAL":
//                    if (!MainActivity.sIsGroupOwner) {
//                        return;
//                    }

                    declaration.waiting_time = System.currentTimeMillis();
                    Map<Integer, byte[]> UpdateMap = new HashMap<>();
                    byteArrayInputStream = new ByteArrayInputStream(packetData.getData());
                    try {
                        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                        UpdateMap = (Map<Integer, byte[]>) objectInputStream.readObject();
                        Log.v("update map", "map size: " + UpdateMap.size());
                        for (Integer key : UpdateMap.keySet()) {
                            Log.v("update map", "receive key: " + key);
                            MainActivity.setLogText("receive key: " + key);
                            synchronized (declaration.decVal) {
                                System.arraycopy(UpdateMap.get(key),
                                        0,
                                        declaration.decVal,
                                        declaration.messageSize[declaration.currentLayer] * key,
                                        declaration.messageSize[declaration.currentLayer]);
                            }
//                            synchronized (declaration.globalDecodedSymbolsRecord) {
//                                declaration.globalDecodedSymbolsRecord[key] = 1;
                            declaration.globalDecodedSymbolsRecord[key] = 1;
                            declaration.selfDecodedSymbolsRecord[0][key] = 1;
//                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    output = System.currentTimeMillis()+"\t"+type+": Map Size = "+ UpdateMap.size()+"\n";

                    try {
                        MainActivity.sFileTimeStampRecord.write(output.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    returnData = new PacketData("SUCESS_UPDATE_DECVAL",  MainActivity.convertIntArrayToString(declaration.globalDecodedSymbolsRecord).getBytes());//String.valueOf(1).getBytes());
//                    returnData.setPosition(packetData.getPosition());
//                    returnData.setDes(packetData.getOri());
//                    MainActivity.sendPacket(returnData);
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
//                    if (!packetData.getDes().equals(MainActivity.mMacAddress) || declaration.isRecordUpdate[packetData.getPosition()]) {
                    if(MainActivity.sIsGroupOwner){
                        return;
                    }
//                    output = (System.currentTimeMillis() - MainActivity.sRequestRecordTime) + "\n";
                    output = System.currentTimeMillis()+"\t"+type+"\n";

                    MainActivity.sRequestRecordTotalTime = MainActivity.sRequestRecordTotalTime + (System.currentTimeMillis() - MainActivity.sRequestRecordTime);
                    MainActivity.num_RequestRecord = MainActivity.num_RequestRecord + 1;
                    try {
                        //MainActivity.sRequestRecordDelayStream.write(output.getBytes());
                        MainActivity.sFileTimeStampRecord.write(output.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    Log.v("packet delay", "process: " + System.currentTimeMillis());
                    Log.v(TAG, "receive answer global record");
//                    int data = Integer.parseInt(new String(packetData.getData()));
//                    try {
//                        MainActivity.sFileOutputStream.write(packetData.getData());
//                        MainActivity.sFileOutputStream.write("\n".getBytes());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    int[] data = MainActivity.convertStringToIntArray(new String(packetData.getData()));

//                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packetData.getData());
//                    try {
//                        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
//                        Map<Integer, byte[]> answerMap = (Map<Integer, byte[]>) objectInputStream.readObject();
//                        for (Integer key : answerMap.keySet()) {
//                            Log.v("map", "receive key: " + key);
//                            synchronized (declaration.decVal) {
//                                System.arraycopy(answerMap.get(key),
//                                        0,
//                                        declaration.decVal,
//                                        declaration.messageSize[declaration.currentLayer] * key,
//                                        declaration.messageSize[declaration.currentLayer]);
//                            }
//                            synchronized (declaration.globalDecodedSymbolsRecord) {
//                                declaration.globalDecodedSymbolsRecord[key] = 1;
//                                declaration.selfDecodedSymbolsRecord[0][key] = 1;
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }

                    synchronized (declaration.globalDecodedSymbolsRecord) {
                        declaration.globalDecodedSymbolsRecord = data;
                    }


                    Map<Integer, byte[]> UMap = new HashMap<>();
                    Map<Integer, byte[]> RMap = new HashMap<>();
                    int maxRequestNumber = 1;
                    int UIndex = 0;
                    int RIndex = 0;
                    for (int i = 0; i < declaration.globalDecodedSymbolsRecord.length; i++) {
                        if (declaration.selfDecodedSymbolsRecord[0][i] != 0 && declaration.globalDecodedSymbolsRecord[i] == 0) {
                            System.out.print("update again: " + i + "\n");
                            UMap.put(i, Arrays.copyOfRange(declaration.decVal,
                                    declaration.messageSize[declaration.currentLayer] * i,
                                    declaration.messageSize[declaration.currentLayer] * (i + 1)));
                            UIndex++;
                        }
                        else if(declaration.selfDecodedSymbolsRecord[0][i] == 0 && declaration.globalDecodedSymbolsRecord[i] != 0){
                            System.out.print("request data: " + i + "\n");
                            RMap.put(i, null);
                            RIndex++;
                        }
                        if (UIndex == maxRequestNumber || i == declaration.globalDecodedSymbolsRecord.length - 1) {
                            if (UIndex > 0) {
                                System.out.print("Send Update Packet \n");

                                ByteArrayOutputStream byteArrayOutputStreamU = new ByteArrayOutputStream();
                                try {
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamU);
                                    objectOutputStream.writeObject(UMap);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                packetData = new PacketData("UPDATE_GLOBAL_DECVAL", byteArrayOutputStreamU.toByteArray());
                                packetData.setPosition(0);
                                packetData.setDes(MainActivity.mOwnerAddress);

                                for (Integer key : UMap.keySet()) {
                                    Log.v("LTD", "send key again: " + key);
                                    MainActivity.setLogText("send key again: " + key);
                                }
                                MainActivity.sendPacket(packetData);
                                declaration.waiting_time = System.currentTimeMillis();
                                UMap = new HashMap<>();
                                UIndex = 0;
                            }
                        }

                        if (RIndex == maxRequestNumber || i == declaration.globalDecodedSymbolsRecord.length - 1) {
                            if (RIndex > 0) {


                                System.out.print("Send Request Packet \n");
                                ByteArrayOutputStream byteArrayOutputStreamR = new ByteArrayOutputStream();
                                try {
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStreamR);
                                    objectOutputStream.writeObject(RMap);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                packetData = new PacketData("REQUEST_GLOBAL_DECVAL", byteArrayOutputStreamR.toByteArray());
                                packetData.setPosition(0);
                                packetData.setDes(MainActivity.mOwnerAddress);

                                for (Integer key : RMap.keySet()) {
                                    Log.v("LTD", "Request key: " + key);
                                    MainActivity.setLogText("Request key: " + key);
                                }
                                MainActivity.sendPacket(packetData);
                                declaration.waiting_time = System.currentTimeMillis();
                                RMap = new HashMap<>();
                                RIndex = 0;
                            }
                        }


                    }




//                    declaration.globalDecodedSymbolsRecord[packetData.getPosition()] = data;
//                    Log.v(TAG, "position = " + packetData.getPosition());
//                    Log.v(TAG, "before: " +declaration.isRecordUpdate[packetData.getPosition()]);
//                    declaration.isRecordUpdate[packetData.getPosition()] = true;
//                    Log.v(TAG, "after: " +declaration.isRecordUpdate[packetData.getPosition()]);

//                    synchronized (MainActivity.waitingLock) {
//                        Log.v(TAG, "unlock waiting");
////                        MainActivity.isWaiting = false;
//                        MainActivity.waitingLock.notify();
//                    }
                    break;
                case "ANSWER_GLOBAL_DECVAL":
                    if (!packetData.getDes().equals(MainActivity.mMacAddress)
                           /* || declaration.isDecvalUpdate[(packetData.getPosition() / declaration.messageSize[declaration.currentLayer])]*/) {
                        return;
                    }

                    //output = (System.currentTimeMillis() - MainActivity.sRequestDecvalTime) + "\n";


                    MainActivity.sRequestDecvalTotalTime = MainActivity.sRequestDecvalTotalTime + (System.currentTimeMillis() - MainActivity.sRequestDecvalTime);
                    MainActivity.num_RequestDecval = MainActivity.num_RequestDecval + 1;


                    Log.v(TAG, "receive answer global decval: ");// + packetData.getPosition());
//                    if ((new String(packetData.getData())).equals(String.valueOf(-1))) {
//                        synchronized (declaration.globalDecodedSymbolsRecord) {
//                            declaration.globalDecodedSymbolsRecord[packetData.getPosition() / declaration.messageSize[declaration.currentLayer]] = 0;
//                        }
//                        Log.v("recorderror", "it's in");
//                    } else {
//                        synchronized (declaration.decVal) {
//                            System.arraycopy(packetData.getData(), 0, declaration.decVal, packetData.getPosition(), declaration.messageSize[declaration.currentLayer]);
//                        }
//                    }
//                    declaration.isDecvalUpdate[(packetData.getPosition() / declaration.messageSize[declaration.currentLayer])] = true;
                    Map<Integer, byte[]>answerMap = new HashMap<>();
                    byteArrayInputStream = new ByteArrayInputStream(packetData.getData());
                    try {
                        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                        answerMap = (Map<Integer, byte[]>) objectInputStream.readObject();
                        for (Integer key : answerMap.keySet()) {
                            Log.v("map", "receive key: " + key);
                            synchronized (declaration.decVal) {
                                System.arraycopy(answerMap.get(key),
                                        0,
                                        declaration.decVal,
                                        declaration.messageSize[declaration.currentLayer] * key,
                                        declaration.messageSize[declaration.currentLayer]);
                            }
//                            synchronized (declaration.globalDecodedSymbolsRecord) {
//                                declaration.globalDecodedSymbolsRecord[key] = 1;
                            declaration.selfDecodedSymbolsRecord[0][key] = 1;
//                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    output = System.currentTimeMillis()+"\t"+type + ": Map Size = "+answerMap.size()+"\n";
                    try {
                        //MainActivity.sRequestDecvalDelayStream.write(output.getBytes());
                        MainActivity.sFileTimeStampRecord.write(output.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    byte[] answerValues = packetData.getData();
//                    for (int i = 0; i < LTDistributed_decoder.indices.length; i++) {
//                        System.arraycopy(answerValues,
//                                i * declaration.messageSize[declaration.currentLayer],
//                                declaration.decVal,
//                                LTDistributed_decoder.indices[i] * declaration.messageSize[declaration.currentLayer],
//                                declaration.messageSize[declaration.currentLayer]);
//                        declaration.selfDecodedSymbolsRecord[0][LTDistributed_decoder.indices[i]] = 1;
//                    }
//                    declaration.isDecvalUpdate[0] = true;
//
//                    synchronized (MainActivity.waitingLock) {
////                        MainActivity.isWaiting = false;
//                        MainActivity.waitingLock.notify();
//                    }
                    break;
                case "SUCESS_UPDATE_DECVAL":
                    if (!packetData.getDes().equals(MainActivity.mMacAddress)
                            || declaration.isGlobalDecvalUpdate[(packetData.getPosition() / declaration.messageSize[declaration.currentLayer])]) {
                        return;
                    }
                    // output = (System.currentTimeMillis() - MainActivity.sUpdateDecvalTime) + "\n";
                    int[] Newdata = MainActivity.convertStringToIntArray(new String(packetData.getData()));
                    synchronized (declaration.globalDecodedSymbolsRecord) {
                        declaration.globalDecodedSymbolsRecord = Newdata;
                    }
                    output = System.currentTimeMillis()+"\t"+type+"\n";

                    MainActivity.sUpdateDecvalTotalTime = MainActivity.sUpdateDecvalTotalTime + (System.currentTimeMillis() - MainActivity.sUpdateDecvalTime);
                    MainActivity.num_UpdateDecval = MainActivity.num_UpdateDecval + 1;


                    try {
                        // MainActivity.sUpdateDecvalDelayStream.write(output.getBytes());
                        MainActivity.sFileTimeStampRecord.write(output.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    declaration.isGlobalDecvalUpdate[0] = true;
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
