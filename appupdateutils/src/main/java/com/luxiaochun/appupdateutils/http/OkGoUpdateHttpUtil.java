package com.luxiaochun.appupdateutils.http;

import android.support.annotation.NonNull;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;

import java.io.File;

/**
 * 使用OkGo实现接口
 */

public class OkGoUpdateHttpUtil implements HttpManager {
    /**
     * 下载
     *
     * @param url      下载地址
     * @param path     文件保存路径
     * @param callback 回调
     */
    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull final FileCallback callback) {
        OkDownload.getInstance().setFolder(path);
        GetRequest<File> request = OkGo.<File>get(url);
        //这里第一个参数是tag，代表下载任务的唯一标识，传任意字符串都行，需要保证唯一,我这里用url作为了tag\
        OkDownload.request(url, request)
                .save()
                .register(new mDownLoadListener(url,callback))
                .start();
    }

    @Override
    public void continueDownload(@NonNull String url) {
        OkDownload.getInstance().getTask(url).start();
    }

    @Override
    public void pause(@NonNull String url) {
        OkDownload.getInstance().getTask(url).pause();
    }

    @Override
    public void remove(@NonNull String url) {
        OkDownload.getInstance().getTask(url).remove(true);
    }

    class mDownLoadListener extends com.lzy.okserver.download.DownloadListener {
        FileCallback callback;

        mDownLoadListener(Object tag, FileCallback callback) {
            super(tag);
            this.callback = callback;
        }

        @Override
        public void onStart(Progress progress) {
            callback.onBefore();
        }

        @Override
        public void onProgress(Progress progress) {
            callback.onProgress(progress.fraction, progress.totalSize);
        }

        @Override
        public void onError(Progress progress) {
            callback.onError("异常");
        }

        @Override
        public void onFinish(File file, Progress progress) {
            callback.onResponse(file);
        }

        @Override
        public void onRemove(Progress progress) {

        }
    }
}