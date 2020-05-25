package com.luxiaochun.appupdateutils.downloadutils;


import android.text.TextUtils;
import android.widget.Toast;

import com.luxiaochun.appupdateutils.RocketFragment;
import com.luxiaochun.appupdateutils.common.AppUpdateBean;
import com.luxiaochun.appupdateutils.downloadService.Callback;
import com.luxiaochun.appupdateutils.downloadService.DownloadService;

import java.io.File;

/**
 * ProjectName: AppUpdateUtilsmaster
 * PackageName: com.luxiaochun.appupdateutils
 * Author: jun
 * Date: 2019-07-11 10:08
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class RocketDownloadUtils extends DownloadUtils {

    private RocketFragment fragment;

    public RocketDownloadUtils(RocketFragment fragment, AppUpdateBean bean) {
        this.fragment = fragment;
        this.bean = bean;
    }

    @Override
    public void download() {
        if (TextUtils.isEmpty(bean.getUrl())) {
            Toast.makeText(fragment.getActivity(), "下载路径错误", Toast.LENGTH_SHORT).show();
            return;
        }
        File appDir = new File(bean.getPath());
        if (!appDir.exists()) {
            if (!appDir.mkdirs())
                return;
        }
        //apk所在地址：指定地址+版本号+apkName
        DownloadService.bindService(fragment.getActivity().getApplicationContext(), conn);
    }

    void startDownload(DownloadService.DownloadBinder binder) {
        if (bean != null) {
            this.mDownloadBinder = binder;
            binder.start(bean, new Callback() {
                @Override
                public void onStart() {
                    fragment.onRocketStart();
                }

                @Override
                public void onProgress(float progress, long totalSize) {
                    fragment.onRocketProgress(progress, totalSize);
                }

                @Override
                public void onFinish(final File file) {
                    cancelDownload();
                    cancelDownloadService();
                    fragment.onRocketFinish(file);
                }

                @Override
                public void onError(String msg) {
                    cancelDownload();
                    cancelDownloadService();
                    fragment.onRocketError(msg);
                }
            });
        }
    }

    @Override
    public void pauseDownload() {
        if (mDownloadBinder != null) {
            fragment.onRocketPause();
            mDownloadBinder.pause(bean.getUrl());
        }
    }

    @Override
    public void continueDownload() {
        if (mDownloadBinder != null) {
            fragment.onRocketContinue();
            mDownloadBinder.continued(bean.getUrl());
        }
    }

    @Override
    public boolean isPaused(){
        if (mDownloadBinder != null) {
            return mDownloadBinder.isPaused();
        }
        return false;
    }

    @Override
    public void cancelDownload() {
        if (mDownloadBinder != null) {
            mDownloadBinder.cancel(bean.getUrl());
        }
    }
}
