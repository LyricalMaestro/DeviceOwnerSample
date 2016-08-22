package com.lyricaloriginal.deviceownersample;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    private Button mInstallLocalAppBtn;
    private Button mPolicyAutoGrantedBtn;
    private Button mPolicyPromptBtn;
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
        mPolicyAutoGrantedBtn = (Button) findViewById(R.id.policy_auto_granted_btn);
        mPolicyAutoGrantedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ComponentName cn = new ComponentName(MainActivity.this, MyDeviceAdminReceiver.class);
                    mDpm.setPermissionPolicy(cn, DevicePolicyManager.PERMISSION_POLICY_AUTO_GRANT);
                    Toast.makeText(MainActivity.this, "Policy Auto Granted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPolicyPromptBtn = (Button) findViewById(R.id.policy_prompt_btn);
        mPolicyPromptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    /**
                     * 元のM Permissionのポリシーに戻すときに使う
                     */
                    ComponentName cn = new ComponentName(MainActivity.this, MyDeviceAdminReceiver.class);
                    mDpm.setPermissionPolicy(cn, DevicePolicyManager.PERMISSION_POLICY_PROMPT);
                    Toast.makeText(MainActivity.this, "Policy Prompt", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.show_dcim_file_list_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionChecker.checkSelfPermission(
                        MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            },
                            REQUEST_CODE_STORAGE_PERMISSION);
                    return;
                }
                showDcimList();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showDcimList();
            }
        }
        //  許可されなかった。
    }

    private void showDcimList() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String[] dirs = dir.list();
        if (dirs == null) {
            Toast.makeText(this, "DCIMフォルダ内に何もありません。", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, TextUtils.join("\n", dirs), Toast.LENGTH_LONG).show();
    }

    private void setButtonEnable(boolean enable) {
        mInstallLocalAppBtn.setEnabled(enable);
        mClearDeviceOwnerBtn.setEnabled(enable);
        mPolicyAutoGrantedBtn.setEnabled(enable);
        mPolicyPromptBtn.setEnabled(enable);
    }
}
