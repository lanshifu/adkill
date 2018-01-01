package com.lanshifu.adkill;

import android.app.Application;
import android.content.Context;

import com.lanshifu.adkill.utils.ToastUtil;

import org.litepal.LitePal;

/**
 * Created by lanshifu on 2017/12/10.
 */

public class MainApplication extends Application {

    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        ToastUtil.init(getApplicationContext());
        LitePal.initialize(this);
    }

    public static Context getContext(){
        return mContext;
    }
}
