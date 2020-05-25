package com.luxiaochun.appupdateutils.common;

/**
 * ProjectName: 2019_AnHui_jianghuai_dangjian
 * PackageName: com.luxiaochun.appupdateutils
 * Author: jun
 * Date: 2019-07-10 09:39
 * Copyright: (C)HESC Co.,Ltd. 2016. All rights reserved.
 */
public enum UpdateType {
    // 普通模式(默认)
    Normal,
    // 静默下载
    Slience,
    // 强制更新
    Force,
    // 仅下载(不显示升级信息)
    Download,
    // 只做提示，不做升级，可以关闭
    Hint;
}
