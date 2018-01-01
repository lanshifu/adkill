package com.lanshifu.adkill.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lanshifu.adkill.R;
import com.lanshifu.adkill.base.BaseTitleBarActivity;
import com.lanshifu.adkill.bean.Comment;
import com.lanshifu.adkill.utils.SystemUtil;
import com.lanshifu.adkill.utils.ToastUtil;
import com.lanshifu.adkill.view.CommRecyclerView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by lanshifu on 2017/12/23.
 */

public class CommentListActivity extends BaseTitleBarActivity {


    private static final int REQUEST_CODE_ADD = 10;
    @BindView(R.id.recycclerview)
    CommRecyclerView mCommRecyclerView;
    private BaseQuickAdapter<Comment, BaseViewHolder> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment_list;
    }

    @Override
    protected void initView() {
        setTitle("讨论区");
        mCommRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCommRecyclerView.setEnableLoadmore(false);
        mAdapter = new BaseQuickAdapter<Comment, BaseViewHolder>(R.layout.item_comment, new ArrayList<Comment>()) {
            @Override
            protected void convert(BaseViewHolder helper, Comment item) {
                helper.setText(R.id.tv_title, item.getTitle());
                helper.setText(R.id.tv_date, item.getUpdatedAt());
                helper.setText(R.id.tv_content, item.getContent());
                helper.setText(R.id.tv_status, item.getStatus());
                helper.setText(R.id.tv_user, "("+getUserName(item.getUserName())+")");
                switch (item.getStatus()){
                    case "置顶":
                        helper.setBackgroundRes(R.id.tv_status, R.drawable.bg_textview_cicle_red);
                        break;
                    case "新建":
                        helper.setBackgroundRes(R.id.tv_status, R.drawable.bg_textview_cicle_green);
                        break;
                }
            }
        };
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Comment comment = mAdapter.getData().get(position);
                Intent intent = new Intent(CommentListActivity.this, CommentDetailActivity.class);
                intent.putExtra(CommentDetailActivity.KEY_TITLE,comment.getTitle());
                intent.putExtra(CommentDetailActivity.KEY_NAME,comment.getUserName());
                intent.putExtra(CommentDetailActivity.KEY_CONTENT,comment.getContent());
                intent.putExtra(CommentDetailActivity.KEY_OBJECTID,comment.getObjectId());
                startActivity(intent);
            }
        });
        mCommRecyclerView.setAdapter(mAdapter);
        mCommRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadData();
            }


        });
        mCommRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mCommRecyclerView.autoRefresh();

    }

    private void loadData() {
        BmobQuery<Comment> query = new BmobQuery<Comment>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        //执行查询方法
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                handlerStatus(list);
                mCommRecyclerView.finishRefresh();
            }

            @Override
            public void onError(int i, String s) {
                mCommRecyclerView.finishRefresh();
                ToastUtil.showShort(s);
            }
        });
    }

    private void handlerStatus(List<Comment> list) {
        List<Comment> newList = new ArrayList<>();
        for (Comment comment : list) {
            if (comment.getStatus().equals("置顶")){
                newList.add(0,comment);
            } else {
                newList.add(comment);
            }

        }
        mAdapter.replaceData(newList);
    }

    private String getUserName(String name){
        String nickName = name;
        if (name.equals(SystemUtil.getSerialNumber())){
            return "我";
        }
        if (name.length() > 6){
            nickName = name.substring(0,2) + "****" + name.substring(name.length() -2 ,name.length());
        }
        return nickName;
    }

    @OnClick(R.id.iv_add)
    public void onViewClicked() {
       startActivityForResult(new Intent(this,AddCommentActivity.class),REQUEST_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE_ADD){
                mCommRecyclerView.autoRefresh();
            }
        }
    }
}
