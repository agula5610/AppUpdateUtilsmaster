package com.luxiaochun.appupdateutils.downloadService;

import java.io.File;

/**
 * ProjectName: AppUpdateUtilsmaster
 * PackageName: com.luxiaochun.appupdateutils.downloadService
 * Author: jun
 * Date: 2019-07-11 09:43
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public interface Callback {
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
