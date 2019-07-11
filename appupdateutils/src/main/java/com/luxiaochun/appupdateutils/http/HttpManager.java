package com.luxiaochun.appupdateutils.http;

import android.support.annotation.NonNull;

import com.luxiaochun.appupdateutils.downloadService.Callback;

import java.io.Serializable;

/**
 * app版本更新接口
 */
public interface HttpManager extends Serializable {

    /**
     * 下载
     *
     * @param url      下载地址
     * @param path     文件保存路径
     * @param callback 回调
     */
    void download(@NonNull String url, @NonNull String path, @NonNull Callback callback);

    /**
     * 继续下载，断点续传
     * @param url
     */
    void continueDownload(@NonNull String url);

    /**
     * 暂停下载
     * @param url
     */
    void pause(@NonNull String url);

    /**
     * 删除任务
     * @param url
     */
    void remove(@NonNull String url);
}
