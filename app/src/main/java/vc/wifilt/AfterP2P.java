package vc.wifilt;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by melissa on 2016/1/6.
 */
public class AfterP2P {
    protected static void main(int node_ID){
        System.out.println("Start !!");
       // for(int node_ID=0; node_ID<declaration.nodeNum; node_ID++){
       //     int flag_first = 1;
            //int node_ID=0;
            declaration.currentLayer=0;
            try {
                readNodeInfo.main(node_ID,MainActivity.MainContext);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                readHeaderInfo.main(declaration.currentLayer, node_ID,MainActivity.MainContext);
            } catch (IOException e) {
                e.printStackTrace();
            }



       // }
        declaration.decVal = new byte[declaration.messageSize[declaration.currentLayer]*declaration.srcSymbols[declaration.currentLayer]];

        System.out.println(declaration.decVal.length);
        System.out.print("messageSize : ");
        System.out.println(declaration.messageSize[declaration.currentLayer]);
        System.out.print("srcSymbols ");
        System.out.println(declaration.srcSymbols[declaration.currentLayer]);
        System.out.print("mPaddingSize ");
        System.out.println(declaration.mPaddingSize[declaration.currentLayer]);

        declaration.globalDecodedSymbolsRecord= new int[declaration.srcSymbols[declaration.currentLayer]]; //init=0
        declaration.decRecord = new int[declaration.nodeNum][declaration.srcSymbols[declaration.currentLayer]];//init=0
        declaration.selfDecodedSymbolsRecord = new int[declaration.nodeNum][declaration.srcSymbols[declaration.currentLayer]];//init=0


        declaration.PData_currentDegree= new int[declaration.nodeNum][declaration.EncPacketNum[declaration.currentLayer]]; //宣告二維陣列
        declaration.PData_originalDegree= new int[declaration.nodeNum][declaration.EncPacketNum[declaration.currentLayer]]; //宣告二維陣列
        declaration.PData_requireSrc= new int[declaration.nodeNum][declaration.EncPacketNum[declaration.currentLayer]][]; //宣告二維陣列
        declaration.PData_codedData= new byte [declaration.nodeNum][declaration.EncPacketNum[declaration.currentLayer]][]; //宣告二維陣列
        declaration.PData_seed= new int[declaration.nodeNum][declaration.EncPacketNum[declaration.currentLayer]]; //宣告二維陣列
        declaration.decode_num= new int [declaration.nodeNum][declaration.srcSymbols[declaration.currentLayer]];
        java.util.Arrays.fill(declaration.rippleStep,1); //  init=1



     //   for(int node_ID=0; node_ID<declaration.nodeNum; node_ID++){
            try {
                declaration.elementNum[node_ID] =readEncodedData.main(declaration.EncPacketNum[declaration.currentLayer],declaration.currentLayer, node_ID,MainActivity.MainContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
     //   }


        //for(int node_ID=0; node_ID<declaration.nodeNum; node_ID++){
        PrintHello.set(node_ID);
        Thread t1 = new PrintHello(); // 產生Thread物件
        t1.start(); // 開始執行t.run()
        //}

        Thread t2 = new FinishLayer(); // 產生Thread物件
        t2.start(); // 開始執行t.run()
        try {
            t2.join();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block

            //		t2.interrupt();
            //		t1.interrupt();
        }

        String index = Integer.toString(declaration.currentLayer);
        String Outputfilename=declaration.Output_FileName+index+declaration.Output_Extension;
        System.out.println(Outputfilename);
        //System.out.println(declaration.rippleStep[0]);
        //System.out.println(declaration.rippleStep[1]);
        System.out.println(declaration.decVal);


        try{
            //Byte result[]= new Byte[declaration.messageSize[declaration.currentLayer] * declaration.srcSymbols[declaration.currentLayer] - declaration.mPaddingSize[declaration.currentLayer]];
            FileOutputStream fo=new FileOutputStream(Outputfilename);


            for(int u = 0 ; u < (declaration.messageSize[declaration.currentLayer] * declaration.srcSymbols[declaration.currentLayer]) - declaration.mPaddingSize[declaration.currentLayer] ; u++)
            {
                fo.write(declaration.decVal[u]);
            }


            fo.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
