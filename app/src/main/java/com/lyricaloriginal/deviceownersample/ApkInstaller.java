package com.lyricaloriginal.deviceownersample;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
class ApkInstaller {

    interface Listener {
        void onProgress(ApkInstaller apkinstaller, String progMsg);
    }

    class Result {
        final boolean result;
        final String errMsg;

        private Result(boolean result, String errMsg) {
            this.result = result;
            this.errMsg = errMsg;
        }
    }

    private final Listener mListener;

    ApkInstaller() {
        this(null);
    }

    ApkInstaller(Listener listener) {
        mListener = listener;
    }

    Result installApk(@NonNull Context context, @NonNull File apkFile, @NonNull PendingIntent pendingIntent) throws Exception {
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        int sessionId = createSession(packageInstaller);
        if (sessionId < 0) {
            return new Result(false, "apkインストール用のセッションを作成することができませんでした。");
        }
        writeSession(packageInstaller, sessionId, apkFile);
        commitSession(packageInstaller, sessionId, pendingIntent);
        return new Result(true, "");
    }

    private int createSession(PackageInstaller packageInstaller) throws IOException {
        final PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setInstallLocation(PackageInfo.INSTALL_LOCATION_INTERNAL_ONLY);
        return packageInstaller.createSession(params);
    }

    private int writeSession(PackageInstaller packageInstaller, final int sessionId, File apkFile) throws IOException {
        long sizeBytes = -1;
        final String splitName = "INSTALL";
        final String apkPath = apkFile.getAbsolutePath();

        final File file = new File(apkPath);
        if (file.isFile()) {
            sizeBytes = file.length();
        }
        Log.v("hoge", "apk size :" + sizeBytes);

        PackageInstaller.Session session = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            session = packageInstaller.openSession(sessionId);
            in = new FileInputStream(apkPath);
            out = session.openWrite(splitName, 0, sizeBytes);
            int total = 0;
            byte[] buffer = new byte[65536];
            int c;
            while ((c = in.read(buffer)) != -1) {
                total += c;
                out.write(buffer, 0, c);
            }
            session.fsync(out);
            return 0;
        } finally {

            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (session != null)
                session.close();
        }
    }

    private void commitSession(PackageInstaller packageInstaller, final int sessionId, PendingIntent pendingIntent) throws IOException {
        PackageInstaller.Session session = null;
        try {
            Log.v("install_apk", "commitSession :");
            session = packageInstaller.openSession(sessionId);
            session.commit(pendingIntent.getIntentSender());
            Log.v("install_apk", "commitSession finished");
        } finally {
            session.close();
        }
    }

}