package com.lanshifu.adkill.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by lanshifu on 2017/12/10.
 */

public class KillAdDB extends DataSupport {

    private String appLabel;
    private String pkgName;
    private String firstActivityName;
    private int viewId;
    private String text;
    private int count;
    private String mVersion;
    private String icon_base64;

    public String getmVersion() {
        return mVersion;
    }

    public void setmVersion(String mVersion) {
        this.mVersion = mVersion;
    }



    public String getIcon_base64() {
        return icon_base64;
    }

    public void setIcon_base64(String icon_base64) {
        this.icon_base64 = icon_base64;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getFirstActivityName() {
        return firstActivityName;
    }

    public void setFirstActivityName(String firstActivityName) {
        this.firstActivityName = firstActivityName;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
