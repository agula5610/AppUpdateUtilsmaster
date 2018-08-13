package com.luxiaochun.appupdateutils;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luxiaochun.appupdateutils.downloadService.DownloadCallback;
import com.luxiaochun.appupdateutils.downloadService.DownloadService;
import com.luxiaochun.appupdateutils.downloadService.SilenceUpdateCallback;
import com.luxiaochun.appupdateutils.utils.AppUpdateUtils;
import com.luxiaochun.appupdateutils.utils.ColorUtil;
import com.luxiaochun.appupdateutils.utils.DrawableUtil;
import com.luxiaochun.appupdateutils.view.NumberProgressBar;

import java.io.File;
import java.util.Objects;

public class UpdateDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TIPS = "请授权访问存储空间权限，否则App无法更新";

    private AppUpdateBean mAppBean;

    private TextView mContentTextView;
    private Button mUpdateOkButton;
    private NumberProgressBar mNumberProgressBar;
    private ImageView mIvClose;
    private TextView mTitleTextView;
    private LinearLayout mLoadingLL;
    private Button mCancelBtn;
    private Button mPauseContinueBtn;
    private boolean isPaused = false;
    /**
     * 回调
     */
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            startDownloadApp((DownloadService.DownloadBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private LinearLayout mLlClose;
    //默认色
    private int mDefaultColor = 0xffe94339;
    private int mDefaultPicResId = R.drawable.lib_update_app_top_bg;
    private ImageView mTopIv;
    private TextView mIgnore;
    private long apkTotalSize = 0;
    private DownloadService.DownloadBinder mDownloadBinder;
    private SilenceUpdateCallback silenceUpdateCallback;

    public static UpdateDialogFragment newInstance(Bundle args) {
        UpdateDialogFragment fragment = new UpdateDialogFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NO_TITLE | DialogFragment.STYLE_NO_FRAME, 0);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppUpdateDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        //点击window外的区域 是否消失
        getDialog().setCanceledOnTouchOutside(false);
        //是否可以取消,会影响上面那条属性
//        setCancelable(false);
        //window外可以点击,不拦截窗口外的事件
//        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //禁用
                    if (mAppBean != null && mAppBean.isConstraint()) {
                        //返回桌面
                        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });

        Window dialogWindow = getDialog().getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        lp.height = (int) (displayMetrics.heightPixels * 0.8f);
        dialogWindow.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lib_update_app_dialog, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    public void setSilenceUpdateCallback(SilenceUpdateCallback silenceUpdateCallback) {
        this.silenceUpdateCallback = silenceUpdateCallback;
    }

    private void initView(View view) {
        //提示内容
        mContentTextView = view.findViewById(R.id.tv_update_info);
        //标题
        mTitleTextView = view.findViewById(R.id.tv_title);
        //更新按钮
        mUpdateOkButton = view.findViewById(R.id.btn_ok);
        //进度条
        mNumberProgressBar = view.findViewById(R.id.npb);
        //取消和暂停按钮布局
        mLoadingLL = view.findViewById(R.id.ll_loading);
        //取消下载
        mCancelBtn = view.findViewById(R.id.btn_cancel);
        //暂停、继续下载
        mPauseContinueBtn = view.findViewById(R.id.btn_pause_continue);
        //关闭按钮
        mIvClose = view.findViewById(R.id.iv_close);
        //关闭按钮+线 的整个布局
        mLlClose = view.findViewById(R.id.ll_close);
        //顶部图片
        mTopIv = view.findViewById(R.id.iv_top);
        //忽略
        mIgnore = view.findViewById(R.id.tv_ignore);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        mAppBean = (AppUpdateBean) getArguments().getSerializable(AppUpdateManager.TAG);
        //设置主题色
        initTheme();
        if (mAppBean != null) {
            //弹出对话框
            final String dialogTitle = mAppBean.getDialogTitle();
            final String newVersion = mAppBean.getNewVersion();
            final String targetSize = mAppBean.getApkSize();
            final String updateLog = mAppBean.getApkUpdateLog();

            String msg = "";

            if (!TextUtils.isEmpty(targetSize)) {
                msg = "新版本大小：" + targetSize + "\n\n";
            }

            if (!TextUtils.isEmpty(updateLog)) {
                msg += updateLog;
            }

            //更新内容
            mContentTextView.setText(msg);
            //标题
            mTitleTextView.setText(TextUtils.isEmpty(dialogTitle) ? String.format("是否升级到%s版本？", newVersion) : dialogTitle);
            //强制更新
            if (mAppBean.isConstraint()) {
                mLlClose.setVisibility(View.GONE);
            }

            if (AppUpdateUtils.appIsDownloaded(Objects.requireNonNull(this.getActivity()), mAppBean)) {   //已下载完成
                showInstallBtn(AppUpdateUtils.getAppFile(mAppBean));
            }

            initClickEvents();
        }
    }

    /**
     * 初始化主题色
     */
    private void initTheme() {
        final int color = mAppBean.getDialogThemeColor();
        final int topResId = mAppBean.getDialogTopPic();

        if (-1 == topResId) {
            if (-1 == color) {
                //默认红色
                setDialogTheme(mDefaultColor, mDefaultPicResId);
            } else {
                setDialogTheme(color, mDefaultPicResId);
            }
        } else {
            if (-1 == color) {
                setDialogTheme(mDefaultColor, topResId);
            } else {
                setDialogTheme(color, topResId);
            }
        }
    }

    /**
     * 设置
     *
     * @param color    主色
     * @param topResId 图片
     */
    private void setDialogTheme(int color, int topResId) {
        mTopIv.setImageResource(topResId);
        mUpdateOkButton.setBackgroundDrawable(DrawableUtil.getDrawable(AppUpdateUtils.dip2px(4, getActivity()), color));
        mCancelBtn.setBackgroundDrawable(DrawableUtil.getDrawable(AppUpdateUtils.dip2px(4, getActivity()), color));
        mPauseContinueBtn.setBackgroundDrawable(DrawableUtil.getDrawable(AppUpdateUtils.dip2px(4, getActivity()), color));
        mNumberProgressBar.setProgressTextColor(color);
        mNumberProgressBar.setReachedBarColor(color);
        //随背景颜色变化
        mUpdateOkButton.setTextColor(ColorUtil.isTextColorDark(color) ? Color.BLACK : Color.WHITE);
        mCancelBtn.setTextColor(ColorUtil.isTextColorDark(color) ? Color.BLACK : Color.WHITE);
        mPauseContinueBtn.setTextColor(ColorUtil.isTextColorDark(color) ? Color.BLACK : Color.WHITE);
    }

    private void initClickEvents() {
        mUpdateOkButton.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mIgnore.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
        mPauseContinueBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_ok) {
            //权限判断是否有访问外部存储空间权限
            int flag = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (flag != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。
                    Toast.makeText(getActivity(), TIPS, Toast.LENGTH_LONG).show();
                } else {
                    // 申请授权。
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            } else {
                installApp();
            }
        } else if (i == R.id.btn_cancel) {
            cancelDownload();
            cancelDownloadService();
            dismiss();
        } else if (i == R.id.btn_pause_continue) {
            if (isPaused) {
                continueDownload();
            } else {
                pauseDownload();
            }
        } else if (i == R.id.iv_close) {
            dismiss();
        } else if (i == R.id.tv_ignore) {
            AppUpdateUtils.saveIgnoreVersion(getActivity(), mAppBean.getNewVersion());
            dismiss();
        }

    }

    /**
     * 回调监听下载
     *
     * @param binder
     */
    private void startDownloadApp(DownloadService.DownloadBinder binder) {
        // 开始下载，监听下载进度，可以用对话框显示
        if (mAppBean != null) {
            this.mDownloadBinder = binder;
            binder.start(mAppBean, new DownloadCallback() {
                @Override
                public void onStart() {
                    if (mAppBean.isSilence()) {
                        dismiss();
                    } else {
                        if (UpdateDialogFragment.this.isShowing()) {
                            getDialog().setCancelable(false);
                            mNumberProgressBar.setVisibility(View.VISIBLE);
                            mUpdateOkButton.setVisibility(View.GONE);
                            mLoadingLL.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onProgress(float progress, long totalSize) {
                    if (!mAppBean.isSilence()) {
                        if (UpdateDialogFragment.this.isShowing()) {
                            mNumberProgressBar.setProgress(Math.round(progress * 100));
                            mNumberProgressBar.setMax(100);
                            if (apkTotalSize == 0 && totalSize > 0) {
                                AppUpdateUtils.saveApkSize(Objects.requireNonNull(UpdateDialogFragment.this.getActivity()), totalSize);
                                apkTotalSize = totalSize;
                            }
                        }
                    }
                }

                @Override
                public void onFinish(final File file) {
                    if (mAppBean.isSilence()) {
                        silenceUpdateCallback.onDownloadFinish(file);
                    } else {
                        if (UpdateDialogFragment.this.isShowing()) {
                            getDialog().setCancelable(true);
                            mLoadingLL.setVisibility(View.GONE);
                            if (mAppBean.isConstraint()) {
                                showInstallBtn(file);
                            } else {
                                dismiss();
                                AppUpdateUtils.installApp(UpdateDialogFragment.this, file);
                            }
                        }
                    }
                    cancelDownloadService();
                }

                @Override
                public void onError(String msg) {
                    if (UpdateDialogFragment.this.isShowing()) {
                        getDialog().setCancelable(true);
                        cancelDownload();
                        cancelDownloadService();
                        dismissAllowingStateLoss();
                    }
                }
            });
        }
    }

    /**
     * 暂停下载
     */
    public void pauseDownload() {
        if (mDownloadBinder != null && mAppBean != null) {
            isPaused = true;
            mAppBean.getmHttpManager().pause(mAppBean.getApkDownloadUrl());
        }
    }

    /**
     * 继续下载
     */
    public void continueDownload() {
        if (mDownloadBinder != null && mAppBean != null) {
            isPaused = false;
            mAppBean.getmHttpManager().continueDownload(mAppBean.getApkDownloadUrl());
        }
    }

    /**
     * 取消下载
     */
    public void cancelDownload() {
        mAppBean.getmHttpManager().remove(mAppBean.getApkDownloadUrl());
    }

    public void cancelDownloadService() {
        if (mDownloadBinder != null) {
            mDownloadBinder.stop(conn);
        }
    }


    private void installApp() {
        if (AppUpdateUtils.appIsDownloaded(Objects.requireNonNull(this.getActivity()), mAppBean)) {   //已下载完成
            AppUpdateUtils.installApp(UpdateDialogFragment.this, AppUpdateUtils.getAppFile(mAppBean));
        } else {   //未下载
            mLlClose.setVisibility(View.GONE);
            downloadApp();
        }
    }

    /**
     * 开启后台服务下载
     */
    private void downloadApp() {
        if (TextUtils.isEmpty(mAppBean.getApkDownloadUrl())) {
            Toast.makeText(this.getActivity(), "下载路径错误", Toast.LENGTH_SHORT).show();
            return;
        }
        File appDir = new File(mAppBean.getApkDownloadPath());
        if (!appDir.exists()) {
            if (!appDir.mkdirs())
                return;
        }
        //apk所在地址：指定地址+版本号+apkName
        DownloadService.bindService(getActivity().getApplicationContext(), conn);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //升级
                installApp();
            } else {
                //提示，并且关闭
                Toast.makeText(getActivity(), TIPS, Toast.LENGTH_LONG).show();
                dismiss();
            }
        }

    }


    private void showInstallBtn(final File file) {
        mNumberProgressBar.setVisibility(View.GONE);
        mUpdateOkButton.setText("安装");
        mUpdateOkButton.setVisibility(View.VISIBLE);
        mUpdateOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdateUtils.installApp(UpdateDialogFragment.this, file);
            }
        });
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.e("", "对话框 requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
//        switch (resultCode) {
//            case Activity.RESULT_CANCELED:
//                switch (requestCode){
//                    // 得到通过UpdateDialogFragment默认dialog方式安装，用户取消安装的回调通知，以便用户自己去判断，比如这个更新如果是强制的，但是用户下载之后取消了，在这里发起相应的操作
//                    case AppUpdateUtils.REQ_CODE_INSTALL_APP:
//                        if (mAppBean.isConstraint()) {
//                            if (AppUpdateUtils.appIsDownloaded(Objects.requireNonNull(this.getActivity()),mAppBean)) {
//                                AppUpdateUtils.installApp(UpdateDialogFragment.this, AppUpdateUtils.getAppFile(mAppBean));
//                            }
//                        }
//                        break;
//                }
//                break;
//
//            default:
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }
}

