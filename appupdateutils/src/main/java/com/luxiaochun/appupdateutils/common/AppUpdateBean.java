package com.luxiaochun.appupdateutils.common;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * ProjectName: JiuZhou
 * PackageName: com.example.jun.jiuzhou.AppUpdateUtil
 * Author: jun
 * Date: 2018-08-02 15:48
 */
public class AppUpdateBean implements Serializable {
    //新版本号
    private String version;
    //新app下载地址
    private String url;
    //apk下载路径
    private String path;
    //更新日志
    private String notes;
    //配置默认更新dialog 的title
    private String title;
    //新app大小
    private String apkSize;

    private UpdateType type;
    private int themeColor;
    @DrawableRes
    private int topPic;

    private boolean wifi;


    private long lastRefreshTime;

    private long refreshTime;

    public UpdateType getType() {
        return type;
    }

    public void setType(UpdateType type) {
        this.type = type;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApkSize() {
        return apkSize;
    }

    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(int themeColor) {
        this.themeColor = themeColor;
    }

    public int getTopPic() {
        return topPic;
    }

    public void setTopPic(int topPic) {
        this.topPic = topPic;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(long lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }
}
