package com.example.wtuapplication.activity;

/**
 * Created by 梁 on 2017/9/18.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.example.wtuapplication.utils.NetUtils;

/**
 * Created by 梁 on 2017/9/8.
 */

public class NetBroadcastReceiver extends BroadcastReceiver {

    private NetEvent netEvent = MainActivity.event;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //检查网络状态的类型
            int netWrokState = NetUtils.getNetWorkState(context);
            if (netEvent != null)
                // 接口回传网络状态的类型
                netEvent.onNetChange(netWrokState);
        }
    }
    //自定义接口
    public interface NetEvent{
        void onNetChange(int netMobile);
    }
}
