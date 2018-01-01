package com.lanshifu.adkill.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lanshifu.adkill.KillADService;
import com.lanshifu.adkill.R;
import com.lanshifu.adkill.bean.KillAdDB;
import com.lanshifu.adkill.utils.BrocastUtil;
import com.lanshifu.adkill.utils.IconUtil;
import com.lanshifu.adkill.utils.LogUtil;
import com.lanshifu.adkill.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_SD = 10;
    private LinearLayout ll_open_or_close;
    private CheckBox checkBox;
    private RecyclerView recycclerview;
    private boolean mIsOpen;
    private BaseQuickAdapter<KillAdDB, BaseViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        refreshData();

        Bmob.initialize(this, "767611a082094fedc64a0633a4a8caa4");
        //初始化更新
//        BmobUpdateAgent.initAppVersion(this);
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        checkSdPermission();
    }

    private void checkSdPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            showNeedSdPermissionDialog();

        } else {
            BmobUpdateAgent.update(this);
        }
    }

    private void showNeedSdPermissionDialog() {
        new AlertDialog.Builder(this)
                .setMessage("为何方便版本更新,需申请SD卡权限,不同意将无法收到新版本通知")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_SD);
                    }
                }).show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SD: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    BmobUpdateAgent.update(this);
                } else {
                    ToastUtil.showShort("没有sd卡权限,将无法收到版本更新通知");
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsOpen = isAccessibilitySettingsOn(this);
        checkBox.setChecked(mIsOpen);
    }

    private void initView() {
        ll_open_or_close = (LinearLayout) findViewById(R.id.ll_open_or_close);
        ll_open_or_close.setOnClickListener(this);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        recycclerview = (RecyclerView) findViewById(R.id.recycclerview);
        checkBox.setOnClickListener(this);
        mAdapter = new BaseQuickAdapter<KillAdDB, BaseViewHolder>(R.layout.item_kill_appinfo) {
            @Override
            protected void convert(BaseViewHolder helper, final KillAdDB item) {
                helper.setImageDrawable(R.id.imgApp, IconUtil.byteToDrawable(item.getIcon_base64()));
                helper.setText(R.id.apkName, item.getAppLabel());
                helper.setText(R.id.pkgName, item.getPkgName());
                helper.setText(R.id.apkVersion, item.getmVersion());
                helper.setText(R.id.first_act, item.getFirstActivityName());
                helper.getView(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pkgName = item.getPkgName();
                        deleteFromDb(pkgName);
                    }
                });
                helper.getView(R.id.btnUpdate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        KillAdDB killAdDB = item;
                        showUpdateDbDialog(killAdDB);
                    }
                });
            }

        };
        recycclerview.setLayoutManager(new LinearLayoutManager(this));
        recycclerview.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recycclerview.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }



    private static final int REQUEST_CODE_APP_LIST = 1;

    private void openOrClose() {
        String settings = Settings.ACTION_ACCESSIBILITY_SETTINGS;
        startActivity(new Intent(settings));
        ToastUtil.showLong("找到 "+getResources().getString(R.string.app_name)+" 然后打开开关即可使用");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_APP_LIST) {
            //添加了记录,更新
            refreshData();
        }
    }

    private void refreshData() {
        List<KillAdDB> all = DataSupport.findAll(KillAdDB.class);
        mAdapter.replaceData(all);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_open_or_close:
            case R.id.checkBox:
                openOrClose();
                break;
        }
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        // KillADService为对应的服务
        final String service = getPackageName() + "/" + KillADService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("lanshifu", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    Log.v("lanshifu", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v("lanshifu", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("lanshifu", "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }

    private void deleteFromDb(String packageName) {
        int delete = DataSupport.deleteAll(KillAdDB.class, "pkgName like ?", packageName);
        BrocastUtil.sendUpdateDBBroccast();
        refreshData();
    }

    // 当菜单被选中时调用
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            showAboutMeDialog();
        } else if (item.getItemId() == R.id.action_add) {
            startActivityForResult(new Intent(this, AppListActivity.class), REQUEST_CODE_APP_LIST);
        }else if (item.getItemId() == R.id.action_setting) {
            startActivity(new Intent(this,SettingActivity.class));
        }else if (item.getItemId() == R.id.comment) {
            startActivity(new Intent(this,CommentListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    private void showAboutMeDialog(){
        showInfoDialog("关于作者",getResources().getString(R.string.about_me));
    }

    private void showUserDialog(){
        showInfoDialog("使用方法",getResources().getString(R.string.use));

    }

    private void showNoticeDialog(){
        showInfoDialog("注意",getResources().getString(R.string.notice));
    }

    private void showInfoDialog(String title,String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }


    private void showUpdateDbDialog(final KillAdDB killAdDB) {
        View view = View.inflate(this ,R.layout.layout_update_db,null);
        final TextView et_app = (TextView) view.findViewById(R.id.et_app);
        final EditText et_package = (EditText) view.findViewById(R.id.et_package);
        final  EditText et_text = (EditText) view.findViewById(R.id.et_text);
        et_app.setText(killAdDB.getAppLabel());
        et_package.setText(killAdDB.getFirstActivityName());
        et_text.setText(killAdDB.getText());
        new AlertDialog.Builder(this)
                .setTitle("修改")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        KillAdDB db = killAdDB;
                        db.setFirstActivityName(et_package.getText().toString());
                        db.setText(et_text.getText().toString());
                        db.save();
                        ToastUtil.showShort("保存成功");
                    }
                })
                .setNegativeButton("取消",null).show();
    }

}
