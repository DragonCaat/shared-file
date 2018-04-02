package com.example.wtuapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
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
import com.example.wtuapplication.adapter.GridViewShowFileAdapter;
import com.example.wtuapplication.bean.Constant;
import com.example.wtuapplication.utils.SamFile;

import java.net.MalformedURLException;
import java.net.Socket;
import java.util.ArrayList;

import static com.example.wtuapplication.R.id.btn_sendFile;
import static com.example.wtuapplication.R.id.gridView;
import static com.example.wtuapplication.R.id.username_edit;
import static com.example.wtuapplication.R.mipmap.set;
import static com.example.wtuapplication.R.string.findNewComputer;
import static com.example.wtuapplication.R.string.userName;
/**
 * Created by wang on 2017/9/22.
 */

public class ShowFileActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener {
    private String TAG = "ShowFileActivity";

    private String own_urlPath;
    private String fa_urlpath;
    private GridView mGridView_showFile;
    private Toolbar toolbar;
    private Context mContext;
   private ArrayList<String> listDir;
    private String fileName;//准备点击的文件夹名字
    public static final int PORT = 8001;//服务器端口号
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());
        setContentView(R.layout.activity_show_file);
        Intent intent = getIntent();
        mContext = this;
        fa_urlpath =intent.getStringExtra("urlpath");//上一级的路径
        initView();
        initData();

        //"smb://cj:cj@192.168.16.100/cj share";
        //urlPath = "smb://cj:cj@" + mStrIp + "/cj share";//zhe lu jing zenme xiesile ?
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleMarginStart(250);
        //设置toolbar栏
        setSupportActionBar(toolbar);
        //actionBar添加按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);

            new Thread() {
                @Override
                public void run() {
                    try {
                         listDir = SamFile.listDir(fa_urlpath);//遍历上一级路径文件夹点击获取本文件的文件夹名称
                       //  fileList = SamFile.listDir(mStrIp,mUserName,mPassWord);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GridViewShowFileAdapter gridViewShowFileAdapter = new GridViewShowFileAdapter(ShowFileActivity.this, listDir);
                                mGridView_showFile.setAdapter(gridViewShowFileAdapter);
                            }
                        });
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
    public void initView()
    {
        mGridView_showFile = (GridView) findViewById(R.id.gridView_showFile);
    }
    public void initData()
    {
        mGridView_showFile.setOnItemClickListener(this);
    }


    protected void onStart() {
        super.onStart();
    }

    /**
     * gridView的条目点击事件
     */
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (listDir.get(i)!=null)
            {
                fileName =listDir.get(i);
                startToOpenFileActivity(fileName);
            }
    }

    private void startToOpenFileActivity(String filename) {
        Intent intent = new Intent(mContext, OpenFileActivity.class);
        own_urlPath = SamFile.returnUrl_(fa_urlpath,filename);
        intent.putExtra("urlpath",own_urlPath);
        startActivity(intent);
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
}
