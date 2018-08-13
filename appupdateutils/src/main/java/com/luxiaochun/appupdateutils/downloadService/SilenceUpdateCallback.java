package com.luxiaochun.appupdateutils.downloadService;

import java.io.File;

/**
 * ProjectName: JiuZhou
 * PackageName: com.example.jun.jiuzhou.AppUpdateUtil.downloadService
 * Author: jun
 * Date: 2018-08-13 09:13
 */
public interface SilenceUpdateCallback {
    void onDownloadFinish(File file);
}
