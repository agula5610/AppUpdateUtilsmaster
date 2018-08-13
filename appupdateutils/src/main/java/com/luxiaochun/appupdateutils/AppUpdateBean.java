package com.luxiaochun.appupdateutils;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * ProjectName: JiuZhou
 * PackageName: com.example.jun.jiuzhou.AppUpdateUtil
 * Author: jun
 * Date: 2018-08-02 15:48
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public class AppUpdateBean implements Serializable {
    //新版本号
    private String newVersion;
    //新app下载地址
    private String apkDownloadUrl;
    //apk下载路径
    private String apkDownloadPath;
    //更新日志
    private String apkUpdateLog;
    //配置默认更新dialog 的title
    private String dialogTitle;
    //新app大小
    private String apkSize;
    //是否强制更新
    private boolean isConstraint;
    //是否静默更新
    private boolean isSilence;
    private int dialogThemeColor;
    @DrawableRes
    private int dialogTopPic;

    private boolean mOnlyWifi;

    private HttpManager mHttpManager;

    private long lastRefreshTime;

    private long refreshTime;

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(long lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public HttpManager getmHttpManager() {
        return mHttpManager;
    }

    public void setmHttpManager(HttpManager mHttpManager) {
        this.mHttpManager = mHttpManager;
    }

    public boolean isSilence() {
        return isSilence;
    }

    public void setSilence(boolean silence) {
        isSilence = silence;
    }

    public boolean ismOnlyWifi() {
        return mOnlyWifi;
    }

    public void setmOnlyWifi(boolean mOnlyWifi) {
        this.mOnlyWifi = mOnlyWifi;
    }

    public String getApkDownloadPath() {
        return apkDownloadPath;
    }

    public void setApkDownloadPath(String apkDownloadPath) {
        this.apkDownloadPath = apkDownloadPath;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getApkDownloadUrl() {
        return apkDownloadUrl;
    }

    public void setApkDownloadUrl(String apkDownloadUrl) {
        this.apkDownloadUrl = apkDownloadUrl;
    }

    public String getApkUpdateLog() {
        return apkUpdateLog;
    }

    public void setApkUpdateLog(String apkUpdateLog) {
        this.apkUpdateLog = apkUpdateLog;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public String getApkSize() {
        return apkSize;
    }

    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }

    public boolean isConstraint() {
        return isConstraint;
    }

    public void setConstraint(boolean constraint) {
        isConstraint = constraint;
    }

    public int getDialogThemeColor() {
        return dialogThemeColor;
    }

    public void setDialogThemeColor(int dialogThemeColor) {
        this.dialogThemeColor = dialogThemeColor;
    }

    public int getDialogTopPic() {
        return dialogTopPic;
    }

    public void setDialogTopPic(int dialogTopPic) {
        this.dialogTopPic = dialogTopPic;
    }
}
