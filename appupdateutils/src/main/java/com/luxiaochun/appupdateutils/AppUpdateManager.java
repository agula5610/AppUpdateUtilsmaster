package com.luxiaochun.appupdateutils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.luxiaochun.appupdateutils.common.AppUpdateBean;
import com.luxiaochun.appupdateutils.common.UpdateType;
import com.luxiaochun.appupdateutils.downloadService.SilenceUpdateCallback;
import com.luxiaochun.appupdateutils.utils.AppUpdateUtils;

import java.io.File;


/**
 * 版本更新管理器
 */
public class AppUpdateManager {
    public static final String TAG = AppUpdateManager.class.getSimpleName();
    private static final long REFRESH_TIME = 150;  //毫秒

    private Context mContext;
    private AppUpdateBean bean;


    private AppUpdateManager(Builder builder) {
        bean = new AppUpdateBean();

        bean.setUrl(builder.getUrl());

        bean.setType(builder.getType());
        bean.setPath(builder.getPath());

        bean.setVersion(builder.getVersion());
        bean.setNotes(builder.getNotes());
        bean.setTitle(builder.getTitle());
        bean.setThemeColor(builder.getThemeColor());
        bean.setTopPic(builder.getTopPic());

        bean.setWifi(builder.isWifi());

        mContext = builder.getContext();
    }


    public void update() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG, bean);
        RocketFragment fragment = RocketFragment
                .newInstance(bundle);
        if (UpdateType.Slience == bean.getType()) {
            fragment.setSilenceUpdateCallback(new SilenceUpdateCallback() {
                @Override
                public void onDownloadFinish(File file) {
                    if (mContext != null) {
                        if (AppUpdateUtils.isAppOnForeground(mContext)) {
                            AppUpdateUtils.installApp(mContext, file);
                        }
                    }
                }
            });
        }
        fragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "");
    }

    public static class Builder {
        // 必填
        private Context context;
        // 下载地址
        private String url;
        // 新版本
        private String version;
        // 更新说明
        private String notes;
        // 标题
        private String title;
        // 主题颜色,按钮，进度条的颜色
        private int themeColor = -1;
        // 头部图片
        @DrawableRes
        private int topPic = -1;
        // 下载地址
        private String path;
        private long refreshTime = REFRESH_TIME;

        private boolean wifi;

        private UpdateType type;

        /**
         * @param type 更新模式
         * @return Builder
         */
        public Builder setType(UpdateType type) {
            this.type = type;
            return this;
        }

        public Builder setRefreshTime(long refreshTime) {
            this.refreshTime = refreshTime;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setThemeColor(int color) {
            themeColor = color;
            return this;
        }

        public Builder setTopPic(int topPic) {
            this.topPic = topPic;
            return this;
        }

        public Builder setWifi() {
            wifi = true;
            return this;
        }


        public Context getContext() {
            return context;
        }

        public String getUrl() {
            return url;
        }

        public String getVersion() {
            return version;
        }

        public String getNotes() {
            return notes;
        }

        public String getTitle() {
            return title;
        }

        public int getThemeColor() {
            return themeColor;
        }

        public int getTopPic() {
            return topPic;
        }

        public String getPath() {
            return path;
        }

        public long getRefreshTime() {
            return refreshTime;
        }

        public boolean isWifi() {
            return wifi;
        }

        public UpdateType getType() {
            return type;
        }

        /**
         * @return 生成app管理器
         */
        public AppUpdateManager build() {
            //校验
            if (null == context) {
                throw new NullPointerException("必要参数不能为空");
            }
            if (TextUtils.isEmpty(path)) {
                //sd卡是否存在
                String path = "";
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    try {
                        path = context.getExternalCacheDir().getAbsolutePath();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(path)) {
                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    }
                } else {
                    path = context.getCacheDir().getAbsolutePath();
                }
                setPath(path);
            }
            return new AppUpdateManager(this);
        }
    }

}

