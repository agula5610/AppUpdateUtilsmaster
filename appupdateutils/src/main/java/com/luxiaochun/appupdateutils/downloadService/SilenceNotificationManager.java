package com.luxiaochun.appupdateutils.downloadService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.luxiaochun.appupdateutils.R;
import com.luxiaochun.appupdateutils.utils.AppUpdateUtils;

import java.io.File;

/**
 * ProjectName: JiuZhou
 * PackageName: com.example.jun.jiuzhou.AppUpdateUtil
 * Author: jun
 * Date: 2018-08-08 11:45
 */
public class SilenceNotificationManager {
    private static volatile SilenceNotificationManager instance;
    private final Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private static final String CHANNEL_ID = "app_update_id";
    private static final CharSequence CHANNEL_NAME = "app_update_channel";
    private static final int NOTIFY_ID = 0;

    private SilenceNotificationManager(Context mContext) {
        this.mContext = mContext;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static SilenceNotificationManager getIstance(Context mContext) {
        return new SilenceNotificationManager(mContext);
    }

    /**
     * 创建通知
     */
    public void setUpNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            //设置绕过免打扰模式
//            channel.setBypassDnd(false);
//            //检测是否绕过免打扰模式
//            channel.canBypassDnd();
//            //设置在锁屏界面上显示这条通知
//            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//            channel.setLightColor(Color.GREEN);
//            channel.setShowBadge(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.enableVibration(false);
            channel.enableLights(false);

            mNotificationManager.createNotificationChannel(channel);
        }

        mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        mBuilder.setContentTitle("开始下载")
                .setContentText("正在连接服务器")
                .setSmallIcon(R.drawable.lib_update_app_update_icon)
                .setLargeIcon(AppUpdateUtils.drawableToBitmap(AppUpdateUtils.getAppIcon(mContext)))
                .setOngoing(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }

    /**
     * 取消通知
     */
    public void cancel(){
        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFY_ID);
        }
    }

    public void setProgress(int rate){
        if (mBuilder != null) {
            mBuilder.setContentTitle("正在下载：" + AppUpdateUtils.getAppName(mContext))
                    .setContentText(rate + "%")
                    .setProgress(100, rate, false)
                    .setWhen(System.currentTimeMillis());
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
            mNotificationManager.notify(NOTIFY_ID, notification);
        }
    }

    public void setProgressDone(File file){
        if (mBuilder != null) {
//            Intent installAppIntent = AppUpdateUtils.getInstallAppIntent(mContext, file);
//            PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, installAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentTitle(AppUpdateUtils.getAppName(mContext))
                    .setContentText("下载完成。")
                    .setProgress(0, 0, false)
                    //                        .setAutoCancel(true)
                    .setDefaults((Notification.DEFAULT_ALL));
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(NOTIFY_ID, notification);
        }
    }

    public void errorStop(String contentText) {
        if (mBuilder != null) {
            mBuilder.setContentTitle(AppUpdateUtils.getAppName(mContext))
                    .setContentText(contentText);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(NOTIFY_ID, notification);
        }
    }
}
