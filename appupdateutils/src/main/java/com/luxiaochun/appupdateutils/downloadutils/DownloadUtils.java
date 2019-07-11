package com.luxiaochun.appupdateutils.downloadutils;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.luxiaochun.appupdateutils.common.AppUpdateBean;
import com.luxiaochun.appupdateutils.downloadService.DownloadService;

/**
 * ProjectName: AppUpdateUtilsmaster
 * PackageName: com.luxiaochun.appupdateutils
 * Author: jun
 * Date: 2019-07-11 09:30
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public abstract class DownloadUtils {
    AppUpdateBean bean;
    DownloadService.DownloadBinder mDownloadBinder;

    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            startDownload((DownloadService.DownloadBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public abstract void download();

    abstract void startDownload(DownloadService.DownloadBinder binder);

    /**
     * 停止后台运行的服务进程
     */
    public void cancelDownloadService() {
        if (mDownloadBinder != null) {
            mDownloadBinder.stop(conn);
        }
    }

    /**
     * 暂停下载
     */
    public abstract void pauseDownload();

    /**
     * 继续下载
     */
    public abstract void continueDownload();

    /**
     * 判断是否是暂停状态
     * @return
     */
    public abstract boolean isPaused();

    /**
     * 取消下载
     */
    public abstract void cancelDownload();
}
