package vc.wifilt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements DeviceFragment.OnListFragmentInteractionListener, LogFragment.OnFragmentInteractionListener{

    protected static Context MainContext;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SwitchCompat mServiceSwitch;
    private CheckBox mOwnerCheckBox;
    private final IntentFilter mIntentFilter = new IntentFilter();
    protected static List<WifiP2pDevice> sPeers = new ArrayList<>();
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    WifiDirectBroadcastReceiver mWifiDirectBroadcastReceiver;
    public static final String TAG="MainActivity";
    private String mGroupOwnerIP;
    private static String mBroadcastAddress;
    protected static boolean sIsGroupOwner = false;
    protected static String mOwnerAddress;
    protected static String mMacAddress;
    private byte[] mHeaderInfo;
    private byte[] mData;

    protected static final Object waitingLock = new Object();
    protected static boolean isWaiting;
    protected static ExecutorService executorService;

    protected static FileOutputStream sFileOutputStream;
    protected static FileOutputStream sRequestRecordDelayStream;
    protected static FileOutputStream sRequestDecvalDelayStream;
    protected static FileOutputStream sUpdateDecvalDelayStream;

    protected static DatagramSocket sDatagramSocket;

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

        MainContext = getApplicationContext();
        executorService = Executors.newCachedThreadPool();

        try {
            sFileOutputStream = new FileOutputStream(getExternalFilesDir(null).getAbsolutePath() + "/degree.txt");
            sRequestRecordDelayStream = new FileOutputStream(getExternalFilesDir(null).getAbsolutePath() + "/request_record.txt");
            sRequestDecvalDelayStream = new FileOutputStream(getExternalFilesDir(null).getAbsolutePath() + "/request_decval.txt");
            sUpdateDecvalDelayStream = new FileOutputStream(getExternalFilesDir(null).getAbsolutePath() + "/update_decval.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "path: " + getExternalFilesDir(null).getAbsolutePath());









        try {
            mHeaderInfo = convertStreamToBytes(getAssets().open("StorageNode_1/L0_encDataHeaderInfo_25.txt"));
//            mData = convertStreamToString(getAssets().open("StorageNode_1/L0_encData_25.txt"));

        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.v(TAG, "Read: " + new String(mHeaderInfo));


        mBroadcastAddress = getBroadcastAddress(wifiIpAddress(this));
        mMacAddress = getMacAddress(this);
        int node_ID = 0;
        //// set node_ID(0,1)  cache : -1(owner)
        declaration.messageSize = new int[3];
        declaration.srcSymbols = new int[3];

        declaration.messageSize[declaration.currentLayer] = 268;
        declaration.srcSymbols[declaration.currentLayer] = 1000;
        declaration.decVal = new byte[declaration.messageSize[declaration.currentLayer]*declaration.srcSymbols[declaration.currentLayer]];
        declaration.globalDecodedSymbolsRecord= new int[declaration.srcSymbols[declaration.currentLayer]]; //init=0
        startService(new Intent(MainActivity.this, ServerSocketService.class));
        LocalBroadcastManager.getInstance(MainActivity.this)
                .registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        executorService.submit(new PacketProcessingService(intent));
                    }
                }, new IntentFilter("WIFI_DIRECT_SOCKET"));
//        Log.v(TAG, "Data: " + mData);
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
        try {
            sFileOutputStream.close();
            sRequestRecordDelayStream.close();
            sRequestDecvalDelayStream.close();
            sUpdateDecvalDelayStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sDatagramSocket.close();
        stopService(new Intent(MainActivity.this, ServerSocketService.class));
        executorService.shutdownNow();
//        stopService();
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
//                    Toast.makeText(MainActivity.this, "Service start", Toast.LENGTH_SHORT).show();
//
//                    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
//                        @Override
//                        public void onSuccess() {
//                            Toast.makeText(MainActivity.this, "Discovery initiation is successful", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onFailure(int reason) {
//                            Toast.makeText(MainActivity.this, "Discovery initiation fails", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
                    switchItem.setTitle(R.string.start_service_title);
                    try {
                        sDatagramSocket = new DatagramSocket();
                        sDatagramSocket.setBroadcast(true);
                        sDatagramSocket.setReuseAddress(true);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                    if (sIsGroupOwner) {
                        sendPacket(new PacketData("OWNER_ADDRESS", mMacAddress.getBytes()));
                    }
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                AfterP2P.main(0);
                            }
                        });
                        FinishLayer finishLayer = new FinishLayer();
                        MainActivity.executorService.submit(finishLayer);
