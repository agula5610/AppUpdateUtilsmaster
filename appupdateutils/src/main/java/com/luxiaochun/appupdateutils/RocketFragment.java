package com.luxiaochun.appupdateutils;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
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

import com.luxiaochun.appupdateutils.common.AppUpdateBean;
import com.luxiaochun.appupdateutils.common.UpdateType;
import com.luxiaochun.appupdateutils.downloadutils.DownloadUtils;
import com.luxiaochun.appupdateutils.downloadutils.RocketDownloadUtils;
import com.luxiaochun.appupdateutils.downloadutils.SlienceDownloadUtils;
import com.luxiaochun.appupdateutils.utils.AppUpdateUtils;
import com.luxiaochun.appupdateutils.utils.ColorUtil;
import com.luxiaochun.appupdateutils.utils.DrawableUtil;
import com.luxiaochun.appupdateutils.view.NumberProgressBar;

import java.io.File;
import java.util.Objects;

public class RocketFragment extends DialogFragment implements View.OnClickListener {
    public static final String TIPS = "请授权访问存储空间权限，否则App无法更新";

    private AppUpdateBean mAppBean;

    private TextView mContentTv;
    private Button mUpdateOkButton;
    private NumberProgressBar mNumberPb;
    private ImageView mCloseIv;
    private TextView mTitleTv;
    private LinearLayout mLoadingLL;
    private Button mCancelBtn;
    private Button mPauseContinueBtn;
    private LinearLayout mLlClose;
    private ImageView mTopIv;
    private TextView mIgnore;

    //默认色
    private int mDefaultColor = 0xffe94339;
    private int mDefaultPicResId = R.drawable.lib_update_app_top_bg;

    private long apkTotalSize = 0;

    private DownloadUtils downloadUtils;

