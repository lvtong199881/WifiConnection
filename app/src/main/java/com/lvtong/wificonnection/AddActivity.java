package com.lvtong.wificonnection;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author 22939
 */
public class AddActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private Boolean isShow = false;
    private Security mSecurity = new Security(Constrants.Security.TYPE_NONE,"无");

    private EditText etSSID;
    private EditText etPassword;
    private ImageView ivShowPassword;
    private TextView tvSecurityMethod;
    private LinearLayout llPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initData();
        initView();
    }

    private void initData() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        llPassword.setVisibility(mSecurity.getKey() == Constrants.Security.TYPE_NONE ?View.GONE:View.VISIBLE);
    }

    private void initView() {
        setHeader();
        etSSID = findViewById(R.id.et_ssid);
        llPassword = findViewById(R.id.ll_password);
        etPassword = findViewById(R.id.et_password);
        tvSecurityMethod = findViewById(R.id.tv_selected_method);
        ivShowPassword = findViewById(R.id.iv_show_password);
        ivShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Boolean.TRUE.equals(isShow)) {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivShowPassword.setImageResource(R.drawable.anquanxing2_3);
                    isShow = false;
                } else {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivShowPassword.setImageResource(R.drawable.anquanxing2_zhengyan);
                    isShow = true;
                }
            }
        });
        findViewById(R.id.ll_security).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, SecurityActivity.class);
                startActivityForResult(intent, Constrants.RequestCode.SECURITY);
            }
        });
        findViewById(R.id.tv_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ssid = etSSID.getText()
                                    .toString();
//                String password = etPassword.getText()
//                                            .toString();
                if (connectWifiNoPassword(wifiManager, ssid)) {
                    Toast.makeText(AddActivity.this, "连接成功", Toast.LENGTH_SHORT)
                         .show();
                } else {
                    Toast.makeText(AddActivity.this, "连接失败", Toast.LENGTH_SHORT)
                         .show();
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
        findViewById(R.id.tv_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constrants.RequestCode.SECURITY && resultCode == RESULT_OK) {
            if (data != null && data.getSerializableExtra(Constrants.Security.SECURITY) != null) {
                mSecurity = (Security) data.getSerializableExtra(Constrants.Security.SECURITY);
                tvSecurityMethod.setText(mSecurity.getName());
            }
        }
    }
    public boolean connectWifiNoPassword(WifiManager mWifiManager, String ssid) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + ssid + "\"";
        configuration.hiddenSSID = true;
        configuration.status = WifiConfiguration.Status.ENABLED;
        configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return mWifiManager.enableNetwork(mWifiManager.addNetwork(configuration), true);
    }
    public boolean connectWifiPassword(WifiManager mWifiManager, String SSID, String Password) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + SSID + "\"";
        configuration.preSharedKey = "\"" + Password + "\"";
        configuration.hiddenSSID = true;
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
