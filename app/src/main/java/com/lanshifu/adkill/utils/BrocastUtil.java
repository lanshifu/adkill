package com.lanshifu.adkill.utils;

import android.content.Intent;

import com.lanshifu.adkill.MainApplication;

/**
 * Created by lanshifu on 2017/12/10.
 */

public class BrocastUtil {

    public static final String ACCTION_UPDATE= "com.lanshifu.update_db";

    public static void sendUpdateDBBroccast(){
        Intent intent = new Intent();
        intent.setAction(ACCTION_UPDATE);
        MainApplication.getContext().sendBroadcast(intent);

    }
}
