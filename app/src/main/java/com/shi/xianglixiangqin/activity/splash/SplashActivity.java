package com.shi.xianglixiangqin.activity.splash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.shi.xianglixiangqin.R;
import com.shi.xianglixiangqin.activity.GuideActivity;
import com.shi.xianglixiangqin.activity.LoginActivity;
import com.shi.xianglixiangqin.activity.MainActivity;
import com.shi.xianglixiangqin.activity.MyBaseActivity;
import com.shi.xianglixiangqin.bean.ApkVersionDataBean;
import com.shi.xianglixiangqin.util.InformationCodeUtil;
import com.shi.xianglixiangqin.util.SnackBarUtil;
import com.shi.xianglixiangqin.view.FragmentOkDialog;
import com.shi.xianglixiangqin.view.FragmentViewDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 闪屏页面
 *
 * @author SHI
 * @time 2016/8/8 17:50
 */
public class SplashActivity extends MyBaseActivity {

    /**
     * 加载进度条
     **/
    @BindView(R.id.iv_splash_loading)
    ImageView iv_splash_loading;
    /**
     * 逻辑业务控制器
     **/
    private SplashActivityController controller;

    private ProgressDialog progressDialog;

    @Override
    public void initView() {
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        if (InformationCodeUtil.AppName_DaiNiFei.equals(getResources().getText(R.string.app_name))) {
            controller = new SplashActivityControllerImpDaiNiFei(SplashActivity.this);
        } else {
            controller = new SplashActivityControllerImpJvHeAndPiFa(SplashActivity.this);
        }
        controller.init();
    }

    @Override
    public void initData() {
        Animation translate = AnimationUtils.loadAnimation(this,
                R.anim.splash_loading);
        iv_splash_loading.setAnimation(translate);

    }

    public void startServiceForAppUpdate() {
//        /**调用系统自带浏览器直接下载文件**/
//		Uri uri = Uri.parse(mReleaseModel.getAddress());
//		Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
//		startActivity(downloadIntent);
//        /**调用系统自带浏览器进入下载界面**/
//        String url_appInfo = "http://a.app.qq.com/o/simple.jsp?pkgname="+getApplicationInfo().processName;
////      String url_appInfo = "http://www.dainif.com/app/appInfo/appInfo.html?" +
////      "appName=" + getResources().getString(R.string.app_name);
//        Uri uri = Uri.parse(url_appInfo);
//		Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(downloadIntent);
//        //开启服务下载
//      Intent intent = new Intent(mContext, UpdateAppService.class);
//      intent.putExtra(InformationCodeUtil.IntentServiceAddressOfApkDownload, addressOfApkDownload);
//      startService(intent);
    }

    public void showMessage(String msg) {
        SnackBarUtil.show(iv_splash_loading, msg);
    }


    public void showDowningProgress(int currentProgress, int maxProgress) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("下载中...");
            progressDialog.setMax(maxProgress);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            return;
        }
        if (currentProgress != maxProgress) {
            progressDialog.setProgress(currentProgress);
            return;
        }
        progressDialog.dismiss();
    }

    public void install(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        } else {
            Uri uri = FileProvider.getUriForFile(mContext, getPackageName() + ".activity.splash.updatefileprovider", file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(0);
    }


    public void showUpdateDialog(final ApkVersionDataBean apkBean) {
        final FragmentViewDialog updateDialog = new FragmentViewDialog();

        View view = View.inflate(mContext, R.layout.dialog_version_update_tip, null);
        TextView tv_newVersionName = (TextView) view.findViewById(R.id.tv_newVersionName);
        TextView tv_newVersionSize = (TextView) view.findViewById(R.id.tv_newVersionSize);
        TextView tv_newVersionDesc = (TextView) view.findViewById(R.id.tv_newVersionDesc);

        tv_newVersionName.setText("最新版本：" + apkBean.getVersionName());
        tv_newVersionSize.setText("新版本大小：" + apkBean.getFileSize().desc);
        tv_newVersionDesc.setText("更新内容：" + apkBean.getNewFeature());
        String strCancel = "以后再说";
        String strOk = "立即更新";

        updateDialog.initView(view, strCancel, strOk, new FragmentViewDialog.OnButtonClickListener() {
            @Override
            public void OnOkClick() {
                controller.queryToDownApp(apkBean);
            }

            @Override
            public void OnCancelClick() {
                controller.expiredVerificationLogin();
            }
        });

        updateDialog.show(getSupportFragmentManager(), "FragmentViewDialog");
    }

    public void showMustUpdateDialog(final ApkVersionDataBean apkBean) {
        final FragmentOkDialog updateDialog = new FragmentOkDialog();

        String strTitle = "应用更新";
        String strMessage = "您需要更新应用才能继续使用\n\n最新版本：" + apkBean.getVersionName()
                + "\n新版本大小：" + apkBean.getFileSize().desc
                + "\n\n更新内容：\n" + apkBean.getNewFeature();
        String strOk = "确定";

        updateDialog.initView(strTitle, strMessage, strOk, new FragmentOkDialog.OnButtonClickListener() {
            @Override
            public void OnOkClick() {
                controller.queryToDownApp(apkBean);
            }

            @Override
            public void OnCancelClick() {
                System.exit(0);
            }
        });

        updateDialog.show(getSupportFragmentManager(), "FragmentViewDialog");
    }

    public void toGuideView() {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
        finish();
    }

    public void toLoginView() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void toMainView() {
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}