package com.lanshifu.adkill.utils;

/**
 * Created by lanshifu on 2018/1/1.
 */

public class UserUtil {

    public static String getUserName(String name){
        String nickName = name;
        if (name.equals(SystemUtil.getSerialNumber())){
            return "我";
        }
        if (name.length() > 6){
            nickName = name.substring(0,2) + "****" + name.substring(name.length() -2 ,name.length());
        }
        return nickName;
    }
}
