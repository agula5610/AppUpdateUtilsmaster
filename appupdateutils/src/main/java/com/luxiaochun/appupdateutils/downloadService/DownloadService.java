package com.luxiaochun.appupdateutils.downloadService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.luxiaochun.appupdateutils.AppUpdateBean;
import com.luxiaochun.appupdateutils.HttpManager;
import com.luxiaochun.appupdateutils.http.OkGoUpdateHttpUtil;

import java.io.File;

/**
 * 后台下载
 */
public class DownloadService extends Service {
    private static final String TAG = DownloadService.class.getSimpleName();
    private SilenceNotificationManager silenceNotificationManager;
    private DownloadBinder binder = new DownloadBinder();
    private HttpManager httpManager;
    private boolean isPaused = false;

    public static void bindService(Context context, ServiceConnection connection) {
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 返回自定义的DownloadBinder实例
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class DownloadBinder extends Binder {
        /**
         * 开始下载
         * @param updateApp
         * @param callback
         */
        public void start(AppUpdateBean updateApp, DownloadCallback callback) {
            //apk所在地址：指定地址+版本号+apkName
            httpManager = new OkGoUpdateHttpUtil();
            String target = updateApp.getApkDownloadPath() + File.separator + updateApp.getNewVersion();
            httpManager.download(updateApp.getApkDownloadUrl(), target, new DownloadService.FileDownloadCallBack(updateApp, callback));
        }

        /**
         * 暂停下载
         */
        public void pause(String url) {
            if (httpManager != null) {
                isPaused = true;
                httpManager.pause(url);
            }
        }

        /**
         * 继续下载
         */
        public void continued(String url) {
            if (httpManager != null) {
                isPaused = false;
                httpManager.continueDownload(url);
            }
        }

        /**
         * 取消下载
         * @param url
         */
        public void cancel(String url){
            if (httpManager != null) {
                httpManager.remove(url);
            }
        }

        /**
         * 停止后台服务
         * @param conn
         */
        public void stop(ServiceConnection conn) {
            if (httpManager != null) {
                httpManager = null;
            }
            stopSelf();
            getApplicationContext().unbindService(conn);
        }

        /**
         * 判断是否是暂停状态
         * @return
         */
        public boolean isPaused(){
            return isPaused;
        }
    }

    class FileDownloadCallBack implements HttpManager.FileCallback {
        private final DownloadCallback mCallBack;
        AppUpdateBean updateApp;

        public FileDownloadCallBack(AppUpdateBean updateApp, @Nullable DownloadCallback callback) {
            super();
            this.mCallBack = callback;
            this.updateApp = updateApp;
        }

        @Override
        public void onBefore() {
            if (mCallBack != null) {
                mCallBack.onStart();
                if (updateApp.isSilence()) {
                    silenceNotificationManager = SilenceNotificationManager.getIstance(DownloadService.this);
                    silenceNotificationManager.setUpNotification();
                }
            }
        }

        @Override
        public void onProgress(float progress, long total) {
            //设置一个刷新时间--RefreshTime，防止自回调过于频繁，造成更新通知栏进度过于频繁，而出现卡顿的问题。
            long lastTime = updateApp.getLastRefreshTime();
            if (updateApp.getRefreshTime() < SystemClock.elapsedRealtime() - lastTime) {
                if (mCallBack != null) {
                    if (updateApp.isSilence()) {
                        int rate = Math.round(progress * 100);
                        silenceNotificationManager.setProgress(rate);
                    } else {
                        mCallBack.onProgress(progress, total);
                    }
                }
                updateApp.setLastRefreshTime(SystemClock.elapsedRealtime());
            }
        }

        @Override
        public void onError(String error) {
            Toast.makeText(DownloadService.this, "更新新版本出错，" + error, Toast.LENGTH_SHORT).show();
            if (mCallBack != null) {
                if (updateApp.isSilence()) {
                    silenceNotificationManager.errorStop(error);
                }
                mCallBack.onError(error);
            }
        }

        @Override
        public void onResponse(File file) {
            if (mCallBack != null) {
                if (updateApp.isSilence()) {
                    silenceNotificationManager.setProgressDone(file);
                }
                mCallBack.onFinish(file);
            }
        }
    }
}
