package vc.wifilt;

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
        //System.out.println(symbolNum);

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
                        if(declaration.globalDecodedSymbolsRecord[declaration.PData_requireSrc[node_ID][r][s]-1] != 0)
                        {
                            if(declaration.selfDecodedSymbolsRecord[node_ID][declaration.PData_requireSrc[node_ID][r][s]-1] == 0){
                                declaration.GlobalRead[node_ID]++;
                                declaration.selfDecodedSymbolsRecord[node_ID][declaration.PData_requireSrc[node_ID][r][s]-1] =1;
                            }
                            decode = 1;
                        }
                        if(decode == 1){

                            //computationStep++;
//
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
                            if(declaration.globalDecodedSymbolsRecord[index - 1] == 0){
                                System.arraycopy(declaration.PData_codedData[node_ID][r],0,declaration.decVal,((index - 1) * declaration.messageSize[declaration.currentLayer]),declaration.messageSize[declaration.currentLayer]);
                                declaration.GlobalWrite[node_ID]++;
                            }
                            declaration.globalDecodedSymbolsRecord[index - 1] = rStep;
                            declaration.sDecodedSymbols[node_ID]++;

                            //sendRecordCount++;

                            break;
                        }
                    }
                }

            }
        }

    }

}
