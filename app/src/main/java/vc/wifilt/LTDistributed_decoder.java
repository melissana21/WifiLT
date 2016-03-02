package vc.wifilt;


import android.util.Log;

import java.io.IOException;

/**
 * Created by melissa on 2016/1/4.
 */
public class LTDistributed_decoder {
    private static int sPacketTimeout = 1000;

    public static void main(int eNum, int rStep, int layer, int node_ID) {
        int n = 0;
        while (n < declaration.srcSymbols[declaration.currentLayer])
        {
            Distributed_BP(n + 1, eNum, rStep + 1, layer, node_ID);
            n++;
            if (declaration.globalFinish == 1) {
                break;
            }
        }
    }

    public static void Distributed_BP (int symbolNum, int dTime, int rStep, int layer, int node_ID )	{
        PacketData packetData;
        //printf("in Distributed_BP\n");
//        System.out.println("in Distributed_BP, symbolnum = "+ symbolNum);

        /*0127 UPDATE*/
        declaration.requestcount ++;
        String output = "global request record count = " + declaration.requestcount + "\n";
//        System.out.print(output);
//        try {
//            MainActivity.sFileOutputStream.write(output.getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if (!MainActivity.sIsGroupOwner) {
            packetData = new PacketData("REQUEST_GLOBAL_RECORD",
                    MainActivity.convertIntArrayToString(declaration.globalDecodedSymbolsRecord).getBytes());
            packetData.setDes(MainActivity.mOwnerAddress);
//                        MainActivity.isWaiting = true;
            do {
                MainActivity.sRequestRecordTime = System.currentTimeMillis();
//                output = "request: " + System.currentTimeMillis() + "\n";
//                try {
//                    MainActivity.sRequestRecordDelayStream.write(output.getBytes());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                MainActivity.sendPacket(packetData);
//                Log.v("packet delay", "request: " + System.currentTimeMillis());
                Log.v("LTDistributed", "send request global record");
                synchronized (MainActivity.waitingLock) {
                    //                            while (MainActivity.isWaiting) {
                    try {
                        MainActivity.waitingLock.wait(sPacketTimeout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //                            }
                }
                Log.v("LTDistributed", "isRecordUpdate=" + declaration.isRecordUpdate[0]);
//            Log.v("LTDistributed", "position = " + (declaration.PData_requireSrc[node_ID][r][s]-1));
            } while (!declaration.isRecordUpdate[0]);
            declaration.isRecordUpdate[0] = false;
        }
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
                        if(declaration.globalDecodedSymbolsRecord[index-1] != 0)
                        {
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
                            decode = 1;
                        }
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

                            output = r + " current degree = " + declaration.PData_currentDegree[node_ID][r] + "\n index = " + (index - 1) + "\n";
                            System.out.print(output);
                            try {
                                MainActivity.sFileOutputStream.write(output.getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            /*  0127 UPDATE  */
                            decodenumber--;
                            if(decodenumber==0 || declaration.PData_currentDegree[node_ID][r] == 1)
                                break;
                        }

                    }

                }

                if(declaration.PData_currentDegree[node_ID][r] == 1)
                {
                    //System.out.print("r");
                    //System.out.print(r);
                    //System.out.println("OVER!");

                    //printf("currentDegree == 1 when r = %d\n",r);
                    for(int e = 0 ; e < declaration.PData_originalDegree[node_ID][r] ; e++)
                    {
                        int index = declaration.PData_requireSrc[node_ID][r][e];
                        if(index != -1 && declaration.decRecord[node_ID][index - 1] == 0)
                        {
                            //wait_start[node_ID] = clock();
                            //pthread_mutex_lock(&mutex);

                            declaration.Broadcast[node_ID]++;
                            declaration.selfDecodedSymbolsRecord[node_ID][index - 1] = 1;

                            int GjoinNode= declaration.nodeNum;
                            for(int ii=0; ii<GjoinNode; ii++){
                                if(declaration.selfDecodedSymbolsRecord[ii][index - 1] == 0){
                                    declaration.RippleSize[ii]++;
                                    //declaration.Receive[node_ID]++;
                                }
                            }
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
                                if (!MainActivity.sIsGroupOwner) {
                                    packetData = new PacketData("UPDATE_GLOBAL_DECVAL", declaration.PData_codedData[node_ID][r]);
                                    packetData.setPosition(((index - 1) * declaration.messageSize[declaration.currentLayer]));
                                    packetData.setDes(MainActivity.mOwnerAddress);
                                    do {
                                        MainActivity.sUpdateDecvalTime = System.currentTimeMillis();
//                                        output = "request: " + System.currentTimeMillis() + "\n";
//                                        try {
//                                            MainActivity.sUpdateDecvalDelayStream.write(output.getBytes());
//                                        } catch (IOException e1) {
//                                            e1.printStackTrace();
//                                        }
                                        MainActivity.sendPacket(packetData);
                                        synchronized (MainActivity.waitingLock) {
                                            try {
                                                MainActivity.waitingLock.wait(sPacketTimeout);
                                            } catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    } while (!declaration.isGlobalDecvalUpdate[(index - 1)]);
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
                            output = "index = " + (index - 1) + "is done\n";
                            try {
                                MainActivity.sFileOutputStream.write(output.getBytes());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

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

    }

}
