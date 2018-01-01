package com.lanshifu.adkill.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lanshifu.adkill.R;
import com.lanshifu.adkill.utils.SPUtil;

/**
 * Created by lanshifu on 2017/12/21.
 */

public class SettingActivity extends AppCompatActivity {

    private CheckBox mCb_show_pass_toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_setting);
        setTitle("设置");

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mCb_show_pass_toast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtil.getInstance().putBoolean(SPUtil.KEY_SHOW_PASS_TOAST,isChecked);
            }
        });
    }

    private void initData() {
        mCb_show_pass_toast.setChecked(SPUtil.getInstance().getBoolean(SPUtil.KEY_SHOW_PASS_TOAST,true));
    }

    private void initView() {
        mCb_show_pass_toast = (CheckBox) findViewById(R.id.cb_show_pass_toast);
        TextView tv_title = (TextView) findViewById(R.id.comm_toolbar_title);
        tv_title.setText("设置");
        findViewById(R.id.icon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
