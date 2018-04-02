package com.example.wtuapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.wtuapplication.R;
import com.example.wtuapplication.adapter.GridViewOpenFileAdapter;
import com.example.wtuapplication.adapter.GridViewShowFileAdapter;
import com.example.wtuapplication.bean.Constant;
import com.example.wtuapplication.utils.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import static android.R.id.list;
import static com.example.wtuapplication.R.id.btn_sendFile;

public class OpenFileActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    private String TAG = "OpenFileActivity";

    private String own_urlPath;//点击后进入下一级文件夹路径
    private String fa_urlpath;//当前进入文件夹路径
    private GridView mGridView_OpenFile;
    private Toolbar toolbar;
    private Context mContext;
    private Button mBtn_sendFile;
    private String mFilePath;
    private  String fileName;//准备点击的文件夹名字
     private ArrayList<String> listDir;
    private AlertDialog mBuilder;
    private ProgressDialog mDialog;
    private boolean flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);
        Intent intent = getIntent();
        mContext = this;
        fa_urlpath =intent.getStringExtra("urlpath");//当前路径
        mGridView_OpenFile = (GridView) findViewById(R.id.gridView_showopenFile);
        mGridView_OpenFile.setOnItemClickListener(this);
        mGridView_OpenFile.setOnItemLongClickListener(this);
        mBtn_sendFile = (Button) findViewById(R.id.btn_sendFile);
        //"smb://cj:cj@192.168.16.100/cj share";
        //urlPath = "smb://cj:cj@" + mStrIp + "/cj share";
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
                         listDir = SamFile.listDir_(fa_urlpath);//遍历当前路径
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GridViewOpenFileAdapter gridViewOpenFileAdapter = new GridViewOpenFileAdapter(OpenFileActivity.this, listDir);
                                mGridView_OpenFile.setAdapter(gridViewOpenFileAdapter);
                            }
                        });
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        mBtn_sendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType("video/*;image/*");//同时选择视频和图片
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, Constant.REQUEST_CODE);

            }
        });
    }

    /**
     * 接收来自文件activity的文件路径信息,并共享视屏
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE) {
                Uri uri = data.getData();
                if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                    mFilePath = uri.getPath();
                    return;
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    mFilePath = getPath(this, uri);
                } else {//4.4以下下系统调用方法
                    mFilePath =  getRealPathFromURI(uri);
                }
                //安卓6.0以上要用这个判断进行获取读取手机文件权限
                if (Build.VERSION.SDK_INT >= 23) {
                    int REQUEST_CODE_CONTACT = 101;
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //验证是否许可权限
                    for (String str : permissions) {
                        if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                            return;
                        }
                    }
                }
                mDialog = new ProgressDialog(OpenFileActivity.this);
                mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
                mDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
                mDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                mDialog.setIcon(R.mipmap.error);//
                mDialog.setTitle("提示");
                mDialog.setMessage("正在上传...");
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

                        SamFile.fileUpload(fa_urlpath, mFilePath);//在当前的路径与当前的文件夹上传文件
                        Log.v("dsss", "上传成功");
                        //刷新界面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    GridViewOpenFileAdapter gridViewOpenFileAdapter = null;
                                    gridViewOpenFileAdapter = new GridViewOpenFileAdapter(OpenFileActivity.this, SamFile.listDir_(fa_urlpath));
                                    mGridView_OpenFile.setAdapter(gridViewOpenFileAdapter);
                                    Toast.makeText(getApplicationContext(),"上传成功",Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }) {
                }.start();
            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (listDir.get(i)!=null)
        {
            fileName=listDir.get(i);
            if (fileName.contains("."))
            {
                view.setClickable(false);
                //Toast.makeText(this,"非文件夹不能被打开",Toast.LENGTH_SHORT).show();
            } else {
                startToOpenFileActivity();
            }
        }
    }
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (listDir.get(i)!=null)
        {
            fileName=listDir.get(i);
        }
        DeleteDialog();
        return false;
    }
    private void DeleteDialog() {
        mBuilder = new AlertDialog.Builder(this)
                .setTitle("请确认是否删除")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    new SmbFile(SamFile.returnUrl_(fa_urlpath, fileName)).delete();
                                    GridViewOpenFileAdapter gridViewOpenFileAdapter = null;
                                    gridViewOpenFileAdapter = new GridViewOpenFileAdapter(OpenFileActivity.this, SamFile.listDir_(fa_urlpath));
                                    mGridView_OpenFile.setAdapter(gridViewOpenFileAdapter);
                                    Toast.makeText(getApplicationContext(),"删除成功",Toast.LENGTH_SHORT).show();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (SmbException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mBuilder.dismiss();

                    }
                })  .create();
        mBuilder.show();
    }
    private void startToOpenFileActivity() {
        Intent intent = new Intent(mContext, OpenFileActivity.class);
        own_urlPath = SamFile.returnUrl_(fa_urlpath,fileName);//已经点击了的文件夹路径
        intent.putExtra("urlpath",own_urlPath);
        startActivity(intent);
    }
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }
    /*
    以下代码是由于安卓版本不一样而需要添加的获得手机的文件的的路径所需要的
     */
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
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