//                    stopService();
                }
            }
        });
        final MenuItem checkBoxItem = menu.findItem(R.id.setOwner);
        mOwnerCheckBox = (CheckBox) MenuItemCompat.getActionView(checkBoxItem)
                .findViewById(R.id.ownerCheckBox);
        mOwnerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sIsGroupOwner = true;
                } else {
                    sIsGroupOwner = false;
                }
                Toast.makeText(MainActivity.this, "isGroupOwner: " + sIsGroupOwner, Toast.LENGTH_SHORT).show();
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
            sendPacket(new PacketData("TEST", mHeaderInfo));
//            sendPacket(new PacketData("TEST", String.valueOf(1).getBytes()));
/*            Intent broadcastIntent = new Intent(MainActivity.this, ClientSocketService.class);
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
                    MainActivity.this.startService(new Intent(MainActivity.this, ServerSocketService.class));
                    LocalBroadcastManager.getInstance(MainActivity.this)
                            .registerReceiver(new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
//                                    Log.v(TAG, "submit packet");
                                    executorService.submit(new PacketProcessingService(intent));
//                                    Gson gson = new Gson();
//                                    String message = intent.getStringExtra("EXTRA_DATA");
//                                    Log.v("receive", message);
//                                    Log.v("receive", "byte to string: " + new String(message));
//                                    PacketData packetData = gson.fromJson(message, PacketData.class);
//                                    PacketData returnData;
//                                    String type = packetData.getType();
//                                    int index;
//                                    switch (type) {
//                                        case "REQUEST_GLOBAL_RECORD":
//                                            if (!sIsGroupOwner) {
//                                                return;
//                                            }
//                                            index = Integer.parseInt(new String(packetData.getData()));
//                                            returnData =
//                                                    new PacketData("ANSWER_GLOBAL_RECORD",
//                                                            String.valueOf(declaration.globalDecodedSymbolsRecord[index]).getBytes());
//                                            returnData.setPosition(index);
//                                            returnData.setDes(packetData.getOri());
//                                            sendPacket(returnData);
//                                            Log.v(TAG, "send answer global record: " + index);
//                                            break;
//                                        case "REQUEST_GLOBAL_DECVAL":
//                                            if (!sIsGroupOwner) {
//                                                return;
//                                            }
//                                            byte[] array = Arrays.copyOfRange(declaration.decVal, packetData.getPosition(), packetData.getPosition() + declaration.messageSize[declaration.currentLayer]);
//                                            returnData = new PacketData("ANSWER_GLOBAL_DECVAL", array);
//                                            returnData.setPosition(packetData.getPosition());
//                                            returnData.setDes(packetData.getOri());
//                                            sendPacket(returnData);
//                                            break;
//                                        case "UPDATE_GLOBAL_DECVAL":
//                                            if (!sIsGroupOwner) {
//                                                return;
//                                            }
//                                            System.arraycopy(packetData.getData(), 0, declaration.decVal, packetData.getPosition(), declaration.messageSize[declaration.currentLayer]);
//                                            break;
//                                        case "UPDATE_GLOBAL_RECORD":
//                                            if (!sIsGroupOwner) {
//                                                return;
//                                            }
//                                            declaration.globalDecodedSymbolsRecord[packetData.getPosition()] = Integer.parseInt(new String(packetData.getData()));
//                                            Log.v(TAG, "UPDATE: " + declaration.globalDecodedSymbolsRecord[packetData.getPosition()]);
//                                            Log.v(TAG,"position : " + packetData.getPosition());
//                                            break;
//                                        case "ANSWER_GLOBAL_RECORD":
//                                            if (!packetData.getDes().equals(mMacAddress)) {
//                                                return;
//                                            }
//                                            Log.v(TAG, "receive answer global record: " + packetData.getPosition());
//                                            int data = Integer.parseInt(new String(packetData.getData()));
//                                            declaration.globalDecodedSymbolsRecord[packetData.getPosition()] = data;
//                                            synchronized (waitingLock) {
//                                                Log.v(TAG, "unlock waiting");
//                                                isWaiting = false;
//                                                waitingLock.notify();
//                                            }
//                                            break;
//                                        case "ANSWER_GLOBAL_DECVAL":
//                                            if (!packetData.getDes().equals(mMacAddress)) {
//                                                return;
//                                            }
//                                            System.arraycopy(packetData.getData(), 0, declaration.decVal, packetData.getPosition(), declaration.messageSize[declaration.currentLayer]);
//                                            synchronized (waitingLock) {
//                                                isWaiting = false;
//                                                waitingLock.notify();
//                                            }
//                                            break;
//                                        default:
//                                            Toast.makeText(MainActivity.this, "Receive: " + new String(packetData.getData()), Toast.LENGTH_SHORT).show();
//                                            Log.v(TAG, "data: " + new String(packetData.getData()));
//                                            break;
//                                    }
                                }
                            }, new IntentFilter("WIFI_DIRECT_SOCKET"));
                    mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(WifiP2pInfo info) {
                            if (info.groupFormed && info.isGroupOwner) {
                                sIsGroupOwner = true;
                                Toast.makeText(MainActivity.this, "This is group owner", Toast.LENGTH_SHORT).show();
                            } else if (info.groupFormed){
                                Toast.makeText(MainActivity.this, "This is client", Toast.LENGTH_SHORT).show();
                            }
                            mGroupOwnerIP = info.groupOwnerAddress.getHostAddress();
//                            Toast.makeText(MainActivity.this, "GO's IP:" + mGroupOwnerIP, Toast.LENGTH_SHORT).show();
                            mBroadcastAddress = getBroadcastAddress(mGroupOwnerIP);
//                            Toast.makeText(MainActivity.this, "Broadcast: " + mBroadcastAddress, Toast.LENGTH_SHORT).show();
/*                            Intent broadcastIntent = new Intent(MainActivity.this, ClientSocketService.class);
                            broadcastIntent.putExtra("EXTRA_IP", mBroadcastAddress);
                            broadcastIntent.putExtra("EXTRA_DATA", mBroadcastAddress);
                            MainActivity.this.startService(broadcastIntent);
*/                        }
                    });
                    int node_ID = 0;
                    //// set node_ID(0,1)  cache : -1(owner)
                    declaration.messageSize = new int[3];
                    declaration.srcSymbols = new int[3];

                    declaration.messageSize[declaration.currentLayer] = 268;
                    declaration.srcSymbols[declaration.currentLayer] = 1000;
                    declaration.decVal = new byte[declaration.messageSize[declaration.currentLayer]*declaration.srcSymbols[declaration.currentLayer]];
                    declaration.globalDecodedSymbolsRecord= new int[declaration.srcSymbols[declaration.currentLayer]]; //init=0


                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                WifiP2pDevice device = (WifiP2pDevice) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                mMacAddress = device.deviceAddress;
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

    protected String wifiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }

    public String getMacAddress(Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "Device don't have mac address or wi-fi is disabled";
        }
        return macAddress;
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

    public static byte[] convertStreamToBytes(InputStream is) throws Exception {
        byte[] line = new byte[268];
        is.read(line);
        is.close();
        return line;
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    protected static String convertIntArrayToString(int[] array) {
        String string = new String();
        for (int i : array) {
            string += i;
        }
        return string;
    }

    protected static int[] convertStringToIntArray(String string) {
        int[] array = new int[string.length()];
        for (int i = 0; i < string.length(); i++) {
            array[i] = Character.getNumericValue(string.charAt(i));
        }
        return array;
    }

    protected static void sendPacket(PacketData data) {
        data.setOri(mMacAddress);
        Intent broadcastIntent = new Intent(MainContext, ClientSocketService.class);
        broadcastIntent.putExtra("EXTRA_IP", mBroadcastAddress);
        broadcastIntent.putExtra("EXTRA_DATA", data);
//        Log.v("sendPacket", "data: " + data.toString());
        if (MainContext.startService(broadcastIntent) != null) {
//            Log.v(TAG, "Broadcast: " + data.toString());
        } else {
            Log.v(TAG, "Broadcast failed");
        }
    }
}
