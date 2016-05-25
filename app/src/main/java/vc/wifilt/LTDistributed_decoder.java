package vc.wifilt;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by melissa on 2016/1/4.
 */
public class LTDistributed_decoder {

    private static int sPacketTimeout = 1000;
    static int maxRequestNumber = 1;
    static int[] indices = new int[maxRequestNumber];
    static int currentIndex = 0;

    public static void main(int eNum, int rStep, int layer, int node_ID) {
        int n = 0;
        //while (n < declaration.srcSymbols[declaration.currentLayer])
        //{
        while(declaration.globalFinish == 0) {
            Distributed_BP(n + 1, eNum, rStep + 1, layer, node_ID);
            n++;
        }
        PacketData packetData;
        // int sPacketTimeout = 1000;
        String output = "";
        if(declaration.UpdateMap.size() > 0){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try{
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(declaration.UpdateMap);
            }catch (IOException ex){
                ex.printStackTrace();
            }
            boolean Uloss = false;
            packetData = new PacketData("UPDATE_GLOBAL_DECVAL", byteArrayOutputStream.toByteArray());
            packetData.setPosition(0);
            packetData.setDes(MainActivity.mOwnerAddress);

            for (Integer key : declaration.UpdateMap.keySet()) {
                Log.v("LTD", "send key1: " + key);
//                MainActivity.setLogText("send key1: " + key);
            }
//            do {
//                MainActivity.sUpdateDecvalLoss++;
//                MainActivity.sUpdateDecvalTime = System.currentTimeMillis();
            declaration.waiting_time = System.currentTimeMillis();
            MainActivity.sendPacket(packetData);

//                if(Uloss == false){
//                    output = System.currentTimeMillis()+"\tUPDATE_GLOBAL_DECVAL : Map Size = "+ declaration.UpdateMap.size()+"\n";
//                    Uloss = true;
//                }
//                else{
//                    output=output+System.currentTimeMillis()+"\tloss \n";
//                }
//
//                synchronized (MainActivity.waitingLock) {
//                    try {
//                        MainActivity.waitingLock.wait(sPacketTimeout);
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            } while (!declaration.isGlobalDecvalUpdate[0]);
//            declaration.isGlobalDecvalUpdate[0] = false;
//            MainActivity.sUpdateDecvalLoss--;


//            try {
//                MainActivity.sFileTimeStampRecord.write(output.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            declaration.UpdateMap = new HashMap<>();
        }




