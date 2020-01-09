package com.lvtong.wificonnection;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author 22939
 */
public class ConnectionActivity extends AppCompatActivity {

    private ScanResult mScanResult;

    private EditText etPassword;
    private ImageView mShowPassword;
    private Boolean isShow = false;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        mScanResult = intent.getParcelableExtra(Constrants.SCAN_RESULT);
        if (mScanResult == null) {
            finish();
        }
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private void initView() {
        setHeader();
        etPassword = findViewById(R.id.et_password);
        mShowPassword = findViewById(R.id.iv_show_password);
        mShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Boolean.TRUE.equals(isShow)) {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mShowPassword.setImageResource(R.drawable.anquanxing2_3);
                    isShow = false;
                } else {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mShowPassword.setImageResource(R.drawable.anquanxing2_zhengyan);
                    isShow = true;
                }
            }
        });
        findViewById(R.id.tv_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etPassword.getText()
                                                .toString())) {
                    Toast.makeText(ConnectionActivity.this, "密码为空", Toast.LENGTH_SHORT)
                         .show();
                } else {
                    if (connectWifiPassword(wifiManager, mScanResult.SSID, etPassword.getText()
                                                                                     .toString())) {
                        finish();
                    } else {
                        Toast.makeText(ConnectionActivity.this, "连接网络失败", Toast.LENGTH_SHORT)
                             .show();
                    }
                }
            }
        });
    }

    private void setHeader() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(mScanResult.SSID);
    }

    public boolean connectWifiPassword(WifiManager mWifiManager, String SSID, String Password) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + SSID + "\"";
        configuration.preSharedKey = "\"" + Password + "\"";
        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        configuration.status = WifiConfiguration.Status.ENABLED;
        return mWifiManager.enableNetwork(mWifiManager.addNetwork(configuration), true);
    }
}
