package com.lvtong.wificonnection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 22939
 */
public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private Switch mWifiSwitch;
    private RecyclerView mWifiRecyclerView;
    private WifiAdapter mWifiAdapter;
    private List<ScanResult> scanResults = new ArrayList<>();

    private ConstraintLayout clConnected;
    private TextView tvSSID;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            searchWifi();
            setConnectedWifi();
            mHandler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        mHandler.postDelayed(mRunnable, 3000);
        initData();
        initView();
    }

    private void requestPermissions() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                               Constrants.RequestCode.PERMISSION);
        }
    }

    private void initData() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
    }

    private void initView() {
        clConnected = findViewById(R.id.cl_connected);
        tvSSID = findViewById(R.id.tv_ssid);
        setConnectedWifi();
        mWifiRecyclerView = findViewById(R.id.rv_wifi_list);
        mWifiRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mWifiSwitch = findViewById(R.id.switch_wifi_enabled);
        mWifiSwitch.setChecked(wifiManager.isWifiEnabled());
        if (mWifiSwitch.isChecked()) {
            searchWifi();
        }
        mWifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wifiManager.setWifiEnabled(isChecked);
                if (isChecked) {
                    searchWifi();
                }
            }
        });
        findViewById(R.id.tv_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OtherActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setConnectedWifi();
    }

    private void setConnectedWifi() {
        wifiInfo = wifiManager.getConnectionInfo();
        if ("<unknown ssid>"
                    .equals(wifiInfo.getSSID())) {
            clConnected.setVisibility(View.GONE);
        } else {
            clConnected.setVisibility(View.VISIBLE);
            tvSSID.setText(wifiInfo.getSSID()
                                   .replace('"', ' '));
        }
    }

    private void searchWifi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.startScan();
            scanResults = wifiManager.getScanResults();
            Collections.sort(scanResults, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult o1, ScanResult o2) {
                    return o2.level - o1.level;
                }
            });
//            System.out.println("MYWIFI:" + scanResults.toString());
        } else {
            scanResults.clear();
        }
        mWifiRecyclerView.setAdapter(getWifiAdapter());
    }

    private WifiAdapter getWifiAdapter() {
        if (mWifiAdapter == null) {
            mWifiAdapter = new WifiAdapter();
        }
        return mWifiAdapter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == Constrants.RequestCode.PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请手动打开定位权限！", Toast.LENGTH_SHORT)
                     .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {

        @NonNull
        @Override
        public WifiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WifiAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                                                            .inflate(R.layout.item_wifi_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final WifiAdapter.ViewHolder holder, int position) {
            final ScanResult scanResult = scanResults.get(position);
//            System.out.println("MYWIFI:" + scanResult.toString());
            holder.tvSSID.setText(scanResult.SSID);
            holder.ivLocked.setVisibility("[ESS]".equals(scanResult.capabilities) ? View.GONE : View.VISIBLE);
            holder.ivSignalStrength.setImageLevel(0 - scanResult.level);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.ivLocked.getVisibility() == View.VISIBLE) {
                        Intent intent = new Intent(MainActivity.this, ConnectionActivity.class);
                        intent.putExtra(Constrants.SCAN_RESULT, scanResult);
                        startActivity(intent);
                    } else {
                        if (connectWifiNoPassword(wifiManager, scanResult.SSID)) {
                            Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT)
                                 .show();
                            setConnectedWifi();
                        } else {
                            Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT)
                                 .show();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return scanResults.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvSSID;
            private ImageView ivLocked;
            private ImageView ivSignalStrength;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSSID = itemView.findViewById(R.id.tv_ssid);
                ivLocked = itemView.findViewById(R.id.iv_locked);
                ivSignalStrength = itemView.findViewById(R.id.iv_signal_strength);
            }
        }
    }

    public boolean connectWifiNoPassword(WifiManager mWifiManager, String ssid) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + ssid + "\"";
        configuration.status = WifiConfiguration.Status.ENABLED;
        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return mWifiManager.enableNetwork(mWifiManager.addNetwork(configuration), true);
    }
}
