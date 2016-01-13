package vc.wifilt;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.*;

/**
 * Created by melissa on 2016/1/4.
 */
public class readEncodedData {
    public static int main(int PacketNum, int layer, int node_ID, Context myContext) throws IOException {



        int decTime = 0;
        int terminate = PacketNum;
        System.out.println(terminate);
        int chooseDecPacketNum = 0;
        String s;
        s = Integer.toString(node_ID + 1);
        while (terminate > 0) {
            String index_1 = Integer.toString(chooseDecPacketNum);
            String index_3 = Integer.toString(layer);

            String HeaderInfo = myContext.getExternalFilesDir(null).getAbsolutePath() + declaration.Storage_Directory + s + declaration.Encoded_FileName + index_3 + declaration.Header_FileName + index_1 + declaration.Encoded_Extension;
            String Data = myContext.getExternalFilesDir(null).getAbsolutePath() + declaration.Storage_Directory + s + declaration.Encoded_FileName + index_3 + declaration.Data_FileName + index_1 + declaration.Encoded_Extension;
            //File file = new File(HeaderInfo);


            chooseDecPacketNum++;

            //if(file.exists()){
            try{
                FileInputStream fi=new FileInputStream(Data);
                byte decTemp[]=new byte[declaration.messageSize[declaration.currentLayer]];
                fi.read(decTemp);


                //      System.out.print("HeaderInfo : ");
                //      System.out.println(HeaderInfo);
                //      System.out.print("Data : ");
                //      System.out.println(Data);

                //String headTemp = convertStreamToString(myContext.getAssets().open(HeaderInfo));
                //InputStream fi =myContext.getAssets().open(Data);
                fi.read(decTemp);
//                System.out.println(decTemp);

                String headTemp = readFile(HeaderInfo);
                String[] AfterSplit = headTemp.split("_");
                int fNID = Integer.valueOf(AfterSplit[0]).intValue();
                declaration.messageSize[declaration.currentLayer] = Integer.valueOf(AfterSplit[1]).intValue();
                declaration.srcSymbols[declaration.currentLayer] = Integer.valueOf(AfterSplit[2]).intValue();
                int mPaddingSize = Integer.valueOf(AfterSplit[3]).intValue();
                int d = Integer.valueOf(AfterSplit[4]).intValue();
                //System.out.print("messagesize : ");
                //System.out.println(declaration.messageSize);
                //System.out.print("messageSize : ");
                //System.out.println(declaration.messageSize);
                //System.out.print("srcSymbols ");
                //System.out.println(declaration.srcSymbols);
                //System.out.print("mPaddingSize ");
                //System.out.println(declaration.mPaddingSize);



                if(d ==1){
                    int index=Integer.valueOf(AfterSplit[5]).intValue();
                    if (declaration.decRecord[node_ID][index-1] ==0){

                        declaration.PData_currentDegree[node_ID][decTime] =d;
                        declaration.PData_originalDegree[node_ID][decTime] = d;
                        declaration.PData_seed[node_ID][decTime] = 0;
                        declaration.PData_requireSrc[node_ID][decTime] = new int[d];
                        declaration.PData_requireSrc[node_ID][decTime][0] =  Integer.valueOf(AfterSplit[5]).intValue();
                        declaration.PData_codedData[node_ID][decTime] = decTemp;

                        declaration.Broadcast[node_ID]++;
                        declaration.selfDecodedSymbolsRecord[node_ID][index - 1] = 1;
                        declaration.decRecord[node_ID][index - 1] = 1;

/*                        int GjoinNode= declaration.nodeNum;
                        for(int ii=0; ii<GjoinNode; ii++){
                            if(declaration.selfDecodedSymbolsRecord[ii][index - 1] == 0){
                                declaration.RippleSize[ii]++;
                            }
                        }
*/
                        declaration.GlobalTryWrite[node_ID]++;
                        if(declaration.globalDecodedSymbolsRecord[index - 1] == 0){
                            System.arraycopy(decTemp,0,declaration.decVal,((index - 1) * declaration.messageSize[declaration.currentLayer]),declaration.messageSize[declaration.currentLayer]);
                            PacketData packetData = new PacketData("UPDATE_GLOBAL_DECVAL", decTemp);
                            packetData.setPosition(((index - 1) * declaration.messageSize[declaration.currentLayer]));
                            packetData.setDes(MainActivity.mOwnerAddress);
                            MainActivity.sendPacket(packetData);
                            //System.out.println(declaration.decVal);
                            //(declaration.decVal + ((index - 1) * declaration.messageSize)), decTemp, declaration.messageSize);
                            declaration.GlobalWrite[node_ID]++;
                        }


                        declaration.globalDecodedSymbolsRecord[index - 1] = 1;
                        PacketData packetData = new PacketData("UPDATE_GLOBAL_RECORD", String.valueOf(1).getBytes());
                        packetData.setPosition(index - 1);
                        packetData.setDes(MainActivity.mOwnerAddress);
                        Log.v("Enc","position : " + (index-1) );
                        MainActivity.sendPacket(packetData);
                        System.out.print(index);
                        System.out.println(" is degree1 !");
                        declaration.sDecodedSymbols[node_ID]++;
                        //sendRecordCount++;
                        decTime++;
//                        while (true) {}
                    }

                }
                else if(d > 1){

                    declaration.PData_currentDegree[node_ID][decTime] =d;
                    declaration.PData_originalDegree[node_ID][decTime] = d;
                    declaration.PData_seed[node_ID][decTime] = 0;
                    declaration.PData_requireSrc[node_ID][decTime] = new int[d];

                    declaration.PData_codedData[node_ID][decTime] = decTemp;


                    //System.out.println(declaration.PData[0][0].currentDegree);
                    int pos=0;
                    for(int i = 5 ; i < 5+d ; i++){
                        declaration.PData_requireSrc[node_ID][decTime][pos++] = Integer.valueOf(AfterSplit[i]).intValue();

                    }

                    decTime++;
                }
               fi.close();
            }catch (IOException e) {
//                e.printStackTrace();
            }
            terminate--;
            //System.out.print("dectime = ");
            //System.out.println(decTime);

            //System.out.print("currentDegree = ");
            //System.out.println(declaration.PData_currentDegree[node_ID][decTime-1]);

        }

        System.out.print("dectime = ");
        System.out.println(decTime);


        return decTime;
    }


    public static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    /*public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }*/
}
