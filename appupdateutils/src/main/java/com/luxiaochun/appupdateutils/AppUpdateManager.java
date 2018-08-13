package com.luxiaochun.appupdateutils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.luxiaochun.appupdateutils.downloadService.SilenceUpdateCallback;
import com.luxiaochun.appupdateutils.utils.AppUpdateUtils;

import java.io.File;


/**
 * 版本更新管理器
 */
public class AppUpdateManager {
    public static final String TAG = AppUpdateManager.class.getSimpleName();
    public static final long REFRESH_TIME = 150;
    /**
     * 三个必填项
     */
    private Activity mActivity;
    private HttpManager mHttpManager;
    private String apkDownloadUrl;
    /**
     * 选填项,均有默认值
     */
    private String newVersion;
    private String apkUpdateLog;
    private String dialogTitle;
    private int mThemeColor;
    @DrawableRes
    private int mTopPic;
    private String apkDownloadPath;
    private long refreshTime;

    private boolean mOnlyWifi;
    private boolean mSilence;
    private boolean mForce;
    //自定义参数

    private AppUpdateManager(Builder builder) {
        mActivity = builder.getActivity();
        mHttpManager = builder.getHttpManager();
        apkDownloadUrl = builder.getUpdateUrl();

        newVersion = builder.getNewVersion();
        apkUpdateLog = builder.getApkUpdateLog();
        dialogTitle = builder.getDialogTitle();
        mThemeColor = builder.getThemeColor();
        mTopPic = builder.getTopPic();
        apkDownloadPath = builder.getApkDownloadPath();
        refreshTime = builder.getRefreshTime();

        mOnlyWifi = builder.isOnlyWifi();
    }

    public Context getContext() {
        return mActivity;
    }

    /**
     * 暂时留用
     *
     * @return
     */
    private boolean verify() {
        return true;
    }

    /**
     * 最简方式
     */

    public void update() {
        showDialogFragment();
    }

    /**
     * 静默更新
     */
    public void silenceUpdate() {
        mSilence = true;
        update();
    }

    /**
     * 强制更新
     */
    public void forceUpdate() {
        mForce = true;
        update();
    }

    /**
     * 跳转到更新页面
     */
    private void showDialogFragment() {
        //校验
        if (!verify()) return;

        if (mActivity != null && !mActivity.isFinishing()) {
            Bundle bundle = new Bundle();
            //添加信息，
            AppUpdateBean bean = fillAppBean();
            bundle.putSerializable(TAG, bean);
            UpdateDialogFragment fragment = UpdateDialogFragment
                    .newInstance(bundle);
            if (bean.isSilence()) {
                fragment.setSilenceUpdateCallback(new SilenceUpdateCallback() {
                    @Override
                    public void onDownloadFinish(File file) {
                        if (mActivity != null) {
                            if (AppUpdateUtils.isAppOnForeground(mActivity)) {
                                AppUpdateUtils.installApp(mActivity,file);
                            }
                        }
                    }
                });
            }
            fragment.show(((FragmentActivity) mActivity).getSupportFragmentManager(), "dialog");

        }
    }

    /**
     * @return 新版本信息
     */
    private AppUpdateBean fillAppBean() {
        AppUpdateBean bean = new AppUpdateBean();
        bean.setmHttpManager(mHttpManager);
        bean.setApkDownloadUrl(apkDownloadUrl);

        bean.setNewVersion(newVersion);
        bean.setApkUpdateLog(apkUpdateLog);
        bean.setDialogTitle(dialogTitle);
        bean.setDialogThemeColor(mThemeColor);
        bean.setDialogTopPic(mTopPic);
        bean.setApkDownloadPath(apkDownloadPath);

        bean.setmOnlyWifi(mOnlyWifi);
        bean.setConstraint(mForce);
        bean.setSilence(mSilence);
        return bean;
    }

    public static class Builder {
        //必填
        private Activity mActivity;
        private HttpManager mHttpManager;
        private String mUpdateUrl;

        private String newVersion;
        private String apkUpdateLog;
        private String dialogTitle;
        private int mThemeColor = -1;
        @DrawableRes
        private int mTopPic = -1;
        private String apkDownloadPath;
        private long refreshTime = REFRESH_TIME;

        private boolean mOnlyWifi;

        public long getRefreshTime() {
            return refreshTime;
        }

        public Builder setRefreshTime(long refreshTime) {
            this.refreshTime = refreshTime;
            return this;
        }

        public String getApkDownloadPath() {
            return apkDownloadPath;
        }

        /**
         * apk的下载路径，
         *
         * @param targetPath apk的下载路径，
         * @return Builder
         */
        public Builder setApkDownloadPath(String targetPath) {
            apkDownloadPath = targetPath;
            return this;
        }


        public Activity getActivity() {
            return mActivity;
        }

        /**
         * 是否是post请求，默认是get
         *
         * @param activity 当前提示的Activity
         * @return Builder
         */
        public Builder setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public HttpManager getHttpManager() {
            return mHttpManager;
        }

        /**
         * 设置网络工具
         *
         * @param httpManager 自己实现的网络对象
         * @return Builder
         */
        public Builder setHttpManager(HttpManager httpManager) {
            mHttpManager = httpManager;
            return this;
        }

        public String getUpdateUrl() {
            return mUpdateUrl;
        }

        /**
         * 更新地址
         *
         * @param updateUrl 更新地址
         * @return Builder
         */
        public Builder setUpdateUrl(String updateUrl) {
            mUpdateUrl = updateUrl;
            return this;
        }

        public String getNewVersion() {
            return newVersion;
        }

        public Builder setNewVersion(String newVersion) {
            this.newVersion = newVersion;
            return this;
        }

        public String getApkUpdateLog() {
            return apkUpdateLog;
        }

        public Builder setApkUpdateLog(String apkUpdateLog) {
            this.apkUpdateLog = apkUpdateLog;
            return this;
        }

        public String getDialogTitle() {
            return dialogTitle;
        }

        public Builder setDialogTitle(String dialogTitle) {
            this.dialogTitle = dialogTitle;
            return this;
        }

        public int getThemeColor() {
            return mThemeColor;
        }

        /**
         * 设置按钮，进度条的颜色
         *
         * @param themeColor 设置按钮，进度条的颜色
         * @return Builder
         */
        public Builder setThemeColor(int themeColor) {
            mThemeColor = themeColor;
            return this;
        }

        public int getTopPic() {
            return mTopPic;
        }

        /**
         * 顶部的图片
         *
         * @param topPic 顶部的图片
         * @return Builder
         */
        public Builder setTopPic(int topPic) {
            mTopPic = topPic;
            return this;
        }

        /**
         * @return 生成app管理器
         */
        public AppUpdateManager build() {
            //校验
            if (getActivity() == null || getHttpManager() == null || TextUtils.isEmpty(getUpdateUrl())) {
                throw new NullPointerException("必要参数不能为空");
            }
            if (TextUtils.isEmpty(getApkDownloadPath())) {
                //sd卡是否存在
                String path = "";
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
                    try {
                        path = getActivity().getExternalCacheDir().getAbsolutePath();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(path)) {
                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    }
                } else {
                    path = getActivity().getCacheDir().getAbsolutePath();
                }
                setApkDownloadPath(path);
            }
            return new AppUpdateManager(this);
        }


        public Builder setOnlyWifi() {
            mOnlyWifi = true;
            return this;
        }

        public boolean isOnlyWifi() {
            return mOnlyWifi;
        }

    }

}

