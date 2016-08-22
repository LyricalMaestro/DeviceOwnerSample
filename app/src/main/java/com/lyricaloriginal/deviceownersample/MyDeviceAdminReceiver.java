package com.lyricaloriginal.deviceownersample;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        DevicePolicyManager dpm = (DevicePolicyManager) context.
                getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm.isDeviceOwnerApp(context.getPackageName())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ComponentName cn = getWho(context);
                dpm.setPermissionPolicy(cn, DevicePolicyManager.PERMISSION_POLICY_AUTO_GRANT);
            }
        }
    }
}
