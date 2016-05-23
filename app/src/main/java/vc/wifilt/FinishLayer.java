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
                MainActivity.sCacheSpendTime = System.currentTimeMillis()-MainActivity.sCacheSpendTime;
                break;
            }
//            declaration.waiting_time = System.currentTimeMillis() - declaration.waiting_time;
            if(MainActivity.sIsGroupOwner && (System.currentTimeMillis()-declaration.waiting_time) > 3000){
                System.out.println(declaration.waiting_time);
                declaration.waiting_time = System.currentTimeMillis();
                PacketData Data;
                Data =
                        new PacketData("ANSWER_GLOBAL_RECORD",
                                MainActivity.convertIntArrayToString(declaration.globalDecodedSymbolsRecord).getBytes());//
                Data.setPosition(0);
                Data.setDes(MainActivity.mOwnerAddress);
                MainActivity.sendPacket(Data);
            }
            finish = 1;
            unfinishcount = 0;

            for(int n = 0 ; n < declaration.srcSymbols[declaration.currentLayer] ; n++){
//                PacketData packetData = new PacketData("REQUEST_GLOBAL_RECORD", String.valueOf(n).getBytes());
//                MainActivity.sendPacket(packetData);
                if(declaration.globalDecodedSymbolsRecord[n] == 0){

                    unfinishcount++;
                    if(declaration.srcSymbols[declaration.currentLayer]-unfinishcount == 1 && MainActivity.firstcountflag == true) {
                        MainActivity.sCacheSpendTime = System.currentTimeMillis();
                        MainActivity.firstcountflag = false;
                    }
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
                MainActivity.setLogText("Finish Count: " + declaration.finishcount);
                declaration.waiting_time=System.currentTimeMillis();
            }
        }
    }
}
