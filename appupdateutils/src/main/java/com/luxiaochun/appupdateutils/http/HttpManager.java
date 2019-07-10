package com.luxiaochun.appupdateutils.http;

import android.support.annotation.NonNull;

import java.io.File;
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
    void download(@NonNull String url, @NonNull String path, @NonNull FileCallback callback);

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
    /**
     * 下载回调
     */
    interface FileCallback {
        /**
         * 进度
         *
         * @param progress 进度0.00 - 0.50  - 1.00
         * @param total    文件总大小 单位字节
         */
        void onProgress(float progress, long total);

        /**
         * 错误回调
         *
         * @param error 错误提示
         */
        void onError(String error);

        /**
         * 结果回调
         *
         * @param file 下载好的文件
         */
        void onResponse(File file);

        /**
         * 请求之前
         */
        void onBefore();

    }
}