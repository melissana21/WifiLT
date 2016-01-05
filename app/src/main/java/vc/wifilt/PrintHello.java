package vc.wifilt;

/**
 * Created by melissa on 2016/1/4.
 */
public class PrintHello extends Thread  {
    static int node_ID;
    public static void set(int ID){
        node_ID = ID;
    }


    public void run(){
        for(int node_ID=0; node_ID<declaration.nodeNum; node_ID++){
            while (true) {


                //System.out.println("Hello");
                System.out.print("Hello");
                System.out.println(node_ID);


                if (declaration.globalFinish == 0) {
                    LTDistributed_decoder.main(declaration.elementNum[node_ID], declaration.rippleStep[node_ID], declaration.currentLayer, node_ID);
                    if(declaration.RippleSize[node_ID]>0 && declaration.sDecodedSymbols[node_ID] < declaration.srcSymbols[declaration.currentLayer]){
                        declaration.rippleStep[node_ID]++;
                    }



                }
                else {
                    System.out.println("收工!");
                    break;
                }
            }
        }
    }
}
