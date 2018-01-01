package com.lanshifu.adkill.ui;

import android.text.TextUtils;
import android.widget.EditText;

import com.lanshifu.adkill.R;
import com.lanshifu.adkill.base.BaseTitleBarActivity;
import com.lanshifu.adkill.bean.Comment;
import com.lanshifu.adkill.utils.ToastUtil;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;

import static com.lanshifu.adkill.utils.SystemUtil.getSerialNumber;

/**
 * Created by lanshifu on 2017/12/24.
 */

public class AddCommentActivity extends BaseTitleBarActivity {
    @BindView(R.id.et_title)
    EditText mEtTitle;
    @BindView(R.id.et_content)
    EditText mEtContent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_comment;
    }

    @Override
    protected void initView() {
        setTitle("新建");

    }

    @OnClick(R.id.btn_commit)
    public void onViewClicked() {
        if (TextUtils.isEmpty(mEtTitle.getText().toString().trim())){
            ToastUtil.showShort("标题不能为空");
            return;
        }else if (TextUtils.isEmpty(mEtContent.getText().toString().trim())){
            ToastUtil.showShort("内容不能为空");
            return;
        }

        commmit();
    }

    private void commmit() {
        startProgressDialog();
        Comment comment = new Comment();
        comment.setTitle(mEtTitle.getText().toString());
        comment.setContent(mEtContent.getText().toString());
        comment.setStatus("新建");
        comment.setUserName(getSerialNumber());
        comment.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                stopProgressDialog();
                ToastUtil.showShort("发布成功");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                stopProgressDialog();
                ToastUtil.showShort("失败" +s);
            }
        });

    }


}
