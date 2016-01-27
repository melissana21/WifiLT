package vc.wifilt;

import android.os.Environment;

/**
 * Created by melissa on 2016/1/4.
 */
public class declaration {
    static int nodeNum = 2;
    static int globalFinish = 0;
    static int layer = 0;
    static int[] EncPacketNum;
    static int SVC_LayerNum;
    static int currentLayer = 0;

    static int[] messageSize;
    static int[] srcSymbols;
    static int[] mPaddingSize;

    static byte[] decVal ;

    static int requestcount=0;


    /*public static class PDATA{
    	private static int currentDegree;
    	private static int originalDegree;
    	private static int  requireSrc[];
    	private static String codedData;
    	private static int seed;

    	public static void set_currentDegree(int degree){
    		currentDegree=degree;
    	}

    	public static int read_currentDegree(){
    		return currentDegree;
    	}
    }
    static public PDATA[][] PData;*/
    public static int PData_currentDegree[][];
    static public int PData_originalDegree[][];
    static public int  PData_requireSrc[][][];
    static public byte[][][] PData_codedData;
    static public int PData_seed[][];

    static boolean[] isRecordUpdate;
    static boolean[] isDecvalUpdate;
    static boolean[] isGlobalDecvalUpdate;





    static int[][] selfDecodedSymbolsRecord= new int[nodeNum][];
    static int[][] decRecord = new int[nodeNum][];
    static int[] LocalRead= new int[nodeNum];
    static int[] GlobalRead= new int[nodeNum];
    static int[] globalDecodedSymbolsRecord;
    static int[] Broadcast= new int[nodeNum];
    static int[] LocalWrite= new int[nodeNum];
    static int[] GlobalWrite = new int[nodeNum];
    static int[] GlobalTryWrite = new int[nodeNum];
    static int[] sDecodedSymbols = new int[nodeNum];
    static int[][] decode_num = new int [nodeNum][];

    static int[] elementNum = new int[nodeNum];  // Used in PrintHello
    static int[] rippleStep = new int[nodeNum];  // Used in PrintHello  init=1
    static int[] RippleSize = new int[nodeNum];  // Used in PrintHello


    //int n =100;
    // Storage Location
    static String Storage_Directory = "/StorageNode_";//"C:/Users/melissa/AndroidStudioProjects/WifiLT/app/src/main/assets/StorageNode_";//"D:\\學長CODE\\PC\\node\\EncData2\\StorageNode_";

    // Encoded File Location
    static String Header_FileName = "_encDataHeaderInfo_";
    static String Data_FileName = "_encData_";
    static String Encoded_FileName = "/L";
    static String Encoded_Extension = ".txt";




    // Input Node Info
    static String NodeInfo_FileName= "Nodeinfo";
    static String NodeInfo_extension = ".txt";

    // Output File Location
    static String Output_Directory = "C:\\";
    static String Output_FileName = "/output_L";
    static String Output_Extension = ".264";



    // Decode Table Location
    String DecodeTable_Directory = ".\\EncData\\StorageNode_";
    static String DecodeTable_FileName = "\\decodeTable_";
    static String tempTable = "L";
    static String DecodeTable_extension = ".txt";
    static String Status = "Process";
    static String CastOrder = "NULL";





    int sendRecordCount = 0;




    int computationStep = 0;

    double waiting_Time = 0;
    int deliveryCost = 0;
    int sendCost = 0;
    int minDecodeNode = 1;

    double LossRate = 0.05;


    int requireNode;
    int GjoinNode;

    double read_Time = 0;

    static int finishcount =0;
}
