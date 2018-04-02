package com.example.wtuapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wtuapplication.R;
import com.example.wtuapplication.bean.Constant;
import com.example.wtuapplication.utils.NetUtils;
import com.example.wtuapplication.utils.SamFile;
import com.example.wtuapplication.utils.ToastUtils;
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "MainActivity";


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int FILE_CODE = 0;

    private String mFilePath;
    private Context mContext;
    private Toolbar toolbar;
    private Button btn_sendFile;
    private DrawerLayout mDrawableLayout;
    private View headerView;
    private TextView nav_localNet;
    private NavigationView navView;
    private TextView des;
    private Button btn_createWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();//初始化控件
        initData();//初始化控件数据
        //设置toolbar栏
        setSupportActionBar(toolbar);
        //actionBar添加按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.home_to_main);
        }
    }

    //初始化控件
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleMarginStart(300);

        navView = (NavigationView) findViewById(R.id.nav_view);
        //动态添加navigation的头布局
        headerView = navView.inflateHeaderView(R.layout.nav_header);
        //通过动态添加的Menu中的headerView找到控件
        nav_localNet = (TextView) headerView.findViewById(R.id.nav_localNet);

        btn_sendFile = (Button) findViewById(R.id.btn_sendFile);
        mDrawableLayout = (DrawerLayout) findViewById(R.id.drawable_layout);

    }

    //初始化控件数据
    private void initData() {
        btn_sendFile.setOnClickListener(this);
        des = (TextView) findViewById(R.id.des);
        des.setText("" + getIpNumber());

        //menu菜单的点击事件
        navView.setCheckedItem(R.id.nav_localNet);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_localNet:
                        Intent intent = new Intent(mContext, FindNewIpComputer.class);
                        startActivity(intent);
                        ToastUtils.showToast(mContext, "点击了局域网");
                        break;
                }
                //关闭navMenu栏
                mDrawableLayout.closeDrawers();
                return true;
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        checkNetWork();
    }

    /**
     * 初始化menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toobar, menu);
        return true;
    }

    /**
     * menu中控件的点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawableLayout.openDrawer(GravityCompat.START);
                //Toast.makeText(this, "click home", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    //通过wifi获取ip
    private String getIpNumber() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }

    private String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    //检查网络
    private void checkNetWork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean networkAvailable = NetUtils.isNetworkAvailable(mContext);
                final boolean networkOnline = NetUtils.isNetworkOnline();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!networkAvailable || !networkOnline) {
                            ToastUtils.showDialog(mContext);
                        }
                    }
                });
            }
        }) {

        }.start();
    }

    //点击事件s
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sendFile:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType("video/*;image/*");//同时选择视频和图片
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, Constant.REQUEST_CODE);
                //ToastUtils.showToast(mContext, "发送文件");
                break;
            case R.id.createWeb:
                String localIpAddress = NetUtils.getLocalIpAddress();
                des.setText(" " + localIpAddress);
                break;
        }
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
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((BaseActivity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                            }
                            SamFile.fileUpload("smb://cj:cj@192.168.16.100/cj share",mFilePath);
                            Log.v("mm","上传成功");
                        }
                    }) {
                    }.start();
            }
        }
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
     * 复写父类的该方法，根据需求做处理
     */
    @Override
    public void onNetChange(int netMobile) {
        //在这做判断，根据需要做处理
        if (netMobile == -1) {
            //展示对话框
            ToastUtils.showDialog(mContext);
        } else {
        }
    }
}
