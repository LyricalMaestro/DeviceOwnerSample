package com.lyricaloriginal.deviceownersample;

import android.app.admin.DevicePolicyManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mInstallLocalAppBtn;
    private Button mClearDeviceOwnerBtn;

    private DevicePolicyManager mDpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInstallLocalAppBtn = (Button) findViewById(R.id.install_app_btn);
        mInstallLocalAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mClearDeviceOwnerBtn = (Button) findViewById(R.id.clear_device_owner_btn);
        mClearDeviceOwnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * DeviceOwnerの解除
                 */
                mDpm.clearDeviceOwnerApp(getPackageName());
                setButtonEnable(false);
            }
        });
        mDpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setButtonEnable(mDpm.isDeviceOwnerApp(getPackageName()));
    }

    private void setButtonEnable(boolean enable) {
        mInstallLocalAppBtn.setEnabled(enable);
        mClearDeviceOwnerBtn.setEnabled(enable);
    }
}