    public static RocketFragment newInstance(Bundle args) {
        RocketFragment fragment = new RocketFragment();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lib_update_app_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        //提示内容
        mContentTv = view.findViewById(R.id.tv_update_info);
        //标题
        mTitleTv = view.findViewById(R.id.tv_title);
        //更新按钮
        mUpdateOkButton = view.findViewById(R.id.btn_ok);
        //进度条
        mNumberPb = view.findViewById(R.id.npb);
        //取消和暂停按钮布局
        mLoadingLL = view.findViewById(R.id.ll_loading);
        //取消下载
        mCancelBtn = view.findViewById(R.id.btn_cancel);
        //暂停、继续下载
        mPauseContinueBtn = view.findViewById(R.id.btn_pause_continue);
        //关闭按钮
        mCloseIv = view.findViewById(R.id.iv_close);
        //关闭按钮+线 的整个布局
        mLlClose = view.findViewById(R.id.ll_close);
        //顶部图片
        mTopIv = view.findViewById(R.id.iv_top);
        //忽略
        mIgnore = view.findViewById(R.id.tv_ignore);
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
                    if (mAppBean != null && UpdateType.Force == mAppBean.getType()) {
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window dialogWindow = getDialog().getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        lp.height = (int) (displayMetrics.heightPixels * 0.6f);
        lp.width = (int) (displayMetrics.widthPixels * 0.76f);
        dialogWindow.setAttributes(lp);
        initData();
    }

    private void initData() {
        mAppBean = (AppUpdateBean) getArguments().getSerializable(AppUpdateManager.TAG);
        if (UpdateType.Slience == mAppBean.getType()) {
            downloadUtils = new SlienceDownloadUtils(this.getContext(),mAppBean);
        } else {
            downloadUtils = new RocketDownloadUtils(this,mAppBean);
        }

        //设置主题色
        initTheme();
        if (mAppBean != null) {
            //弹出对话框
            final String title = mAppBean.getTitle();
            final String version = mAppBean.getVersion();
            final String apkSize = mAppBean.getApkSize();
            final String notes = mAppBean.getNotes();

            String msg = "";

            if (!TextUtils.isEmpty(apkSize)) {
                msg = "新版本大小：" + apkSize + "\n\n";
            }

            if (!TextUtils.isEmpty(notes)) {
                msg += notes;
            }

            //更新内容
            mContentTv.setText(msg);
            //标题
            mTitleTv.setText(TextUtils.isEmpty(title) ? String.format("是否升级到%s版本？", version) : title);
            //强制更新
            if (UpdateType.Force == mAppBean.getType()) {
                mLlClose.setVisibility(View.GONE);
                mCancelBtn.setVisibility(View.GONE);
            }

            if (UpdateType.Hint == mAppBean.getType()) {
                mUpdateOkButton.setVisibility(View.GONE);
            }
            // 判断是否已下载
            if (AppUpdateUtils.appIsDownloaded(Objects.requireNonNull(this.getActivity()), mAppBean)) {
                showInstallBtn(AppUpdateUtils.getAppFile(mAppBean));
            }

            initClickEvents();
        }
    }

    /**
     * 初始化主题色
     */
    private void initTheme() {
        final int color = mAppBean.getThemeColor();
        final int topResId = mAppBean.getTopPic();

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
        mUpdateOkButton.setBackground(DrawableUtil.getDrawable(AppUpdateUtils.dp2px(4, getActivity()), color));
        mCancelBtn.setBackground(DrawableUtil.getDrawable(AppUpdateUtils.dp2px(4, getActivity()), color));
        mPauseContinueBtn.setBackground(DrawableUtil.getDrawable(AppUpdateUtils.dp2px(4, getActivity()), color));
        mNumberPb.setProgressTextColor(color);
        mNumberPb.setReachedBarColor(color);
        //随背景颜色变化
        mUpdateOkButton.setTextColor(ColorUtil.isTextColorDark(color) ? Color.BLACK : Color.WHITE);
        mCancelBtn.setTextColor(ColorUtil.isTextColorDark(color) ? Color.BLACK : Color.WHITE);
        mPauseContinueBtn.setTextColor(ColorUtil.isTextColorDark(color) ? Color.BLACK : Color.WHITE);
    }

    private void initClickEvents() {
        mUpdateOkButton.setOnClickListener(this);
        mCloseIv.setOnClickListener(this);
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
                downloadApp();
            }
        } else if (i == R.id.btn_cancel) {
            downloadUtils.cancelDownload();
            downloadUtils.cancelDownloadService();
            dismiss();
        } else if (i == R.id.btn_pause_continue) {
                if (downloadUtils.isPaused()) {
                    downloadUtils.continueDownload();
                } else {
                    downloadUtils.pauseDownload();
                }
        } else if (i == R.id.iv_close) {
            dismiss();
        } else if (i == R.id.tv_ignore) {
            AppUpdateUtils.saveIgnoreVersion(getActivity(), mAppBean.getVersion());
            dismiss();
        }
    }

    private void downloadApp() {
        if (AppUpdateUtils.appIsDownloaded(Objects.requireNonNull(this.getActivity()), mAppBean)) {   //已下载完成
            AppUpdateUtils.installApp(RocketFragment.this, AppUpdateUtils.getAppFile(mAppBean));
        } else {   //未下载
            mLlClose.setVisibility(View.GONE);
            if (UpdateType.Slience == mAppBean.getType()) {
                dismiss();
            }
            downloadUtils.download();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //升级
                downloadApp();
            } else {
                //提示，并且关闭
                Toast.makeText(getActivity(), TIPS, Toast.LENGTH_LONG).show();
                dismiss();
            }
        }

    }

    private void showInstallBtn(final File file) {
        mNumberPb.setVisibility(View.GONE);
        mUpdateOkButton.setText("安装");
        mUpdateOkButton.setVisibility(View.VISIBLE);
        mUpdateOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdateUtils.installApp(RocketFragment.this, file);
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
//                                AppUpdateUtils.downloadApp(UpdateDialogFragment.this, AppUpdateUtils.getAppFile(mAppBean));
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

    public void onRocketStart() {
        if (isShowing()) {
            getDialog().setCancelable(false);
            mNumberPb.setVisibility(View.VISIBLE);
            mUpdateOkButton.setVisibility(View.GONE);
            mLoadingLL.setVisibility(View.VISIBLE);
        }
    }

    public void onRocketProgress(float progress, long totalSize) {
        if (isShowing()) {
            mNumberPb.setProgress(Math.round(progress * 100));
            mNumberPb.setMax(100);
            if (apkTotalSize == 0 && totalSize > 0) {
                AppUpdateUtils.saveApkSize(Objects.requireNonNull(RocketFragment.this.getActivity()), totalSize);
                apkTotalSize = totalSize;
            }
        }
    }

    public void onRocketFinish(File file) {
        if (isShowing()) {
            getDialog().setCancelable(true);
            mLoadingLL.setVisibility(View.GONE);
            if (UpdateType.Force == mAppBean.getType()) {
                showInstallBtn(file);
            } else {
                dismiss();
            }
            AppUpdateUtils.installApp(RocketFragment.this, file);
        }
    }

    public void onRocketError(String msg) {
        if (isShowing()) {
            getDialog().setCancelable(true);
            dismissAllowingStateLoss();
        }
    }

    public void onRocketPause() {
        mPauseContinueBtn.setText("继续");
    }

    public void onRocketContinue() {
        mPauseContinueBtn.setText("暂停");
    }
}

