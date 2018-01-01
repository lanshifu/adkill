package com.lanshifu.adkill.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lanshifu.adkill.R;
import com.lanshifu.adkill.bean.AppInfo;
import com.lanshifu.adkill.bean.KillAdDB;
import com.lanshifu.adkill.utils.BrocastUtil;
import com.lanshifu.adkill.utils.IconUtil;
import com.lanshifu.adkill.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by lanshifu on 2017/12/10.
 */

public class AppListActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    ProgressBar progressBar;
    private List<AppInfo> mlistAppInfo = new ArrayList<>();
    private BaseQuickAdapter<AppInfo, BaseViewHolder> mAdapter;
    private List<AppInfo> mAppInfos;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layout_app_list);
        initView();
        initData();
    }

    private void initView() {
        TextView tv_title = (TextView) findViewById(R.id.comm_toolbar_title);
        tv_title.setText("请选择要去广告的应用");
        findViewById(R.id.icon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recycclerview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAdapter = new BaseQuickAdapter<AppInfo, BaseViewHolder>(R.layout.item_appinfo, new ArrayList<AppInfo>()) {
            @Override
            protected void convert(BaseViewHolder helper, AppInfo item) {
                helper.setImageDrawable(R.id.imgApp, item.getAppIcon());
                helper.setText(R.id.apkName, item.getAppLabel());
                helper.setText(R.id.pkgName, item.getPkgName());
                helper.setText(R.id.apkSize, item.getTotalSize());
                helper.setText(R.id.apkVersion, item.getmVersion());
                helper.setText(R.id.sigmd5, item.getSigmd5());
                helper.setText(R.id.first_act, item.getFirstActivityName());
            }
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AppListActivity.this.onItemClick(mAdapter.getItem(position));
            }
        });

    }


    private void initData() {
        new Thread() {
            @Override
            public void run() {
                mAppInfos = queryAppInfo();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        mAdapter.replaceData(mAppInfos);
                    }
                });
            }
        }.start();
    }

    private void onItemClick(AppInfo appInfo) {
        //写到数据库
        KillAdDB first = DataSupport.where("pkgName like ?", appInfo.getPkgName())
                .findFirst(KillAdDB.class);
        if (first != null) {
            ToastUtil.showShort(appInfo.getAppLabel() + "已经添加过了");
            return;
        }

        KillAdDB db = new KillAdDB();
        db.setAppLabel(appInfo.getAppLabel());
        db.setPkgName(appInfo.getPkgName());
        db.setFirstActivityName(appInfo.getFirstActivityName());
        db.setViewId(-1);
        db.setText("跳");
        db.setCount(0);
        db.setmVersion(appInfo.getmVersion());
        db.setIcon_base64(IconUtil.drawableToByte(appInfo.getAppIcon()));
        boolean save = db.save();
        BrocastUtil.sendUpdateDBBroccast();
        setResult(RESULT_OK);
        finish();
    }

    // 获得所有启动Activity的信息，类似于Launch界面
    public List<AppInfo> queryAppInfo() {
        PackageManager pm = this.getPackageManager(); // 获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        if (mlistAppInfo != null) {
            mlistAppInfo.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名

                String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
                // 为应用程序的启动Activity 准备Intent
                Intent launchIntent = new Intent();
                launchIntent.setComponent(new ComponentName(pkgName, activityName));
                // 创建一个AppInfo对象，并赋值
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(appLabel);
                appInfo.setPkgName(pkgName);
                appInfo.setAppIcon(icon);
                appInfo.setFirstActivityName(activityName);
                appInfo.setmVersion(getVersionName(pkgName));
                appInfo.setSigmd5(getSignMd5Str(pkgName));
                mlistAppInfo.add(appInfo); // 添加至列表中
            }
            return mlistAppInfo;
        }
        return null;
    }

    private String getVersionName(String packageName) {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = "  版本名: " + packInfo.versionName + "  版本号: " + packInfo.versionCode;
        return version != null ? version : "未获取到系统版本号";
    }

    /**
     * 获取app签名md5值
     */
    public String getSignMd5Str(String packageName) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = encryptionMD5(sign.toByteArray());
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * MD5加密
     *
     * @param byteStr 需要加密的内容
     * @return 返回 byteStr的md5值
     */
    public static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }


}
