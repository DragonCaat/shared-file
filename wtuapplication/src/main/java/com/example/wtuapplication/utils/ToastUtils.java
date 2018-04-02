package com.example.wtuapplication.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import com.example.wtuapplication.R;

/**
 * Created by 梁 on 2017/9/7.
 */

/**
 * 自定义弹出框
 * */
public class ToastUtils {
    public static void showToast(Context context,String s){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }
    public static void showDialog(Context context){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("温馨提示").setIcon(R.mipmap.error).setMessage("当前无可用网络，请确认网络后再试").create();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.mystyle);  //添加动画
        dialog.show();
    }
}
