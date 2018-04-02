package com.example.wtuapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.wtuapplication.utils.NetUtils;

/**
 * Created by 梁 on 2017/9/8.
 */

/**
 * 继承该activity,即可实现实时监听网络
 * */
public class BaseActivity extends AppCompatActivity implements NetBroadcastReceiver.NetEvent {
    public static NetBroadcastReceiver.NetEvent event;
    /**
     * 网络类型
     */
    private int netMobile;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        event = this;
        inspectNet();
    }


    /**
     * 初始化时判断有没有网络
     */

    public boolean inspectNet() {
        this.netMobile = NetUtils.getNetWorkState(BaseActivity.this);
        return isNetConnect();
    }

    /**
     * 网络变化之后的类型
     */
    @Override
    public void onNetChange(int netMobile) {
        // TODO Auto-generated method stub
        this.netMobile = netMobile;
        isNetConnect();

    }

    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netMobile == 1) {
            return true;
        } else if (netMobile == 0) {
            return true;
        } else if (netMobile == -1) {
            return false;

        }
        return false;
    }
}
