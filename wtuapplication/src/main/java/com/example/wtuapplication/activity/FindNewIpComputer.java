package com.example.wtuapplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wtuapplication.R;
import com.example.wtuapplication.adapter.MyGridViewAdapter;
import com.example.wtuapplication.bean.Constant;
import com.example.wtuapplication.utils.SamFile;
import com.example.wtuapplication.utils.ScanDeviceTool;
import com.example.wtuapplication.utils.ToastUtils;

import java.net.MalformedURLException;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class FindNewIpComputer extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private String TAG = "FindNewIpComputer";

    private Button btn_createNewIp;
    private Context mContext;
    private Toolbar toolbar;
    private String ip;
    public String userName;
    public String passWord;
    private Button btn_search;
    private TextView des;
    private GridView gridView;

    private ArrayList<String> scanList;
    private ProgressDialog mDialog;
    private AlertDialog mBuilder;
    public String mStrIp;
    private SmbFile[] fpTemp;
    private static int  flag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_new_ip_coputer);
        mContext = this;
        initView();
        initData();
        //设置toolbar栏
        setSupportActionBar(toolbar);
        //actionBar添加按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
           // actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }
    }

    //初始化控件数据
    private void initData() {
        btn_search.setOnClickListener(this);
       // btn_createNewIp.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
    }

    //初始化控件
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleMarginStart(250);
        //btn_createNewIp = (Button) findViewById(R.id.btn_createNewIp);
        btn_search = (Button) findViewById(R.id.btn_search);
        gridView = (GridView) findViewById(R.id.gridView);
    }

    /**
     * menu中控件的点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    /**
     * 获取返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE) {
               ip = data.getStringExtra("ip");
                //userName = data.getStringExtra("strUserName");
               // passWord = data.getStringExtra("strPassWord");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_createNewIp:
//                Intent intent = new Intent(mContext, CreateNewIp.class);
//                startActivityForResult(intent, Constant.REQUEST_CODE);
//                break;
            case R.id.btn_search:
                mDialog = new ProgressDialog(this);
                mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
                mDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
                mDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                mDialog.setIcon(R.mipmap.error);//
                mDialog.setTitle("提示");
                mDialog.setMessage("正在搜索...");
                // dismiss监听
                mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mDialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                return;
                            }
                        });
                mDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ScanDeviceTool scanDeviceTool = new ScanDeviceTool();
                        scanList = (ArrayList<String>) scanDeviceTool.scan();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (scanList.size() == 0) {
                                    mDialog.dismiss();
                                    ToastUtils.showToast(mContext, "未搜索到共享设备");
                                } else {
                                    mDialog.dismiss();
                                    MyGridViewAdapter myGridViewAdapter = new MyGridViewAdapter(mContext, scanList);
                                    gridView.setAdapter(myGridViewAdapter);
                                }
                            }
                        });
                    }
                }) {
                }.start();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * gridView的条目点击事件
     */

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mStrIp = scanList.get(i);
        SharedPreferences sharedPreferences = getSharedPreferences(mStrIp, MODE_APPEND);
        String userName = sharedPreferences.getString("userName", null);
        String passWord = sharedPreferences.getString("passWord", null);
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)){
            showOwnDialog();
        }else {
            startToUISelectActivity(userName,passWord);
        }

    }

    private void showOwnDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.user_data_item, null);
        mBuilder = new AlertDialog.Builder(this)
                .setTitle("用户登录")
                .setView(view)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText et_userName = (EditText) view.findViewById(R.id.username_edit);
                        EditText et_passWord = (EditText) view.findViewById(R.id.password_edit);
                        userName = et_userName.getText().toString().trim();
                        passWord = et_passWord.getText().toString().trim();
                        new Thread (new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    fpTemp = new SmbFile(new SamFile().returnUrl(mStrIp,userName,passWord)).listFiles();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)) {
                                                Toast.makeText(getApplicationContext(),"用户名密码不能为空",Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                if (fpTemp[0] ==null||fpTemp.length ==0)
                                                {
                                                    Toast.makeText(getApplicationContext(),"用户名密码错误,请重新输入",Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                else{
                                                    //将用户名和密码存储到sharedPreferences
                                                    SharedPreferences.Editor editor = getSharedPreferences(mStrIp, MODE_APPEND).edit();
                                                    editor.putString("userName",userName);
                                                    editor.putString("passWord",passWord);
                                                    editor.apply();
                                                    flag = 0;
                                                    startToUISelectActivity(userName,passWord);
                                                    Toast.makeText(getApplicationContext(),"登陆成功",Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        }
                                    });

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();}
                                catch (SmbException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mBuilder.dismiss();
                    }
                })
                .create();
        mBuilder.show();
    }

    private void startToUISelectActivity(String userName,String passWord) {
        Intent intent = new Intent(mContext, UISelect.class);
        String urlpath =  SamFile.returnUrl(mStrIp,userName,passWord);
        intent.putExtra("urlpath",urlpath);
        intent.putExtra("ip",mStrIp);
        startActivity(intent);
    }
}