        //   if (declaration.globalFinish == 1) {
        //       break;
        //   }
        //}
    }

    public static void Distributed_BP (int symbolNum, int dTime, int rStep, int layer, int node_ID )	{

        PacketData packetData;
        //printf("in Distributed_BP\n");
//        System.out.println("in Distributed_BP, symbolnum = "+ symbolNum);

        /*0127 UPDATE*/
//        declaration.requestcount ++;
        String output = "";// = "global request record count = " + declaration.requestcount + "\n";
//        System.out.print(output);
//        try {
//            MainActivity.sFileOutputStream.write(output.getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(declaration.selfdecoding==false && declaration.UpdateMap.size()==0) {
//            Map<Integer, byte[]> valueMap = new HashMap<>();
//            for (int i = 0; i < declaration.globalDecodedSymbolsRecord.length; i++) {
//                System.out.print(declaration.globalDecodedSymbolsRecord[i]);
//                if (declaration.globalDecodedSymbolsRecord[i] != 0) {
//                    valueMap.put(i, Arrays.copyOfRange(declaration.decVal,
//                            declaration.messageSize[declaration.currentLayer] * i,
//                            declaration.messageSize[declaration.currentLayer] * (i + 1)));
//                    currentIndex++;
//                    System.out.print("currentIndex =  "+currentIndex+"\n");
//                }
//                if (currentIndex == maxRequestNumber || i == declaration.globalDecodedSymbolsRecord.length-1) {
//                    System.out.print("Send Packet \n");
//                    if (currentIndex == 0) {
//                        break;
//                    }
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    try {
//                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
//                        objectOutputStream.writeObject(valueMap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    packetData = new PacketData("UPDATE_GLOBAL_DECVAL", byteArrayOutputStream.toByteArray());
//                    packetData.setPosition(0);
//                    packetData.setDes(MainActivity.mOwnerAddress);
//                    MainActivity.sendPacket(packetData);
//                    valueMap = new HashMap<>();
//                    currentIndex = 0;
//                }
//            }
//        }
//            boolean Rloss = false;
//            if (!MainActivity.sIsGroupOwner && declaration.isNeedRequest == true) {
//                packetData = new PacketData("REQUEST_GLOBAL_RECORD", String.valueOf(-1).getBytes());
//                    MainActivity.convertIntArrayToString(declaration.globalDecodedSymbolsRecord).getBytes());
//                packetData.setDes(MainActivity.mOwnerAddress);
//                        MainActivity.isWaiting = true;
//                do {
//                    MainActivity.sRequestRecordLoss++;
//                    MainActivity.sRequestRecordTime = System.currentTimeMillis();
//                output = "request: " + System.currentTimeMillis() + "\n";
//                try {
//                    MainActivity.sRequestRecordDelayStream.write(output.getBytes());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                    if (Rloss == false) {
//                        output = System.currentTimeMillis() + "\tREQUEST_GLOBAL_RECORD \n";
//                        Rloss = true;
//                    } else {
//                        output = output + System.currentTimeMillis() + "\tloss \n";
//                    }
//                    MainActivity.sendPacket(packetData);
//                Log.v("packet delay", "request: " + System.currentTimeMillis());
//                    Log.v("LTDistributed", "send request global record");
//                    synchronized (MainActivity.waitingLock) {
//                                                    while (MainActivity.isWaiting) {
//                        try {
//                            MainActivity.waitingLock.wait(sPacketTimeout);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                                                    }
//                    }
//                    Log.v("LTDistributed", "isRecordUpdate=" + declaration.isRecordUpdate[0]);
//            Log.v("LTDistributed", "position = " + (declaration.PData_requireSrc[node_ID][r][s]-1));
//                } while (!declaration.isRecordUpdate[0]);
//                declaration.isRecordUpdate[0] = false;
//                MainActivity.sRequestRecordLoss--;
//
//                try {
//                    MainActivity.sFileTimeStampRecord.write(output.getBytes());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

//            declaration.isNeedRequest = true; //  assume we don't get new source, so  need to request data from cache
//            Map<Integer, byte[]> valueMap = new HashMap<>();
//            for (int i = 0; i < declaration.globalDecodedSymbolsRecord.length; i++) {
//                System.out.print(declaration.globalDecodedSymbolsRecord[i]);
//                if (declaration.selfDecodedSymbolsRecord[0][i] == 0 && declaration.globalDecodedSymbolsRecord[i] != 0) {
//                    declaration.isNeedRequest = false;  // there are some new source, do not need to request from cache
//                    indices[currentIndex] = i;
//                    System.out.print("request "+i+"\n");
//                    valueMap.put(i, null);
//                    currentIndex++;
//                    System.out.print("currentIndex =  "+currentIndex+"\n");
//                }
//                if (currentIndex == maxRequestNumber || i == declaration.globalDecodedSymbolsRecord.length-1) {
//                    System.out.print("Send Request Packet \n");
//                    if (currentIndex == 0) {
//                        break;
//                    }
//                    if (i == declaration.globalDecodedSymbolsRecord.length) {
//                        indices = Arrays.copyOf(indices, currentIndex);
//                    }
//
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    try {
//                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
//                        objectOutputStream.writeObject(valueMap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    // packetData = new PacketData("REQUEST_GLOBAL_DECVAL", MainActivity.convertIntArrayToString(indices).getBytes());
//                    boolean Dloss = false;
//                    packetData = new PacketData("REQUEST_GLOBAL_DECVAL", byteArrayOutputStream.toByteArray());
//
//                    packetData.setDes(MainActivity.mOwnerAddress);
//                    do {
//                        MainActivity.sRequestDecvalLoss++;
//                        MainActivity.sRequestDecvalTime = System.currentTimeMillis();
//                        MainActivity.sendPacket(packetData);
//                        Log.v("LTD", "map size = " + valueMap.size());
//                        if (Dloss == false) {
//                            output = System.currentTimeMillis() + "\tREQUEST_GLOBAL_DECVAL\n";
//                            Dloss = true;
//                        } else {
//                            output = output + System.currentTimeMillis() + "\tloss \n";
//                        }
//
//                        synchronized (MainActivity.waitingLock) {
//                            try {
//                                MainActivity.waitingLock.wait(sPacketTimeout);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } while (!declaration.isDecvalUpdate[0]);
//                    declaration.isDecvalUpdate[0] = false;
//                    MainActivity.sRequestDecvalLoss--;
//
//
//                    try {
//                        MainActivity.sFileTimeStampRecord.write(output.getBytes());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    indices = new int[maxRequestNumber];
//                    valueMap = new HashMap<>();
//                    currentIndex = 0;
//                }
//            }
//
//        }

        //Map<Integer, byte[]> UpdateMap = new HashMap<>();

        declaration.selfdecoding = false; //assume it can't decode anymore after this step

        for(int r = 0 ; r < dTime ; r++)
        {
//            if(declaration.globalFinish == 1){
//                break;
//            }
            if(declaration.PData_currentDegree[node_ID][r] > 1)
            {

                byte[] tmp ;
                tmp= new byte [declaration.messageSize[declaration.currentLayer]];
                //System.out.print(r);
                //System.out.print("originalDegree = ");
                //System.out.println(declaration.PData_originalDegree[node_ID][r]);

                /*  0127 UPDATE  */
                int decodenumber = 0;
                for(int s = 0 ; s < declaration.PData_originalDegree[node_ID][r]; s++){
                    int index = declaration.PData_requireSrc[node_ID][r][s];
                    if(index != -1)
                        if(declaration.globalDecodedSymbolsRecord[index-1] != 0 ||declaration.selfDecodedSymbolsRecord[node_ID][index-1] != 0)
                            decodenumber++;
                }


                for(int s = 0 ; s < declaration.PData_originalDegree[node_ID][r]; s++)
                {
                    int decode = 0;
                    int index = declaration.PData_requireSrc[node_ID][r][s];
                    if(index != -1){

                        if(declaration.selfDecodedSymbolsRecord[node_ID][index-1] != 0){
                            declaration.LocalRead[node_ID]++;
                            decode = 1;
                        }
/*                        PacketData packetData = new PacketData("REQUEST_GLOBAL_RECORD", String.valueOf(declaration.PData_requireSrc[node_ID][r][s]-1).getBytes());
                        packetData.setDes(MainActivity.mOwnerAddress);
//                        MainActivity.isWaiting = true;
                        do {
                            MainActivity.sendPacket(packetData);
                            Log.v("LTDistributed", "send request global record: " + (declaration.PData_requireSrc[node_ID][r][s] - 1));
                            synchronized (MainActivity.waitingLock) {
    //                            while (MainActivity.isWaiting) {
                                try {
                                    MainActivity.waitingLock.wait(sPacketTimeout);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
    //                            }
                            }
                            Log.v("LTDistributed", "isRecordUpdate=" + declaration.isRecordUpdate[(declaration.PData_requireSrc[node_ID][r][s]-1)]);
                            Log.v("LTDistributed", "position = " + (declaration.PData_requireSrc[node_ID][r][s]-1));
                        } while (!declaration.isRecordUpdate[(declaration.PData_requireSrc[node_ID][r][s]-1)]);
                        declaration.isRecordUpdate[(declaration.PData_requireSrc[node_ID][r][s]-1)] = false;

                        Log.v("LTDistributed", "Request Record value = " + declaration.globalDecodedSymbolsRecord[declaration.PData_requireSrc[node_ID][r][s]-1]);*/
                        //if(declaration.globalDecodedSymbolsRecord[index-1] != 0)
                        //{
//                            if(declaration.selfDecodedSymbolsRecord[node_ID][index-1] == 0){
//
//                                if (!MainActivity.sIsGroupOwner) {
//                                    packetData = new PacketData("REQUEST_GLOBAL_DECVAL", String.valueOf(declaration.messageSize[declaration.currentLayer]).getBytes());
//                                    packetData.setPosition(((index - 1) * declaration.messageSize[declaration.currentLayer]));
//                                    packetData.setDes(MainActivity.mOwnerAddress);
////                            MainActivity.isWaiting = true;
//                                    do {
//                                        MainActivity.sRequestDecvalTime = System.currentTimeMillis();
////                                        output = "request: " + System.currentTimeMillis() + "\n";
////                                        try {
////                                            MainActivity.sRequestDecvalDelayStream.write(output.getBytes());
////                                        } catch (IOException e) {
////                                            e.printStackTrace();
////                                        }
//                                        MainActivity.sendPacket(packetData);
//                                        Log.v("LTDistributed", "send request global decval: " + (index - 1));
//                                        synchronized (MainActivity.waitingLock) {
//                                            //                                while (MainActivity.isWaiting) {
//                                            try {
//                                                MainActivity.waitingLock.wait(sPacketTimeout);
//                                            } catch (InterruptedException e) {
//                                                e.printStackTrace();
//                                            }
//                                            //                                }
//                                        }
//                                    }
//                                    while (!declaration.isDecvalUpdate[(index - 1)]);
//                                    Log.v("LTDistributed", "Request decval position = " + (index - 1));
//                                }
//
//                                declaration.GlobalRead[node_ID]++;
//                                declaration.selfDecodedSymbolsRecord[node_ID][index-1] =1;
//                            }
                        //   decode = 1;
                        //}
//                        else {
//                            if(declaration.selfDecodedSymbolsRecord[node_ID][declaration.PData_requireSrc[node_ID][r][s]-1] != 0){
//                                int position = ((declaration.PData_requireSrc[node_ID][r][s]-1) * declaration.messageSize[declaration.currentLayer]);
//                                packetData = new PacketData("UPDATE_GLOBAL_DECVAL", Arrays.copyOfRange(declaration.decVal, position, position + declaration.messageSize[declaration.currentLayer]));
//                                packetData.setPosition(position);
//                                MainActivity.sendPacket(packetData);
//                            }
//                        }
                        if(decode == 1){

                            //computationStep++;
//

                            System.arraycopy(declaration.decVal, ((index - 1) * declaration.messageSize[declaration.currentLayer]), tmp, 0, declaration.messageSize[declaration.currentLayer]);

                            // Decode XOR
                            for(int p = 0 ; p < declaration.messageSize[declaration.currentLayer] ; p++){
                                declaration.PData_codedData[node_ID][r][p] = (byte) (declaration.PData_codedData[node_ID][r][p] ^ tmp[p]);
                            }


//                            System.out.println("Decode XOR done \n");
                            declaration.PData_currentDegree[node_ID][r]--;
//                            System.out.print(r);
                            //System.out.print("current degree = ");
                            //System.out.println(declaration.PData_currentDegree[node_ID][r]);

//                            index = -1;
                            declaration.PData_requireSrc[node_ID][r][s] = -1;

//                            System.out.print(r);
//                            System.out.print("current degree = ");
//                            System.out.println(declaration.PData_currentDegree[node_ID][r]);

//                            output = r + " current degree = " + declaration.PData_currentDegree[node_ID][r] + "\n index = " + (index - 1) + "\n";
//                            System.out.print(output);
/*                            try {
                                MainActivity.sFileOutputStream.write(output.getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/

                            /*  0127 UPDATE  */
                            decodenumber--;
                            if(decodenumber==0 || declaration.PData_currentDegree[node_ID][r] == 1)
                                break;
                        }

                    }

                }

                if(declaration.PData_currentDegree[node_ID][r] == 1)
                {
                    declaration.selfdecoding = true;   //  new degree1 source decoded, decoding by the new source
                    //System.out.print("r");
                    //System.out.print(r);
                    //System.out.println("OVER!");

                    //printf("currentDegree == 1 when r = %d\n",r);
                    for(int e = 0 ; e < declaration.PData_originalDegree[node_ID][r] ; e++)
                    {
                        int index = declaration.PData_requireSrc[node_ID][r][e];
                        if(index != -1 && declaration.selfDecodedSymbolsRecord[node_ID][index - 1]==0)//declaration.decRecord[node_ID][index - 1] == 0)
                        {
                            //wait_start[node_ID] = clock();
                            //pthread_mutex_lock(&mutex);

                            declaration.Broadcast[node_ID]++;
                            declaration.selfDecodedSymbolsRecord[node_ID][index - 1] = 1;

//                            int GjoinNode= declaration.nodeNum;
//                            for(int ii=0; ii<GjoinNode; ii++){
//                                if(declaration.selfDecodedSymbolsRecord[ii][index - 1] == 0){
//                                    declaration.RippleSize[ii]++;
//                                    //declaration.Receive[node_ID]++;
//                                }
//                            }
                            declaration.decRecord[node_ID][index - 1] = rStep;
                            declaration.LocalWrite[node_ID]++;
                            declaration.GlobalTryWrite[node_ID]++;
/*                            PacketData packetData = new PacketData("REQUEST_GLOBAL_RECORD", String.valueOf(index - 1).getBytes());
                            packetData.setDes(MainActivity.mOwnerAddress);
//                            MainActivity.isWaiting = true;
                            do {
                                MainActivity.sendPacket(packetData);
                                Log.v("LTDistributed", "send request global record: " + (index - 1));
                            synchronized (MainActivity.waitingLock) {
//                                while (MainActivity.isWaiting) {
                                try {
                                    MainActivity.waitingLock.wait(sPacketTimeout);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
//                                }
                            }
                            } while (!declaration.isRecordUpdate[(index - 1)]);
                            declaration.isRecordUpdate[index-1] = false;

                            Log.v("LTDistributed", "Request Record value = " + declaration.globalDecodedSymbolsRecord[index-1]);*/
                            if(declaration.globalDecodedSymbolsRecord[index - 1] == 0) {
                                declaration.UpdateMap.put(index-1, declaration.PData_codedData[node_ID][r]);
                                if(declaration.UpdateMap.size() >= maxRequestNumber){
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    try{
                                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                                        objectOutputStream.writeObject(declaration.UpdateMap);
                                    }catch (IOException ex){
                                        ex.printStackTrace();
                                    }

                                    boolean Uloss = false;
                                    packetData = new PacketData("UPDATE_GLOBAL_DECVAL", byteArrayOutputStream.toByteArray());
                                    packetData.setPosition(0);
                                    packetData.setDes(MainActivity.mOwnerAddress);
//                                    do {
//                                        MainActivity.sUpdateDecvalLoss++;
//                                        MainActivity.sUpdateDecvalTime = System.currentTimeMillis();

                                    for (Integer key : declaration.UpdateMap.keySet()) {
                                        Log.v("LTD", "send key2: " + key);
//                                        MainActivity.setLogText("send key2: " + key);
                                    }
                                    MainActivity.sendPacket(packetData);
                                    declaration.waiting_time = System.currentTimeMillis();
//                                        if(Uloss == false){
//                                            output = System.currentTimeMillis()+"\tUPDATE_GLOBAL_DECVAL : Map Size = "+ declaration.UpdateMap.size()+ "\n";
//                                            Uloss = true;
//                                        }
//                                        else{
//                                            output=output+System.currentTimeMillis()+"\tloss \n";
//                                        }
//                                        synchronized (MainActivity.waitingLock) {
//                                            try {
//                                                MainActivity.waitingLock.wait(sPacketTimeout);
//                                            } catch (InterruptedException e1) {
//                                                e1.printStackTrace();
//                                            }
//                                        }
//                                    } while (!declaration.isGlobalDecvalUpdate[0]);
//                                    declaration.isGlobalDecvalUpdate[0] = false;
//                                    MainActivity.sUpdateDecvalLoss--;
//
//                                    try {
//                                        MainActivity.sFileTimeStampRecord.write(output.getBytes());
//                                    } catch (IOException ex) {
//                                        ex.printStackTrace();
//                                    }

                                    declaration.UpdateMap = new HashMap<>();
                                }



                            }
                            synchronized (declaration.decVal) {
                                System.arraycopy(declaration.PData_codedData[node_ID][r], 0, declaration.decVal, ((index - 1) * declaration.messageSize[declaration.currentLayer]), declaration.messageSize[declaration.currentLayer]);
                            }
                            declaration.GlobalWrite[node_ID]++;

//                            else {
//                                if (declaration.selfDecodedSymbolsRecord[node_ID][index - 1] != 0) {
//                                    int position = ((index - 1) * declaration.messageSize[declaration.currentLayer]);
//                                    packetData = new PacketData("UPDATE_GLOBAL_DECVAL", Arrays.copyOfRange(declaration.decVal, position, position + declaration.messageSize[declaration.currentLayer]));
//                                    packetData.setPosition(position);
//                                    MainActivity.sendPacket(packetData);
//                                }
//                            }
/*                            output = "index = " + (index - 1) + "is done\n";
                            try {
                                MainActivity.sFileOutputStream.write(output.getBytes());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }*/

                            synchronized (declaration.globalDecodedSymbolsRecord) {
                                declaration.globalDecodedSymbolsRecord[index - 1] = rStep;
                            }
                            declaration.sDecodedSymbols[node_ID]++;
//                            packetData = new PacketData("UPDATE_GLOBAL_RECORD", String.valueOf(rStep).getBytes());
//                            packetData.setPosition(index - 1);
//                            MainActivity.sendPacket(packetData);

                            //sendRecordCount++;

                            break;
                        }
                    }
                }

            }
        }
        if(declaration.UpdateMap.size() > 0 && declaration.selfdecoding == false){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try{
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(declaration.UpdateMap);
            }catch (IOException ex){
                ex.printStackTrace();
            }
            boolean Uloss = false;
            packetData = new PacketData("UPDATE_GLOBAL_DECVAL", byteArrayOutputStream.toByteArray());
            packetData.setPosition(0);
            packetData.setDes(MainActivity.mOwnerAddress);

            for (Integer key : declaration.UpdateMap.keySet()) {
                Log.v("LTD", "send key3: " + key);
//                MainActivity.setLogText("send key3: " + key);
            }
//            do {
//                MainActivity.sUpdateDecvalLoss++;
//                MainActivity.sUpdateDecvalTime = System.currentTimeMillis();

            declaration.waiting_time = System.currentTimeMillis();
            MainActivity.sendPacket(packetData);
//
//                if(Uloss == false){
//                    output = System.currentTimeMillis()+"\tUPDATE_GLOBAL_DECVAL : Map Size = "+ declaration.UpdateMap.size()+"\n";
//                    Uloss = true;
//                }
//                else{
//                    output=output+System.currentTimeMillis()+"\tloss \n";
//                }
//
//                synchronized (MainActivity.waitingLock) {
//                    try {
//                        MainActivity.waitingLock.wait(sPacketTimeout);
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            } while (!declaration.isGlobalDecvalUpdate[0]);
//            declaration.isGlobalDecvalUpdate[0] = false;
//            MainActivity.sUpdateDecvalLoss--;
//
//
//            try {
//                MainActivity.sFileTimeStampRecord.write(output.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            declaration.UpdateMap = new HashMap<>();
        }
    }
}
