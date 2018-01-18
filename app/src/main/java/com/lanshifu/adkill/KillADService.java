package com.lanshifu.adkill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.accessibility.AccessibilityEvent;

import com.lanshifu.adkill.base.BaseAccessibilityService;
import com.lanshifu.adkill.bean.KillAdDB;
import com.lanshifu.adkill.utils.BrocastUtil;
import com.lanshifu.adkill.utils.LogUtil;
import com.lanshifu.adkill.utils.SPUtil;
import com.lanshifu.adkill.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lanshifu on 2017/12/10.
 */

public class KillADService extends BaseAccessibilityService {


    private static final String TAG = "lanshifu";
    private List<KillAdDB> mKillAdDBList = new ArrayList<>();
    private List<String> mPackageNameList = new ArrayList<>();
    private Map<String, KillAdDB> mPackageNameMap = new HashMap<String, KillAdDB>();
    private boolean isPass = true;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        String pkgName = event.getPackageName().toString();
        String className = event.getClassName().toString();

        boolean contains = mPackageNameList.contains(pkgName);
        if (!contains) {
            return;
        }

        KillAdDB db = mPackageNameMap.get(pkgName);
        String firstActivityName = db.getFirstActivityName();
        LogUtil.d("className " + className);
        boolean equals = firstActivityName.equals(className);
        if (equals) {
            isPass = false;
        } else {
            if (isPass) {
                return;
            }
        }
        String text = db.getText();
        boolean click = clickTextViewByText(text);
        if (click) {
            isPass = true;
            db.setCount(db.getCount() +1);
            db.save();
            if (SPUtil.getInstance().getBoolean(SPUtil.KEY_SHOW_PASS_TOAST, true)){
                ToastUtil.showShort("已跳过首页广告");
            }
        }

    }

    @Override
    public void onInterrupt() {
        LogUtil.d("onInterrupt");
        ToastUtil.showShort("去广告功能已关闭");
        unRegisterBrocast();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        ToastUtil.showShort("去广告功能已关闭");
        unRegisterBrocast();
        return super.onUnbind(intent);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        ToastUtil.showShort("去广告功能已启动,赶紧体验吧");
        registerBrocast();
        refreshData();
    }

    private void registerBrocast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BrocastUtil.ACCTION_UPDATE);
        MainApplication.getContext().registerReceiver(receiver, filter);
    }

    private void unRegisterBrocast() {
        MainApplication.getContext().unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BrocastUtil.ACCTION_UPDATE)) {
                //数据库更新
//                LogUtil.d("收到通知,查询数据库");
                refreshData();
            }

        }


    };

    private void refreshData() {
        mKillAdDBList.clear();
        mKillAdDBList = DataSupport.findAll(KillAdDB.class);
        mPackageNameList.clear();
        mPackageNameMap.clear();

        for (KillAdDB db : mKillAdDBList) {
            mPackageNameList.add(db.getPkgName());
            mPackageNameMap.put(db.getPkgName(), db);
        }
        LogUtil.d("更新完成  " + mPackageNameList.size());
    }


}
