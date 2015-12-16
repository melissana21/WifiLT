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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeviceFragment.OnListFragmentInteractionListener, LogFragment.OnFragmentInteractionListener {

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
                    stopService();
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onListFragmentInteraction(WifiP2pDevice item) {
        Toast.makeText(MainActivity.this, item.toString(), Toast.LENGTH_SHORT).show();

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
        }
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
                    mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(WifiP2pInfo info) {
                            if (info.groupFormed && info.isGroupOwner) {
                                Toast.makeText(MainActivity.this, "This is group owner", Toast.LENGTH_SHORT).show();
                            } else if (info.groupFormed) {
                                Toast.makeText(MainActivity.this, "This is client", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            }
        }
    }
}
