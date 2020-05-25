package com.luxiaochun.appupdateutils.downloadService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import com.luxiaochun.appupdateutils.common.AppUpdateBean;
import com.luxiaochun.appupdateutils.http.HttpManager;
import com.luxiaochun.appupdateutils.http.OkGoUpdateHttpUtil;

import java.io.File;

/**
 * 后台下载
 */
public class DownloadService extends Service {
    private static final String TAG = DownloadService.class.getSimpleName();
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
         *
         * @param updateApp
         * @param callback
         */
        public void start(AppUpdateBean updateApp, Callback callback) {
            //apk所在地址：指定地址+版本号+apkName
            httpManager = new OkGoUpdateHttpUtil();
            String target = updateApp.getPath() + File.separator + updateApp.getVersion();
            httpManager.download(updateApp.getUrl(), target, callback);
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
                httpManager.continued(url);
            }
        }

        /**
         * 取消下载
         *
         * @param url
         */
        public void cancel(String url) {
            if (httpManager != null) {
                httpManager.remove(url);
            }
        }

        /**
         * 停止后台服务
         *
         * @param conn
         */
        public void stop(ServiceConnection conn) {
            if (httpManager != null) {
                httpManager = null;
            }
            if (conn != null) {
                getApplicationContext().unbindService(conn);
            }
            stopSelf();
        }

        /**
         * 判断是否是暂停状态
         *
         * @return
         */
        public boolean isPaused() {
            return isPaused;
        }
    }
}
