package com.lvtong.wificonnection;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 22939
 */
public class SecurityActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private List<Security> mSecurityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        initData();
        initView();
    }

    private void initData() {
        mSecurityList.add(new Security(Constrants.Security.TYPE_NONE, "无"));
        mSecurityList.add(new Security(Constrants.Security.TYPE_WEP, "WEP"));
        mSecurityList.add(new Security(Constrants.Security.TYPE_WPA, "WPA/WPA2 PSK"));
        mSecurityList.add(new Security(Constrants.Security.TYPE_EAP, "802.1 x EAP"));
    }

    private void initView() {
        setHeader();
        mRecyclerView = findViewById(R.id.rv_security_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new SecurityAdapter());
    }

    private void setHeader() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class SecurityAdapter extends RecyclerView.Adapter<SecurityAdapter.ViewHolder> {

        @NonNull
        @Override
        public SecurityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SecurityAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                                                                .inflate(R.layout.item_wifi_security, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SecurityAdapter.ViewHolder holder, int position) {
            final Security security = mSecurityList.get(position);
            holder.tvSecurity.setText(security.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SecurityActivity.this, AddActivity.class);
                    intent.putExtra(Constrants.Security.SECURITY, security);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mSecurityList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvSecurity;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSecurity = itemView.findViewById(R.id.tv_security);
            }
        }
    }
}
