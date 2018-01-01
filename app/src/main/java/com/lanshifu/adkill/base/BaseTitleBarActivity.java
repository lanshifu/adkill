package com.lanshifu.adkill.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lanshifu.adkill.R;
import com.lanshifu.adkill.view.LoadingDialog;

import butterknife.ButterKnife;

/**
 * Created by lanshifu on 2017/12/23.
 */

public abstract class BaseTitleBarActivity extends AppCompatActivity{

    private TextView mTv_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.comm_titlebar);

        mTv_title = (TextView) findViewById(R.id.comm_toolbar_title);
        FrameLayout mFl_container = (FrameLayout) findViewById(R.id.fl_container);
        View view = getLayoutInflater().inflate(getLayoutId(),null);
        mFl_container.addView(view);
        ButterKnife.bind(this);
        findViewById(R.id.icon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackClick();
            }
        });

        initView();
        initData();
    }

    protected abstract  int getLayoutId();
    protected abstract  void initView();
    protected   void initData(){}

    protected void setTitle(String title){
        mTv_title.setText(title);
    }

    protected void BackClick(){
        finish();
    }


    /**
     * 开启浮动加载进度条
     */
    public void startProgressDialog() {
        LoadingDialog.showDialogForLoading(this);
    }

    /**
     * 开启浮动加载进度条
     *
     * @param msg
     */
    public void startProgressDialog(String msg) {
        LoadingDialog.showDialogForLoading(this, msg, true);
    }

    /**
     * 停止浮动加载进度条
     */
    public void stopProgressDialog() {
        LoadingDialog.cancelDialogForLoading();
    }


}
