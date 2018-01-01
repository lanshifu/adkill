package com.lanshifu.adkill.ui;

import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;

import com.lanshifu.adkill.R;
import com.lanshifu.adkill.base.BaseTitleBarActivity;
import com.lanshifu.adkill.utils.UserUtil;
import com.lanshifu.adkill.view.CommRecyclerView;
import com.lanshifu.adkill.view.RxHeartLayout;

import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lanshifu on 2017/12/25.
 */

public class CommentDetailActivity extends BaseTitleBarActivity {
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.tv_hv)
    TextView mTvHv;
    @BindView(R.id.heart_layout)
    RxHeartLayout mHeartLayout;
   @BindView(R.id.recycclerview)
   CommRecyclerView mCommRecyclerView;

    public static final String KEY_TITLE = "key_title";
    public static final String KEY_CONTENT = "key_content";
    public static final String KEY_NAME = "key_name";
    public static final String KEY_OBJECTID = "objectId";
    private Random random = new Random();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment_detail;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        String title = intent.getStringExtra(KEY_TITLE);
        String content = intent.getStringExtra(KEY_CONTENT);
        String name = intent.getStringExtra(KEY_NAME);
        String objectid = intent.getStringExtra(KEY_OBJECTID);
        setTitle(title);
        mTvName.setText(UserUtil.getUserName(name));
        mTvTitle.setText(title);
        mTvContent.setText(content);

    }


    @OnClick(R.id.love)
    public void onViewClicked() {
        mHeartLayout.post(new Runnable() {
            @Override
            public void run() {
                int rgb = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                mHeartLayout.addHeart(rgb);
            }
        });
    }
}
