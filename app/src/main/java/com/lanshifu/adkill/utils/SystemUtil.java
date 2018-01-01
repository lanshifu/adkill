package com.lanshifu.adkill.utils;

import java.lang.reflect.Method;

/**
 * Created by lanshifu on 2017/12/25.
 */

public class SystemUtil {

    public static String getSerialNumber(){
        String serial = "null";
        try {
            Class<?> c =Class.forName("android.os.SystemProperties");
            Method get =c.getMethod("get", String.class);
            serial = (String)get.invoke(c, "ro.serialno");

        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
        return serial;

    }
}
