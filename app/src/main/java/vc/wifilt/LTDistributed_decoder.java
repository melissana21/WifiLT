package vc.wifilt;


import android.util.Log;

import java.util.Arrays;

/**
 * Created by melissa on 2016/1/4.
 */
public class LTDistributed_decoder {
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
        //printf("in Distributed_BP\n");
//        System.out.println("in Distributed_BP, symbolnum = "+ symbolNum);

        for(int r = 0 ; r < dTime ; r++)
        {


            if(declaration.globalFinish == 1){
                break;
            }
            if(declaration.PData_currentDegree[node_ID][r] > 1)
            {

                byte[] tmp ;
                tmp= new byte [declaration.messageSize[declaration.currentLayer]];
                //System.out.print(r);
                //System.out.print("originalDegree = ");
                //System.out.println(declaration.PData_originalDegree[node_ID][r]);
                for(int s = 0 ; s < declaration.PData_originalDegree[node_ID][r]; s++)
                {
                    int decode = 0;
                    if(declaration.PData_requireSrc[node_ID][r][s] != -1){

                        if(declaration.selfDecodedSymbolsRecord[node_ID][declaration.PData_requireSrc[node_ID][r][s]-1] != 0){
                            declaration.LocalRead[node_ID]++;
                            decode = 1;
                        }
                        PacketData packetData = new PacketData("REQUEST_GLOBAL_RECORD", String.valueOf(declaration.PData_requireSrc[node_ID][r][s]-1).getBytes());
                        MainActivity.isWaiting = true;
                        do {
                            MainActivity.sendPacket(packetData);
                            Log.v("LTDistributed", "send request global record: " + (declaration.PData_requireSrc[node_ID][r][s] - 1));
                        synchronized (MainActivity.waitingLock) {
//                            while (MainActivity.isWaiting) {
                            try {
                                MainActivity.waitingLock.wait(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                            }
                        }
                        } while (MainActivity.isWaiting);
                        Log.v("LTDistributed", "Request Record value = " + declaration.globalDecodedSymbolsRecord[declaration.PData_requireSrc[node_ID][r][s]-1]);
                        if(declaration.globalDecodedSymbolsRecord[declaration.PData_requireSrc[node_ID][r][s]-1] != 0)
                        {
                            if(declaration.selfDecodedSymbolsRecord[node_ID][declaration.PData_requireSrc[node_ID][r][s]-1] == 0){
                                declaration.GlobalRead[node_ID]++;
                                declaration.selfDecodedSymbolsRecord[node_ID][declaration.PData_requireSrc[node_ID][r][s]-1] =1;
                            }
                            decode = 1;
                        }
                        else {
                            if(declaration.selfDecodedSymbolsRecord[node_ID][declaration.PData_requireSrc[node_ID][r][s]-1] != 0){
                                int position = ((declaration.PData_requireSrc[node_ID][r][s]-1) * declaration.messageSize[declaration.currentLayer]);
                                packetData = new PacketData("UPDATE_GLOBAL_DECVAL", Arrays.copyOfRange(declaration.decVal, position, position + declaration.messageSize[declaration.currentLayer]));
                                packetData.setPosition(position);
                                MainActivity.sendPacket(packetData);
                            }
                        }
                        if(decode == 1){

                            //computationStep++;
//
                            packetData = new PacketData("REQUEST_GLOBAL_DECVAL", String.valueOf(declaration.messageSize[declaration.currentLayer]).getBytes());
                            packetData.setPosition(((declaration.PData_requireSrc[node_ID][r][s]-1) * declaration.messageSize[declaration.currentLayer]));
                            MainActivity.isWaiting = true;
                            do {
                                MainActivity.sendPacket(packetData);
                                Log.v("LTDistributed", "send request global decval: " + (declaration.PData_requireSrc[node_ID][r][s] - 1));
                            synchronized (MainActivity.waitingLock) {
//                                while (MainActivity.isWaiting) {
                                try {
                                    MainActivity.waitingLock.wait(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
//                                }
                            }
                            } while (MainActivity.isWaiting);
                            Log.v("LTDistributed", "Request decval position = " + (declaration.PData_requireSrc[node_ID][r][s]-1));
                            System.arraycopy(declaration.decVal,((declaration.PData_requireSrc[node_ID][r][s]-1) * declaration.messageSize[declaration.currentLayer]),tmp,0,declaration.messageSize[declaration.currentLayer]);

                            // Decode XOR
                            for(int p = 0 ; p < declaration.messageSize[declaration.currentLayer] ; p++){
                                declaration.PData_codedData[node_ID][r][p] = (byte) (declaration.PData_codedData[node_ID][r][p] ^ tmp[p]);
                            }


//                            System.out.println("Decode XOR done \n");
                            declaration.PData_currentDegree[node_ID][r]--;
//                            System.out.print(r);
                            //System.out.print("current degree = ");
                            //System.out.println(declaration.PData_currentDegree[node_ID][r]);

                            declaration.PData_requireSrc[node_ID][r][s] = -1;

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
                            PacketData packetData = new PacketData("REQUEST_GLOBAL_RECORD", String.valueOf(index - 1).getBytes());
                            MainActivity.isWaiting = true;
                            do {
                                MainActivity.sendPacket(packetData);
                                Log.v("LTDistributed", "send request global record: " + (index - 1));
                            synchronized (MainActivity.waitingLock) {
//                                while (MainActivity.isWaiting) {
                                try {
                                    MainActivity.waitingLock.wait(5000);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
//                                }
                            }
                            } while (MainActivity.isWaiting);
                            Log.v("LTDistributed", "Request Record value = " + declaration.globalDecodedSymbolsRecord[index-1]);
                            if(declaration.globalDecodedSymbolsRecord[index - 1] == 0){
                                packetData = new PacketData("UPDATE_GLOBAL_DECVAL", declaration.PData_codedData[node_ID][r]);
                                packetData.setPosition(((index - 1) * declaration.messageSize[declaration.currentLayer]));
                                MainActivity.sendPacket(packetData);
                                System.arraycopy(declaration.PData_codedData[node_ID][r],0,declaration.decVal,((index - 1) * declaration.messageSize[declaration.currentLayer]),declaration.messageSize[declaration.currentLayer]);
                                declaration.GlobalWrite[node_ID]++;
                            }
                            else {
                                if (declaration.selfDecodedSymbolsRecord[node_ID][index - 1] != 0) {
                                    int position = ((index - 1) * declaration.messageSize[declaration.currentLayer]);
                                    packetData = new PacketData("UPDATE_GLOBAL_DECVAL", Arrays.copyOfRange(declaration.decVal, position, position + declaration.messageSize[declaration.currentLayer]));
                                    packetData.setPosition(position);
                                    MainActivity.sendPacket(packetData);
                                }
                            }

                            declaration.globalDecodedSymbolsRecord[index - 1] = rStep;
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
