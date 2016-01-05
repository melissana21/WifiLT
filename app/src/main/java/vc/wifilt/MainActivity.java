package vc.wifilt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import java.io.IOException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements DeviceFragment.OnListFragmentInteractionListener, LogFragment.OnFragmentInteractionListener{

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SwitchCompat mServiceSwitch;
    private final IntentFilter mIntentFilter = new IntentFilter();
    protected static List<WifiP2pDevice> sPeers = new ArrayList<>();
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    WifiDirectBroadcastReceiver mWifiDirectBroadcastReceiver;
    public static final String TAG="MainActivity";
    private String mGroupOwnerIP;
    private String mBroadcastAddress;
    private String mHeaderInfo;
    private String mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new DeviceFragment(), "DEVICE");
        viewPagerAdapter.addFragment(new LogFragment(), "LOG");
        mViewPager.setAdapter(viewPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);


        System.out.println("Start !!");
        for(int node_ID=0; node_ID<declaration.nodeNum; node_ID++){
            int flag_first = 1;
            //int node_ID=0;
            declaration.currentLayer=0;
            try {
                readNodeInfo.main(node_ID,getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                readHeaderInfo.main(declaration.currentLayer, node_ID,getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }



       }
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



        for(int node_ID=0; node_ID<declaration.nodeNum; node_ID++){
            try {
                declaration.elementNum[node_ID] =readEncodedData.main(declaration.EncPacketNum[declaration.currentLayer],declaration.currentLayer, node_ID,getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //for(int node_ID=0; node_ID<declaration.nodeNum; node_ID++){
        //PrintHello.set(node_ID);
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
        String Outputfilename= declaration.Storage_Directory+"1"+declaration.Output_FileName+index+declaration.Output_Extension;
        System.out.println(Outputfilename);
        System.out.println(declaration.rippleStep[0]);
        //System.out.println(declaration.rippleStep[1]);


        try{
            //Byte result[]= new Byte[declaration.messageSize[declaration.currentLayer] * declaration.srcSymbols[declaration.currentLayer] - declaration.mPaddingSize[declaration.currentLayer]];
            FileOutputStream fo=new FileOutputStream(Outputfilename);


            for(int u = 0 ; u < (declaration.messageSize[declaration.currentLayer] * declaration.srcSymbols[declaration.currentLayer]) - declaration.mPaddingSize[declaration.currentLayer] ; u++)
            {
                fo.write(declaration.decVal[u]);
            }


            fo.close();

        }catch (Exception e) {
        }

        /*try {
            mHeaderInfo = convertStreamToString(getAssets().open("StorageNode_1/L0_encDataHeaderInfo_25.txt"));
            mData = convertStreamToString(getAssets().open("StorageNode_1/L0_encData_25.txt"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Read: " + mHeaderInfo);
        Log.v(TAG, "Data: " + mData);*/
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


    @Override
    protected void onResume() {
        super.onResume();
        mWifiDirectBroadcastReceiver = new WifiDirectBroadcastReceiver();
        registerReceiver(mWifiDirectBroadcastReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mWifiDirectBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Context context = getSupportActionBar().getThemedContext();
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem switchItem = menu.findItem(R.id.toggleService);
        mServiceSwitch = (SwitchCompat) MenuItemCompat.getActionView(switchItem)
                .findViewById(R.id.serviceSwitch);
        mServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchItem.setTitle(R.string.stop_service_title);
                    Toast.makeText(MainActivity.this, "Service start", Toast.LENGTH_SHORT).show();

                    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(MainActivity.this, "Discovery initiation is successful", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int reason) {
                            Toast.makeText(MainActivity.this, "Discovery initiation fails", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    switchItem.setTitle(R.string.start_service_title);

                    MainActivity.this.startService(new Intent(MainActivity.this, ServerSocketService.class));
                    LocalBroadcastManager.getInstance(MainActivity.this)
                            .registerReceiver(new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    String message = (String) intent.getSerializableExtra("EXTRA_DATA");
                                    Toast.makeText(MainActivity.this, "Receive: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }, new IntentFilter("WIFI_DIRECT_SOCKET"));
//                    stopService();
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onListFragmentInteraction(WifiP2pDevice item) {
//        Toast.makeText(MainActivity.this, item.toString(), Toast.LENGTH_SHORT).show();

        if (item.status == WifiP2pDevice.AVAILABLE) {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = item.deviceAddress;
            config.wps.setup = WpsInfo.PBC;

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(MainActivity.this, "Connect failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (item.status == WifiP2pDevice.CONNECTED) {
            Intent broadcastIntent = new Intent(MainActivity.this, ClientSocketService.class);
//            broadcastIntent.putExtra("EXTRA_IP", "224.0.0.1");
            broadcastIntent.putExtra("EXTRA_IP", mBroadcastAddress);
            broadcastIntent.putExtra("EXTRA_DATA", mData);
            if (MainActivity.this.startService(broadcastIntent) != null) {
                Toast.makeText(MainActivity.this, "Broadcast success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Broadcast failed", Toast.LENGTH_SHORT).show();
            }
/*
            try {
                for (NetworkInterface n : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    Toast.makeText(MainActivity.this, n.getName() + " : " + n.getDisplayName(), Toast.LENGTH_SHORT).show();
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
*/        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void stopService() {
        mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Service stop", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {

                    }
                });
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            sPeers.clear();
            sPeers.addAll(peers.getDeviceList());
            Log.v(TAG, sPeers.toString());

            DeviceFragment.sDeviceRecyclerViewAdapter.notifyDataSetChanged();
        }
    };

    private class WifiDirectBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

                } else {

                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                if (mManager != null) {
                    mManager.requestPeers(mChannel, peerListListener);
                }
                Toast.makeText(MainActivity.this, "P2P peers changed", Toast.LENGTH_SHORT).show();
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                if (mManager == null) {
                    return;
                }

                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {
/*                    MainActivity.this.startService(new Intent(MainActivity.this, ServerSocketService.class));
                    LocalBroadcastManager.getInstance(MainActivity.this)
                            .registerReceiver(new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    String message = (String) intent.getSerializableExtra("EXTRA_DATA");
                                    Toast.makeText(MainActivity.this, "Receive: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }, new IntentFilter("WIFI_DIRECT_SOCKET"));
*/                    mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(WifiP2pInfo info) {
                            if (info.groupFormed && info.isGroupOwner) {
                                Toast.makeText(MainActivity.this, "This is group owner", Toast.LENGTH_SHORT).show();
                            } else if (info.groupFormed){
                                Toast.makeText(MainActivity.this, "This is client", Toast.LENGTH_SHORT).show();
                            }
                            mGroupOwnerIP = info.groupOwnerAddress.getHostAddress();
                            Toast.makeText(MainActivity.this, "GO's IP:" + mGroupOwnerIP, Toast.LENGTH_SHORT).show();
                            mBroadcastAddress = getBroadcastAddress(mGroupOwnerIP);
                            Toast.makeText(MainActivity.this, "Broadcast: " + mBroadcastAddress, Toast.LENGTH_SHORT).show();
/*                            Intent broadcastIntent = new Intent(MainActivity.this, ClientSocketService.class);
                            broadcastIntent.putExtra("EXTRA_IP", mBroadcastAddress);
                            broadcastIntent.putExtra("EXTRA_DATA", mBroadcastAddress);
                            MainActivity.this.startService(broadcastIntent);
*/                        }
                    });
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            }
        }
    }

    private String getBroadcastAddress(String groupOwnerIP) {
        if (groupOwnerIP == null) {
            return "255.255.255.255";
        }
        String[] ip = groupOwnerIP.split("\\.");
        return ip[0] + "." + ip[1] + "." + ip[2] + ".255";
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
}
