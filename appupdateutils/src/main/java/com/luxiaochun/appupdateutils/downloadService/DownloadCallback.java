package com.luxiaochun.appupdateutils.downloadService;

/**
 * ProjectName: JiuZhou
 * PackageName: com.example.jun.jiuzhou.AppUpdateUtil.downloadService
 * Author: jun
 * Date: 2018-08-08 14:27
 */

import java.io.File;

public interface DownloadCallback {
    /**
     * 开始
     */
    void onStart();

    /**
     * 进度
     *
     * @param progress  进度 0.00 -1.00 ，总大小
     * @param totalSize 总大小 单位B
     */
    void onProgress(float progress, long totalSize);

    /**
     * 下载完了
     *
     * @param file 下载的app
     * @return true ：下载完自动跳到安装界面，false：则不进行安装
     */
    void onFinish(File file);

    /**
     * 下载异常
     *
     * @param msg 异常信息
     */
    void onError(String msg);


}
