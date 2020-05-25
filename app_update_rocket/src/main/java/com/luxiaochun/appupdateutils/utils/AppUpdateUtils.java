package com.luxiaochun.appupdateutils.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.TypedValue;

import com.luxiaochun.appupdateutils.common.AppUpdateBean;

import java.io.File;
import java.util.List;

/**
 * Created by luxiaochun
 * on 2019/07/10
 */

public class AppUpdateUtils {

    public static final String IGNORE_VERSION = "ignore_version";
    public static final String APK_SIZE = "apk_size";
    private static final String PREFS_FILE = "update_app_config.xml";

    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }


    public static File getAppFile(AppUpdateBean updateAppBean) {
        String appName = getApkName(updateAppBean);
        return new File(updateAppBean.getPath()
                .concat(File.separator + updateAppBean.getVersion())
                .concat(File.separator + appName));
    }

    /**
     * 获取apk的名字
     *
     * @param updateAppBean
     * @return
     */
    @NonNull
    public static String getApkName(AppUpdateBean updateAppBean) {
        String apkUrl = updateAppBean.getUrl();
        String appName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1, apkUrl.length());
        if (!appName.endsWith(".apk")) {
            appName = "temp.apk";
        }
        return appName;
    }

    /**
     * apk是否完整下载,如果为存在但未完整下载，则删除掉重新下载
     *
     * @param context
     * @param updateAppBean
     * @return
     */
    public static boolean appIsDownloaded(@NonNull Context context, AppUpdateBean updateAppBean) {
        //文件存在
        File appFile = getAppFile(updateAppBean);
        if (appFile.exists()) {
            if (isApkSize(context, appFile.length())) {
                return true;
            } else {
                appFile.delete();
                return false;
            }
        } else {
            return false;
        }
    }

    public static void installApp(Context context, File appFile) {
        Intent intent = getInstallAppIntent(context, appFile);
        if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            context.startActivity(intent);
        }
    }

    public static void installApp(Fragment fragment, File appFile) {
        installApp(fragment.getActivity(), appFile);
    }

    public static Intent getInstallAppIntent(Context context, File appFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
                Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", appFile);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(appFile), "application/vnd.android.package-archive");
            }
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    public static String getAppName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
        }
        return "";
    }

    public static Drawable getAppIcon(Context context) {
        try {
            return context.getPackageManager().getApplicationIcon(context.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

    public static int dp2px(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


    private static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    public static void saveIgnoreVersion(Context context, String newVersion) {
        getSP(context).edit().putString(IGNORE_VERSION, newVersion).apply();
    }

    public static void saveApkSize(@NonNull Context context, long size) {
        getSP(context).edit().putLong(APK_SIZE, size).apply();
    }

    public static boolean isApkSize(@NonNull Context context, long fileLength) {
        return getSP(context).getLong(APK_SIZE, 0) == fileLength;
    }

    public static boolean isNeedIgnore(Context context, String newVersion) {
        return getSP(context).getString(IGNORE_VERSION, "").equals(newVersion);
    }
}
