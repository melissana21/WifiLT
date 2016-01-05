package vc.wifilt;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;




/**
 * Created by melissa on 2016/1/4.
 */
public class readHeaderInfo {
    public static void main(int layer, int node_ID, Context myContext) throws IOException{

        int n = 0;
        //int terminate = PacketNum;
        while(true){
            String index3 = Integer.toString(n);
            String index2 = Integer.toString(layer);
            String s;
            s = Integer.toString(node_ID + 1);
           //String HeaderInfo = declaration.Storage_Directory + s + declaration.Encoded_FileName + index2+ declaration.Header_FileName + index3 + declaration.Encoded_Extension;

            String HeaderInfo = declaration.Storage_Directory + s + declaration.Encoded_FileName + index2+ declaration.Header_FileName + index3 + declaration.Encoded_Extension;

            //File file = new File(HeaderInfo);

            //if(file.exists()){
            //    String headTemp = readFile(HeaderInfo);
            try{
                String headTemp = convertStreamToString(myContext.getAssets().open(HeaderInfo));
                System.out.println(HeaderInfo);
                System.out.println(headTemp);
                String[] AfterSplit = headTemp.split("_");
                int nid = Integer.valueOf(AfterSplit[0]).intValue();
                declaration.messageSize[layer] = Integer.valueOf(AfterSplit[1]).intValue();
                declaration.srcSymbols[layer] = Integer.valueOf(AfterSplit[2]).intValue();
                declaration.mPaddingSize[layer] = Integer.valueOf(AfterSplit[3]).intValue();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }


             //   break;
            //}

            //    terminate--;
            n++;

        }

    }

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


   /* public static String readFile(String fileName) throws IOException {
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
}
