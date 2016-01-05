package vc.wifilt;

/**
 * Created by melissa on 2016/1/4.
 */
public class FinishLayer extends Thread{

    public void run(){

        int finish = 0;

        int unfinishcount =0;
        int MinUnfinishcount = 9999999;
        while(true){
            if(finish == 1){
                declaration.globalFinish = 1;
                break;
            }
            finish = 1;
            unfinishcount = 0;

            for(int n = 0 ; n < declaration.srcSymbols[declaration.currentLayer] ; n++){
                if(declaration.globalDecodedSymbolsRecord[n] == 0){

                    unfinishcount++;
                    finish = 0;
                }
            }


            if(unfinishcount < MinUnfinishcount){
                MinUnfinishcount = unfinishcount;

            }

            if(declaration.finishcount<declaration.srcSymbols[declaration.currentLayer]-unfinishcount ){
                declaration.finishcount=declaration.srcSymbols[declaration.currentLayer]-unfinishcount;
                System.out.print("finishcount");
                System.out.println(declaration.finishcount);
            }
        }
    }
}
