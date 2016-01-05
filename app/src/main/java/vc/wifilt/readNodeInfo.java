package vc.wifilt;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by melissa on 2016/1/4.
 */
public class readNodeInfo {
    public static void main(int node_ID, Context myContext) throws IOException{
        String s;
        s = Integer.toString(node_ID + 1);
        String NodeInfo = declaration.Storage_Directory + s + "/" + declaration.NodeInfo_FileName + declaration.NodeInfo_extension;
        String NodeTemp = convertStreamToString(myContext.getAssets().open(NodeInfo));
        System.out.println(NodeTemp);
        //String NodeTemp = readFile(NodeInfo);
        String[] AfterSplit = NodeTemp.split("_");

        declaration.SVC_LayerNum = Integer.valueOf(AfterSplit[1]).intValue();

        declaration.EncPacketNum = new int[declaration.SVC_LayerNum];
        declaration.messageSize = new int[declaration.SVC_LayerNum];
        declaration.srcSymbols = new int[declaration.SVC_LayerNum];
        declaration.mPaddingSize = new int[declaration.SVC_LayerNum];
        declaration.EncPacketNum[declaration.layer] = Integer.valueOf(AfterSplit[2]).intValue();

        //declaration.layer++;
    }

    /*public static String readFile(String fileName) throws IOException {
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
    }*/
    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
