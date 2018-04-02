package com.example.wtuapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wtuapplication.R;
import com.example.wtuapplication.utils.ToastUtils;

/**
 * Created by 梁 on 2017/9/20.
 */

public class CreateNewIp extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText et_ip;
    private EditText et_userName;
    private EditText et_passWord;
    private Button btn_sure;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ip);
        initView();
        //设置toolbar栏
        setSupportActionBar(toolbar);
        //actionBar添加按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }
    }
    //初始化控件
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleMarginStart(300);


        et_ip = (EditText) findViewById(R.id.et_ip);
        et_userName = (EditText) findViewById(R.id.et_userName);
        et_passWord = (EditText) findViewById(R.id.et_passWord);
        btn_sure = (Button) findViewById(R.id.btn_sure);

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strIp = et_ip.getText().toString().trim();//trim（）去掉空格
                String strUserName = et_userName.getText().toString().trim();
                String strPassWord = et_passWord.getText().toString().trim();
                if (TextUtils.isEmpty(strIp)){
                    ToastUtils.showToast(CreateNewIp.this,"IP不可为空");
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("ip", strIp);
                    intent.putExtra("strUserName", strUserName);
                    intent.putExtra("strPassWord", strPassWord);
                    setResult(RESULT_OK, intent);
                    CreateNewIp.this.finish();
                }
            }
        });
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
