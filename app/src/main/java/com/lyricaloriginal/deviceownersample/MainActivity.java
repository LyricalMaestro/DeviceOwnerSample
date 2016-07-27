package com.lyricaloriginal.deviceownersample;

import android.app.admin.DevicePolicyManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mClearDeviceOwnerBtn;

    private DevicePolicyManager mDpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mClearDeviceOwnerBtn = (Button) findViewById(R.id.clear_device_owner_btn);
        mClearDeviceOwnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDpm.clearDeviceOwnerApp(getPackageName());
                v.setEnabled(false);
            }
        });
        mDpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mClearDeviceOwnerBtn.setEnabled(mDpm.isDeviceOwnerApp(getPackageName()));
    }
}
