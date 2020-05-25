package com.luxiaochun.appupdateutils.downloadutils;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;

import com.luxiaochun.appupdateutils.common.AppUpdateBean;
import com.luxiaochun.appupdateutils.downloadService.Callback;
import com.luxiaochun.appupdateutils.downloadService.DownloadService;
import com.luxiaochun.appupdateutils.downloadService.SilenceNotificationManager;
import com.luxiaochun.appupdateutils.utils.AppUpdateUtils;

import java.io.File;

/**
 * ProjectName: AppUpdateUtilsmaster
 * PackageName: com.luxiaochun.appupdateutils
 * Author: jun
 * Date: 2019-07-11 10:07
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class SlienceDownloadUtils extends DownloadUtils {
    private Context context;
    private SilenceNotificationManager silenceNotificationManager;

    public SlienceDownloadUtils(Context context, AppUpdateBean bean) {
        this.context = context;
        this.bean = bean;
    }

    @Override
    public void download() {
        if (TextUtils.isEmpty(bean.getUrl())) {
            Toast.makeText(context, "下载路径错误", Toast.LENGTH_SHORT).show();
            return;
        }
        File appDir = new File(bean.getPath());
        if (!appDir.exists()) {
            if (!appDir.mkdirs())
                return;
        }
        //apk所在地址：指定地址+版本号+apkName
        DownloadService.bindService(context.getApplicationContext(), conn);
    }

    void startDownload(DownloadService.DownloadBinder binder) {
        if (bean != null) {
            this.mDownloadBinder = binder;
            binder.start(bean, new Callback() {
                @Override
                public void onStart() {
                    silenceNotificationManager = SilenceNotificationManager.getIstance(context);
                    silenceNotificationManager.setUpNotification();
                }

                @Override
                public void onProgress(float progress, long totalSize) {
                    long lastTime = bean.getLastRefreshTime();
                    if (bean.getRefreshTime() < SystemClock.elapsedRealtime() - lastTime) {
                        int rate = Math.round(progress * 100);
                        if (silenceNotificationManager != null) {
                            silenceNotificationManager.setProgress(rate);
                        }
                        bean.setLastRefreshTime(SystemClock.elapsedRealtime());
                    }
                }

                @Override
                public void onFinish(final File file) {
                    cancelDownload();
                    cancelDownloadService();
                    if (silenceNotificationManager != null) {
                        silenceNotificationManager.cancel();
                    }
                    if (context != null) {
                        AppUpdateUtils.installApp(context, file);
                    }
                }

                @Override
                public void onError(String msg) {
                    if (silenceNotificationManager != null) {
                        silenceNotificationManager.errorStop(msg);
                    }
                }
            });
        }
    }

    @Override
    public void pauseDownload() {

    }

    @Override
    public void continueDownload() {

    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public void cancelDownload() {

    }
}
